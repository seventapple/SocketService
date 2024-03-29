package com.wang.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread.State;
import java.net.Socket;
import java.net.URLDecoder;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wang.common.Constants;
import com.wang.common.StringUtil;
import com.wang.control.WorkManager;
import com.wang.enumertion.EXECUTE_TYPE;
import com.wang.log.LogManager;
import com.wang.model.RequestInfo;

public class ExecuteThread implements Runnable {
	private static LogManager LOG = LogManager.getLogManager(ExecuteThread.class);

	private boolean isRunning = true;

	private Socket socket;

	private DataInputStream dis;

	private FileOutputStream fos;

	private DataOutputStream dos;

	private Thread currentThread;

	private Thread keepAliveThread;

	private SocketAlive keepSocketAlive;

	@Override
	public void run() {
		currentThread = Thread.currentThread();
		while (isRunning) {
			RequestInfo info = null;
			boolean resultError = true;
			try {
				try {
					socket = WorkManager.getInstance().popQueue();
				} catch (InterruptedException e) {
					if (!isRunning) {
						continue;
					}
				}
				LOG.workStart("SOCKET_JOB");
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				// 维持通信线程启动
				keepSocketAlive = new SocketAlive(dos);
				keepAliveThread = new Thread(keepSocketAlive);
				keepAliveThread.start();
				info = getRequestInfo();
				if (info == null) {
					LOG.error("info format error");
					continue;
				}
				if (EXECUTE_TYPE.ADD.equals(info.getMethod())) {
					doAdd(info);
				} else if (EXECUTE_TYPE.DELETE.equals(info.getMethod())) {
					doDelete(info);
				} else if (EXECUTE_TYPE.SAVE.equals(info.getMethod())) {
					doSave(info);
				} else {
					LOG.error("input method error");
					continue;
				}
				resultError = false;
			} catch (Exception e) {
				LOG.error(Constants.ERROR_SYSTEM, e);
			} finally {
				// 维持通信线程关闭
				if (keepSocketAlive != null) {
					keepSocketAlive.stop();
					while (!State.TERMINATED.equals(keepAliveThread.getState())) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							;
						}
					}
				}
				if (resultError) {
					sendResult("400");
					LOG.workErrorEnd("SOCKET_JOB");
				} else {
					sendResult("200");
					LOG.workNormalEnd("SOCKET_JOB");
				}
				// 系统关闭时,仍执行中的线程关闭
				if (!isRunning) {
					Thread.interrupted();
				}
				// 关闭流
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						;
					}
					fos = null;
				}
				if (dos != null) {
					try {
						dos.close();
					} catch (IOException e) {
						;
					}
					dos = null;
				}
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						;
					}
					dis = null;
				}
				try {
					socket.close();
				} catch (IOException e) {
					;
				}
			}
		}
	}

	private void doSave(RequestInfo info) throws Exception {
		String methodName = "doSave";
		LOG.methodStart(methodName);
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("E:/TestFile/IO/1.txt"), "utf-8"));) {
//			// method-1
//			byte[] data = new byte[128];
//			int len;
//			while ((len = dis.read(data)) != -1) {
//				bw.write(URLDecoder.decode(new String(data, 0, len), "UTF-8"));
//			}
//			bw.flush();
			StringBuilder strb = new StringBuilder();
			byte[] data = new byte[128];
			int len;
			while ((len = dis.read(data)) != -1) {
				strb.append(new String(data, 0, len));
			}
			String[] msgs = StringUtil.strToBean(strb.toString(), String[].class);
			for (String msg : msgs) {
				bw.write(msg);
				bw.newLine();
			}
		} finally {
			LOG.methodEnd(methodName);
		}
	}

	private void doAdd(RequestInfo info) throws Exception {
		String methodName = "doAdd";
		LOG.methodStart(methodName);
		try {
			File file = new File(info.getPath());
			if (!file.exists()) {
				file.mkdirs();
			}
			fos = new FileOutputStream(new File(info.getPath(), info.getName()).getCanonicalFile());
			byte[] data = new byte[1024];
			int len;
			while ((len = dis.read(data)) != -1) {
				fos.write(data, 0, len);
			}
			fos.flush();
		} finally {
			LOG.methodEnd(methodName);
		}
	}

	private void doDelete(RequestInfo info) {
		String methodName = "doDelete";
		LOG.methodStart(methodName);
		File file = new File(info.getPath(), info.getName());
		file.delete();
		LOG.methodEnd(methodName);
	}

	private RequestInfo getRequestInfo() throws IOException {
		int size = dis.readInt();
		if (size > 100 * 1024 * 1024) {
			return null;
		}
		StringBuilder strb = new StringBuilder();
		byte[] data = new byte[128];
		for (int i = 0; i < Math.floor(size / 128); i++) {
			dis.read(data);
			strb.append(new String(data, 0, 128));
		}
		int extraSize = size % 128;
		if (extraSize > 0) {
			dis.read(data, 0, extraSize);
			strb.append(new String(data, 0, extraSize));
		}
		return StringUtil.strToBean(strb.toString(), RequestInfo.class);
	}

	private void sendResult(String msg) {
		try {
			dos.writeUTF(msg);
			dos.flush();
		} catch (IOException e) {
			;
		}
	}

	public void currentInterrupt() {
		isRunning = false;
		if (currentThread != null) {
			if (State.WAITING.equals(currentThread.getState())) {
				currentThread.interrupt();
			}
		}
	}

}
