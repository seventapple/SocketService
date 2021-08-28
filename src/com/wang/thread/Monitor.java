package com.wang.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.wang.common.Constants;
import com.wang.common.PropertiesLoader;
import com.wang.control.WorkManager;
import com.wang.exception.CtrFileException;
import com.wang.log.LogManager;

//监视器
public class Monitor implements Runnable {

	private static LogManager LOG = LogManager.getLogManager(Monitor.class);

	private boolean isRunning = true;

	private ServerSocket monitor;

	private String[] param;

	public Monitor(String[] param) {
		this.param = param;
	}

	@Override
	public void run() {
		exec();
	}

	private void exec() {
		LOG.workStart("SOCKET_MONITOR");
		try {
			monitor = new ServerSocket(getPort());
			param[0] = Constants.RESULT_SUCCESS;
			while (isRunning) {
				Socket accept = null;
				try {
					accept = monitor.accept();
				} catch (Exception e) {
					if (!isRunning) {
						continue;
					}
				}
				WorkManager.getInstance().pushQueue(accept);
			}
			LOG.workNormalEnd("SOCKET_MONITOR");
		} catch (Throwable e) {
			LOG.workErrorEnd("SOCKET_MONITOR");
			LOG.error(Constants.ERROR_SYSTEM, e);
			param[0] = Constants.RESULT_ERROR;
			if (!Thread.currentThread().isInterrupted()) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void stop() {
		isRunning = false;
		if (monitor != null) {
			try {
				monitor.close();
			} catch (IOException e) {
				;
			}
		}
	}

	public int getPort() {
		int port;
		try {
			PropertiesLoader loader = new PropertiesLoader(Constants.PROPERTIES_FILE);
			port = loader.getProperty("port", 11253);
		} catch (CtrFileException e) {
			port = 11253;
		}
		LOG.debug("port : " + port);
		return port;
	}
}
