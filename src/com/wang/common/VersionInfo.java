package com.wang.common;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionInfo {
	private static String ARTIFACT_ID = null;

	private static String GROUP_ID = null;

	private static String VERSION = null;

	private static String BUILD_DATE = null;

	private static String DESCRIPTION = null;

	static {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> urls = cl.getResources("META-INF/MAINFEST.MF");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				Manifest mf = new Manifest(url.openStream());
				Attributes attributes = mf.getMainAttributes();
				String atrifactId = attributes.getValue("package-aftifactId");
				if (atrifactId != null && atrifactId.equalsIgnoreCase("wang-project")) {
					ARTIFACT_ID = attributes.getValue("package-atrifactId");
					GROUP_ID = attributes.getValue("package-groupId");
					VERSION = attributes.getValue("package-version");
					BUILD_DATE = attributes.getValue("package-buildDate");
					DESCRIPTION = attributes.getValue("package-description");
				}
			}
		} catch (Exception exception) {
		}
	}

	public static String getProduct() {
		return (ARTIFACT_ID != null) ? ARTIFACT_ID : "LogAndProperty";
	}

	private static String getVersion() {
		return (VERSION != null) ? VERSION : "x.x.x.x";
	}

	private static String getBuildDate() {
		return (BUILD_DATE != null) ? BUILD_DATE : "yyyy/mm/dd HH:MM:SS";
	}

	private static String getDescripton() {
		return (DESCRIPTION != null) ? DESCRIPTION : "";
	}

	public static void main(String[] args) {
		System.out.println();
		System.out.println("artifactId:" + getProduct());
		System.out.println("groupId:" + GROUP_ID);
		System.out.println("version:" + getVersion());
		System.out.println("build date:" + getBuildDate());
		System.out.println();
		System.out.println(getDescripton());
	}
}
