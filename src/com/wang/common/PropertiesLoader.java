package com.wang.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.StringTokenizer;

import com.wang.exception.CtrFileException;

public class PropertiesLoader {
	private Properties properties = new Properties();

	public PropertiesLoader(String propertyFile) throws CtrFileException {
		try {
			String jarFilePath = getJavaLibraryPath(VersionInfo.getProduct() + ".jar", PropertiesLoader.class);
			// (For eclipse run)jarFilePath = "src/resources";
			// System.out.println("PropertyFilePath:" + jarFilePath);
			File tmp = null;
			if (!StringUtil.isNull(jarFilePath)) {
				String location = (new File(jarFilePath)).getParent();
				tmp = Paths.get(location, propertyFile).toFile();
			}
			if (tmp != null && tmp.exists()) {
				try (InputStreamReader inReader = new InputStreamReader(new FileInputStream(tmp),
						Constants.ENCOD_UTF8)) {
					properties.load(inReader);
				} catch (Exception exception) {
				}
			} else {
				throw new CtrFileException(Constants.ERROR_PROPERTY_NOT_EXI, "");
			}
		} catch (IOException e) {
			throw new CtrFileException(e);
		}
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		String value = this.properties.getProperty(key);
		if (StringUtil.isNull(value))
			return defaultValue;
		return value;
	}

	public int getProperty(String key, int defaultValue) {
		int ret = defaultValue;
		String value = this.properties.getProperty(key);
		if (StringUtil.isNull(value))
			return ret;
		try {
			ret = Integer.valueOf(value).intValue();
		} catch (Exception e) {
			return defaultValue;
		}
		return ret;
	}

	private static String getJavaLibraryPath(String jarName, Class className) throws IOException {
		if (className != null) {
			File file = getModuleFileName(className);
			if (file != null) {
				if (jarName != null && !file.getName().equals(jarName))
					return null;
				return file.getPath();
			}
		}
		if (className != null && jarName != null) {
			URL url = getResource(jarName, className);
			if (url != null && url.getProtocol().equalsIgnoreCase("file"))
				return (new File(url.getFile())).getCanonicalPath();
		}
		if (jarName != null) {
			StringTokenizer cp = new StringTokenizer(System.getProperty("java.class.path", ""), File.pathSeparator);
			while (cp.hasMoreTokens()) {
				File path = new File(cp.nextToken());
				if (path.isFile())
					try {
						String file = (new File(path.getParent(), jarName)).getCanonicalPath();
						if (file.equals(path.getCanonicalPath()))
							return file;
					} catch (Exception exception) {
					}
			}
		}
		return null;
	}

	private static File getModuleFileName(Class className) {
		try {
			URL url = className.getResource(
					String.valueOf((new File(className.getName().replace('.', '/'))).getName()) + ".class");
			if (url != null) {
				URLConnection uc = url.openConnection();
				try {
					if (uc instanceof JarURLConnection)
						return (new File(((JarURLConnection) uc).getJarFile().getName())).getCanonicalFile();
					String urlFile = url.getFile();
					int urlSeparatorIndex = urlFile.indexOf("!/");
					if (urlSeparatorIndex > 0) {
						urlFile = urlFile.substring(0, urlSeparatorIndex);
						if (urlFile.startsWith("file:"))
							urlFile = urlFile.substring("file:".length());
						return (new File(urlFile)).getCanonicalFile();
					}
				} finally {
					uc.getInputStream().close();
				}
			}
		} catch (Throwable throwable) {
		}
		return null;
	}

	private static URL getResource(String resource, Class className) {
		ClassLoader cl = className.getClassLoader();
		if (cl != null)
			try {
				URL uRL;
				if ((uRL = cl.getResource(resource)) != null)
					return uRL;
			} catch (Throwable throwable) {
			}
		URL rv;
		if ((rv = ClassLoader.getSystemResource(resource)) != null)
			return rv;
		return className.getResource(resource);
	}
}
