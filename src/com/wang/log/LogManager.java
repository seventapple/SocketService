package com.wang.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;

import com.wang.common.Constants;
import com.wang.common.LogMessageDictionary;
import com.wang.common.StringUtil;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

public class LogManager {
	private static Logger logger;

	private Class<?> c;

	static {
		try {
			String xmlName = Constants.XML_LOG_PROPERTIES_FILE;
			String basePath = StringUtil.getPropertyFilePath();
			// (For eclipse run) basePath = "src";
			File xmlFile = Paths.get(basePath, xmlName).toFile();
			// System.out.println("LogXmlFile:" + xmlFile.getCanonicalPath());
			if (!xmlFile.exists() || !xmlFile.isFile() || !xmlFile.canRead()) {
				throw new IOException("");
			} else {
				LoggerContext logbackContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				JoranConfigurator joranConfig = new JoranConfigurator();
				joranConfig.setContext(logbackContext);
				logbackContext.reset();
				joranConfig.doConfigure(xmlFile);
				StatusPrinter.printInCaseOfErrorsOrWarnings(logbackContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static LogManager getLogManager(Class<?> c) {
		return new LogManager(c);
	}

	private LogManager(Class<?> c) {
		this.c = c;
		getLogger();
	}

	private synchronized Logger getLogger() {
		if (logger == null)
			logger = createLogger();
		return logger;
	}

	private Logger createLogger() {
		logger = LoggerFactory.getLogger(this.c);
		return logger;
	}

	private void setParameter(Level level) {
		if (Level.INFO == level || Level.WARN == level) {
			MDC.put("class", Constants.STRING_EMPTY);
		} else {
			MDC.put("class", Constants.SPACE + this.c.getName());
		}
		MDC.put("thread_name", Thread.currentThread().getName());
	}

	public void debug(String msg) {
		if (logger == null)
			return;
		if (logger.isDebugEnabled()) {
			setParameter(Level.DEBUG);
			logger.debug(msg);
		}
	}

	public boolean isDebugEnabled() {
		if (logger == null)
			return false;
		return logger.isDebugEnabled();
	}

	public void debug(String id, Object... args) {
		debug(LogMessageDictionary.getMsg(id, args));
	}

	public void debug(String id, String args, boolean keyFlg) {
		if (keyFlg)
			args = LogMessageDictionary.getMsg(args);
		debug(LogMessageDictionary.getMsg(id, new Object[] { args }));
	}

	public void info(String msg) {
		if (logger == null)
			return;
		if (logger.isInfoEnabled()) {
			setParameter(Level.INFO);
			logger.info(msg);
		}
	}

	public void info(String id, Object... args) {
		info(LogMessageDictionary.getMsg(id, args));
	}

	public void info(String id, String args, boolean keyFlg) {
		if (keyFlg)
			args = LogMessageDictionary.getMsg(args);
		info(LogMessageDictionary.getMsg(id, new Object[] { args }));
	}

	public void warn(String id, Object... args) {
		if (logger == null)
			return;
		if (logger.isWarnEnabled()) {
			setParameter(Level.WARN);
			logger.warn(LogMessageDictionary.getMsg(id, args));
		}
	}

	public void outputWarn(String msg) {
		if (logger == null)
			return;
		if (logger.isWarnEnabled()) {
			setParameter(Level.WARN);
			logger.warn(msg);
		}
	}

	public void error(String id, Throwable e, Object... args) {
		if (logger == null)
			return;
		if (logger.isErrorEnabled()) {
			setParameter(Level.ERROR);
			logger.error(String.valueOf(id) + ":" + LogMessageDictionary.getMsg(id, args), e);
		}
	}

	public void error(String msg) {
		if (logger == null)
			return;
		if (logger.isErrorEnabled()) {
			setParameter(Level.ERROR);
			logger.error(msg);
		}
	}

	public void error(Throwable e) {
		if (logger == null)
			return;
		if (logger.isErrorEnabled()) {
			setParameter(Level.ERROR);
			logger.error(e.getMessage(), e);
		}
	}

	public void methodStart(String methodName) {
		debug("D0000001", methodName);
	}

	public void methodEnd(String methodName) {
		debug("D0000002", methodName);
	}

	public void workStart(String methodName) {
		info("I0000001", methodName, true);
	}

	public void workNormalEnd(String methodName) {
		info("I0000002", methodName, true);
	}

	public void workErrorEnd(String methodName) {
		info("I0000002", methodName, true);
	}
}
