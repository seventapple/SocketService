package com.wang.startup;

import java.util.Scanner;

import com.wang.common.Constants;
import com.wang.common.PropertiesLoader;
import com.wang.control.WorkManager;
import com.wang.exception.CtrFileException;
import com.wang.log.LogManager;

public class StartUp {

	private static LogManager LOG = LogManager.getLogManager(StartUp.class);

	private boolean isRunning = true;

	// 入口
	public static void main(String[] args) {
		StartUp startUp = new StartUp();
		startUp.start();
	}

	public void start() {
		LOG.workStart("PROCESS_NAME");
		try (Scanner sc = new Scanner(System.in)) {
			WorkManager.getInstance().init(getThreadCount());
			String msg = null;
			while (isRunning) {
				msg = sc.nextLine();
				if ("exit".equalsIgnoreCase(msg)) {
					isRunning = false;
					WorkManager.getInstance().stop();
				} else {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			LOG.workNormalEnd("PROCESS_NAME");
		}
	}

	public int getThreadCount() {
		int cnt;
		try {
			PropertiesLoader loader = new PropertiesLoader(Constants.PROPERTIES_FILE);
			cnt = loader.getProperty("threadCount", 5);
		} catch (CtrFileException e) {
			cnt = 11253;
		}
		return cnt;
	}
}
