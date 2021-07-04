package com.wang.common;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class StringUtil {
	public static boolean isNull(String str) {
		return !(str != null && str.length() != 0);
	}

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
