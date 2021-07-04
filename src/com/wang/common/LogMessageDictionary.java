package com.wang.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LogMessageDictionary {
	public static final String LOG_MESSAGES = "resources.message";

	private Map<String, MessageBundle> bundles = new HashMap<>();

	private static LogMessageDictionary dictionary = null;

	private LogMessageDictionary() {
		load("resources.message");
	}

	public static LogMessageDictionary getInstance() {
		if (dictionary == null)
			dictionary = new LogMessageDictionary();
		return dictionary;
	}

	public void load(String[] files) {
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = files).length, b = 0; b < i;) {
			String file = arrayOfString[b];
			load(file);
			b++;
		}
	}

	public MessageBundle getMessageBundle(String file) {
		return this.bundles.get(file);
	}

	public void remove(String file) {
		if (this.bundles.containsKey(file))
			this.bundles.remove(file);
	}

	public static String getMsg(String id) {
		String msg = getInstance().findMsg(id);
		if (msg == null)
			throw new RuntimeException(getParam("Failed to get message", id));
		return msg;
	}

	public static String getMsg(String id, Object... args) {
		ArrayList<Object> list = new ArrayList<Object>();
		if (args.length > 0) {
			byte b;
			int i;
			Object[] arrayOfObject;
			for (i = (arrayOfObject = args).length, b = 0; b < i;) {
				Object obj = arrayOfObject[b];
				if (obj instanceof Integer) {
					list.add(String.valueOf(obj));
				} else {
					list.add(obj);
				}
				b++;
			}
		}
		return MessageFormat.format(getMsg(id), list.toArray());
	}

	private Collection<MessageBundle> getBundles() {
		return this.bundles.values();
	}

	private String findMsg(String id) {
		Collection<MessageBundle> mbs = getBundles();
		for (MessageBundle mb : mbs) {
			try {
				String msg = mb.getMessage(id);
				if (!StringUtil.isNull(msg))
					return msg;
			} catch (Throwable throwable) {
			}
		}
		return null;
	}

	public static String getParam(Object... params) {
		StringBuilder sb = new StringBuilder();
		if (params == null)
			return sb.toString();
		sb.append("[");
		for (int i = 0; i < params.length; i += 2) {
			if (params.length > i)
				sb.append(params[i]);
			sb.append(":");
			if (params.length > i + 1)
				sb.append(params[i + 1]);
			if (params.length > i + 2)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public static String getParam(String msg, String param) {
		StringBuilder sb = new StringBuilder();
		sb.append(msg);
		sb.append("[");
		sb.append(param);
		sb.append("]");
		return sb.toString();
	}

	public void load(String file) {
		this.bundles.put(file, new DefaultBundle(file));
	}

	class DefaultBundle extends MessageBundle {
		public DefaultBundle(String file) {
			super(file);
		}
	}
}
