package com.wang.thread;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Socket维持通信用线程 (心跳模式)
 * 
 * @author 王李点儿
 *
 */
public class SocketAlive implements Runnable {
	private long keepAliveDelay = 5000;
	private DataOutputStream dos;
	private boolean isRunning = true;
	private long lastReceiveTime = getTime();

	public SocketAlive(DataOutputStream dos) {
		this.dos = dos;
	}

	@Override
	public void run() {
		while (isRunning) {
			if (getTime() - lastReceiveTime > keepAliveDelay) {
				try {
					dos.writeUTF("continue");
					dos.flush();
				} catch (IOException e) {
					isRunning = false;
				}
			} else {
				try {
					Thread.sleep(keepAliveDelay);
				} catch (InterruptedException e) {
					isRunning = false;
				}
			}
		}
	}

	public void stop() {
		isRunning = false;
	}

	private long getTime() {
		return System.currentTimeMillis();
	}

}
