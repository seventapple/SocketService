package com.wang.common;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StringUtil {

	public static final String JSON_PATH_SEPARATOR = "/";

	public static boolean isNull(String str) {
		return !(str != null && str.length() != 0);
	}

	// json格式的字符串转换成Bean
	public static <T> T strToBean(String str, Class<T> clazz) {
		if (isNull(str)) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(str, clazz);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Bean转换成json格式字符串
	 * 
	 * @param obj    对象
	 * @param pretty 是否格式转换
	 * @return
	 */
	public static String objectToJson(Object obj, boolean pretty) {
		if (obj == null) {
			return "{}";
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (pretty) {
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			} else {
				return mapper.writeValueAsString(obj);
			}
		} catch (Exception e) {
			return "{}";
		}
	}

	/**
	 * bean -> JsonNode
	 */
	public static <T> JsonNode parseJsonNode(T obj) {
		ObjectMapper mapper = new ObjectMapper();
		if (Objects.isNull(obj))
			// need ver2.10.1
//			return mapper.nullNode();
			return null;
		return mapper.valueToTree(obj);
	}

	/**
	 * file -> ObjectBean
	 */
	public static <T> T toObject(File f, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(f, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * String -> ObjectBean
	 */
	public static <T> T toObject(String str, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(str, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * JsonNode -> ObjectBean
	 */
	public static <T> T toObject(JsonNode content, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.treeToValue(content, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 指定路径的值变更
	 */
	public static <T> JsonNode updateValue(JsonNode content, String path, T value) {
		JsonNode objNode = content.deepCopy();
		if (Objects.isNull(objNode) || Objects.isNull(value)) {
			return objNode;
		}
		String[] fields = path.split(JSON_PATH_SEPARATOR);
		JsonNode childNode = objNode;
		for (int i = 0; i < fields.length; i++) {
			if (Objects.isNull(childNode)) {
				return objNode;
			}
			String field = fields[i];
			if (childNode.isArray()) {
				if (isNumeric(field)) {
					childNode = childNode.get(Integer.valueOf(field));
				}
			} else {
				childNode = childNode.get(field);
			}
		}
		String key = fields[fields.length - 1];
		if (Objects.nonNull(childNode)) {
			if (childNode.isArray()) {
				if (isNumeric(key)) {
					int idx = Integer.valueOf(key);
					ArrayNode arrayChile = ((ArrayNode) childNode);
					if (arrayChile.has(idx)) {
						arrayChile.remove(idx);
						arrayChile.insert(idx, parseJsonNode(value));
					}
				}
			} else if (childNode.has(key)) {
				((ObjectNode) childNode).set(key, parseJsonNode(value));
			}
		}
		return objNode;
	}

	public static <T> JsonNode updateValue(String content, String path, T value) {
		JsonNode object = toObject(content, JsonNode.class);
		return updateValue(object, path, value);
	}

	/**
	 * 指定路径删除或[null]设定
	 */
	public static void removeNodeOrSetNull(JsonNode body, String parentPaht, String fieldName, boolean removeFlg) {
		// body.isEmpty()
		if (body.isNull() || isNull(fieldName)) {
			return;
		}
		if (!isNull(parentPaht)) {
			String[] fields = parentPaht.split(JSON_PATH_SEPARATOR);
			for (String field : fields) {
				if (!isNull(field)) {
					if (body.isArray()) {
						body = body.get(Integer.valueOf(field));
					} else {
						body = body.get(field);
					}
				}
			}
		}
		ObjectNode objNode = (ObjectNode) body;
		if (removeFlg) {
			objNode.remove(fieldName);
		} else {
			objNode.putNull(fieldName);
		}
	}

	/**
	 * 是否为半角数字
	 */
	public static final boolean isNumeric(String str) {
		Matcher matcher = Pattern.compile("^[0-9]+$").matcher(str);
		return matcher.find();
	}

	// 配置文件读取相关↓↓↓
	public static String getPropertyFilePath() {
		String path = "";
		try {
			String filePath = getJavaLibraryPath();
			File file = new File(filePath);
			if (file.exists()) {
				path = file.getParentFile().getCanonicalPath();
			}
		} catch (Exception exception) {
		}
		return path;
	}

	@SuppressWarnings("rawtypes")
	public static String getJavaLibraryPath() throws IOException {
		String jarName = VersionInfo.getProduct() + ".jar";
		Class className = StringUtil.class;
		if (className != null) {
			File file = getModuleFileName(className);
			if (file != null) {
				if (jarName != null && !file.getName().equals(jarName)) {
					return null;
				}
				return file.getPath();
			}
		}
		if (className != null && jarName != null) {
			URL url = getResource(jarName, className);
			if (url != null && url.getProtocol().equalsIgnoreCase("file")) {
				return new File(url.getFile()).getCanonicalPath();
			}
		}
		if (jarName != null) {
			StringTokenizer cp = new StringTokenizer(System.getProperty("java.class.path", ""), File.pathSeparator);
			while (cp.hasMoreTokens()) {
				File path = new File(cp.nextToken());
				if (path.isFile()) {
					try {
						String file = new File(path.getParent(), jarName).getCanonicalPath();
						if (file.equals(path.getCanonicalPath())) {
							return file;
						}
					} catch (Exception exception) {
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static File getModuleFileName(Class className) {
		try {
			URL url = className.getResource(new File(className.getName().replace('.', '/')).getName() + ".class");
			if (url != null) {
				URLConnection uc = url.openConnection();
				try {
					if (uc instanceof JarURLConnection) {
						return new File(((JarURLConnection) uc).getJarFile().getName()).getCanonicalFile();
					}
					String urlFile = url.getFile();
					int urlSeparatorIndex = urlFile.indexOf("!/");
					if (urlSeparatorIndex > 0) {
						urlFile = urlFile.substring(0, urlSeparatorIndex);
						if (urlFile.startsWith("file:")) {
							urlFile = urlFile.substring("file:".length());
						}
						return new File(urlFile).getCanonicalFile();
					}
				} finally {
					uc.getInputStream().close();
				}
			}
		} catch (Throwable throwable) {
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static URL getResource(String resource, Class className) {
		ClassLoader cl = className.getClassLoader();
		if (cl != null) {
			try {
				URL rv;
				if ((rv = cl.getResource(resource)) != null) {
					return rv;
				}
			} catch (Throwable throwable) {
			}
		}
		URL rv;
		if ((rv = ClassLoader.getSystemResource(resource)) != null) {
			return rv;
		}
		return className.getResource(resource);
	}
}
