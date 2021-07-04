package com.wang.exception;

import com.wang.common.LogMessageDictionary;

public class CtrFileException extends Exception {
	private static final long serialVersionUID = 1L;

	protected String code;

	public CtrFileException(String code, Object... params) {
		super(String.valueOf(code) + ":" + LogMessageDictionary.getMsg(code, params));
		this.code = code;
	}

	public CtrFileException() {
	}

	public CtrFileException(String param) {
		super(param);
	}

	public CtrFileException(String param, Throwable cause) {
		super(param, cause);
	}

	public CtrFileException(Throwable cause) {
		super(cause);
	}

	public CtrFileException(String code, Throwable cause, Object... params) {
		super(String.valueOf(code) + ":" + LogMessageDictionary.getMsg(code, params), cause);
		this.code = code;
	}

}
