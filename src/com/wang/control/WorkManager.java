package com.wang.control;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.wang.common.Constants;
import com.wang.thread.ExecuteThread;
import com.wang.thread.Monitor;

//生产者消费者管理器
public class WorkManager {

	private static volatile WorkManager _instance = new WorkManager();

	// pool
	private BlockingQueue<Socket> queue = new LinkedBlockingQueue<Socket>();

	// 消费者队列
	private List<ExecuteThread> execThreadList = new ArrayList<ExecuteThread>();

	// 消费者管理器
	private ExecutorService executorService;

	// 生产者
	private Monitor monitor;

	public static WorkManager getInstance() {
		return _instance;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	private void threadSetting(int cnt) {
		for (int threadNo = 1; threadNo <= cnt; threadNo++) {
			execThreadList.add(new ExecuteThread());
		}
	}

	/**
	 * 启动生产者消费者
	 */
	public String init(int cnt) {
		executorService = Executors.newFixedThreadPool(cnt);

		String[] monitorRet = new String[2];
		// 生产者启动
		monitor = new Monitor(monitorRet);
		new Thread(monitor).start();

		while (monitorRet[0] == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				;
			}
		}
		if (monitorRet[0] != Constants.RESULT_SUCCESS) {
			return Constants.RESULT_ERROR;
		}

		threadSetting(cnt);
		// 消费者线程启动
		for (ExecuteThread thread : execThreadList) {
			executorService.submit(thread);
		}
		return Constants.RESULT_SUCCESS;
	}

	/**
	 * 任务入列
	 */
	public void pushQueue(Socket socket) throws InterruptedException {
		if (!queue.contains(socket)) {
			queue.put(socket);
		}
	}

	/**
	 * 任务出列
	 */
	public Socket popQueue() throws InterruptedException {
		return queue.take();
	}

	/**
	 * 关闭所有线程
	 */
	public void stop() {
		if (monitor != null) {
			monitor.stop();
		}
		if (executorService != null) {
			executorService.shutdown();
			for (ExecuteThread thread : execThreadList) {
				thread.currentInterrupt();
			}
		}
	}
}
