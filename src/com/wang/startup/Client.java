package com.wang.startup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import com.wang.common.StringUtil;
import com.wang.model.RequestInfo;

public class Client {
	private static Socket client;
	private static DataInputStream dis;
	private static DataOutputStream dos;

	public static void main(String[] args) {
		add();
	}

	public static void add() {
		try (FileInputStream fis = new FileInputStream("E:/TestFile/111.txt")) {
			client = new Socket("localhost", 17005);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			// msg
			RequestInfo info = new RequestInfo();
			info.setMethod("add");
			info.setPath("E:/TestFile/1/3/");
			info.setName("0709.txt");
			String msgStr = StringUtil.objectToJson(info, false);
			byte[] bytes = msgStr.getBytes();
			// length
			dos.writeInt(bytes.length);
			dos.write(bytes);
			byte[] data = new byte[1024];
			int len;
			while ((len = fis.read(data)) != -1) {
				dos.write(data, 0, len);
			}
			System.out.println("close client's os.");
			client.shutdownOutput();
			// return msg
			boolean isFinish = false;
			while (!isFinish) {
				String ret = dis.readUTF();
				System.out.println(ret);
				if ("200".equals(ret)) {
					isFinish = true;
					System.out.println("success");
				} else if ("400".equals(ret)) {
					isFinish = true;
					System.out.println("failed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
			}
		}
	}

}
