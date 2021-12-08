package com.wang.enumertion;

public enum EXECUTE_TYPE {
	ADD("ADD"), DELETE("DELETE"), SAVE("SAVE");
	private String value;

	EXECUTE_TYPE(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean equals(String arg) {
		return this.value.equalsIgnoreCase(arg);
	}

	public static boolean isFile(String param) {
		if (ADD.equals(param) || SAVE.equals(param)) {
			return true;
		}
		return false;
	}
}
