package com.wang.startup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.wang.common.StringUtil;
import com.wang.model.RequestInfo;

public class Client {
	private static Socket client;
	private static DataInputStream dis;
	private static DataOutputStream dos;

	public static void main(String[] args) {
//		add();
		save();
	}

	public static void add() {
		try (FileInputStream fis = new FileInputStream("E:/TestFile/111.txt")) {
//			client = new Socket("127.0.0.1", 17005);
			client = new Socket("192.168.101.129", 17005);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			// msg
			RequestInfo info = new RequestInfo();
			info.setMethod("add");
//			info.setPath("E:\\001Git\\BatRun\\LogAndProperty\\temp");
			info.setPath("/home/wang/test/LogAndProperty/file");
			info.setName("2.txt");
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

	public static void save() {
		try {
			client = new Socket("127.0.0.1", 11253);
//			client = new Socket("192.168.101.129", 17005);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			// msg
			RequestInfo info = new RequestInfo();
			info.setMethod("save");
			String msgStr = StringUtil.objectToJson(info, false);
			byte[] bytes = msgStr.getBytes();
			// length
			dos.writeInt(bytes.length);
			dos.write(bytes);
			dos.flush();
//			//method-1
//			dos.write((URLEncoder.encode("123,\r\na,あ", "UTF-8")+"\r\n").getBytes());
//			dos.write((URLEncoder.encode("123,b,い", "UTF-8")+"\r\n").getBytes());
//			dos.write((URLEncoder.encode("123,c,う", "UTF-8")+"\r\n").getBytes());
//			dos.write((URLEncoder.encode("123,d,え", "UTF-8")+"\r\n").getBytes());
			List<String> msg = new ArrayList<String>();
			msg.add("123,a\r\n,あ");
			msg.add("123,b,い" );
			msg.add("123,c,う");
			msg.add("123,d,え");
			String fileStr = StringUtil.objectToJson(msg, false);
			dos.write(fileStr.getBytes());
			dos.flush();
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
