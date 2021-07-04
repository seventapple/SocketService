package com.wang.startup;

import com.wang.common.Constants;
import com.wang.common.PropertiesLoader;
import com.wang.exception.CtrFileException;
import com.wang.log.LogManager;

public class StartUp {

	private static LogManager LOG = LogManager.getLogManager(StartUp.class);
//	private static Logger LOG2 = LoggerFactory.getLogger(StartUp.class);

	public static void main(String[] args) {
		LOG.workStart("PROCESS_NAME");
		try {
			PropertiesLoader loader = new PropertiesLoader(Constants.PROPERTIES_FILE);
			String property = loader.getProperty("dir");
			System.out.println("dir : " + property);
			LOG.debug("dir : " + property);
		} catch (CtrFileException e) {
			e.printStackTrace();
			LOG.workErrorEnd("PROCESS_NAME");
		}
		LOG.workNormalEnd("PROCESS_NAME");
	}

}
