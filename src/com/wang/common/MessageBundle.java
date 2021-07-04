package com.wang.common;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageBundle {
	private ResourceBundle resource = null;

	public MessageBundle(String file) {
		Locale locale = Locale.getDefault();
		// (set locale en)
		locale = Locale.ENGLISH;
		if (locale == null) {
			this.resource = ResourceBundle.getBundle(file);
			return;
		}
		this.resource = ResourceBundle.getBundle(file, locale);
	}

	public MessageBundle(String file, String locale) {
		if (StringUtil.isNull(locale)) {
			this.resource = ResourceBundle.getBundle(file);
			return;
		}
		this.resource = ResourceBundle.getBundle(file, Locale.forLanguageTag(locale));
	}

	public String getMessage(String id) {
		return this.resource.getString(id);
	}

	public String getMessage(String id, Object... args) {
		return MessageFormat.format(getMessage(id), args);
	}
}
