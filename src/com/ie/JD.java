package com.ie;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

public class JD implements Runnable {

	// û�н��棬Ϊ�˵��Է��㣬����ץȡ��id������
	public int startId = 656101;
	public int endId = 656401;
	// �Ѿ��¼ܣ�656207

	// ������һЩhtml��css��ʽ(��ѡ����),��Ϊ������ʶ���׼
	private static final String breadcrumb = "div class=\"breadcrumb\"";
	private static final String tdTile = "td class=\"tdTitle\"";
	private static final String split = "&nbsp;&gt;&nbsp;";

	// ץȡ���ݴ�ŵ�Ŀ¼
	private static final String downloadPath = "����\\�ֻ���Ϣ\\";

	private static String brand = "";
	private static String version = "";
	private long lastAliveTime = 0;
	
	// ץȡ����
	Queue queue = null;
	int id;
	int terminal = 0;

	public JD() {
	}

	public JD(int startId, int endId) {
		this.startId = startId;
		this.endId = endId;
	}

	public JD(Queue queue, int id) {
		this.queue = queue;
		this.id = id;
	}

	// �ж��ǲ���Ϊ�ֻ�ҳ��
	public Boolean isCellphonePage(String url) {
		try {

			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");

			// ��Ծ�����վ���ֻ���ҳʶ��
			// �õ����о������˵ı�ǩ
			NodeList list = parser.extractAllNodesThatMatch(new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith(breadcrumb)) {
						return true;
					} else {
						return false;
					}
				}
			});

			Node tag = list.elementAt(0);

			if (tag == null) {
				System.err.println(url + ";");
				return false;
			}

			String[] categories = tag.toPlainTextString().trim().split(split);

			if (categories[0].equals("�ֻ�") && categories[2].equals("�ֻ�")) {
				brand = categories[3].substring(categories[3].indexOf(";") + 1);
				version = categories[4];
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			System.err.println("JD 86 " + url + " " + e.getMessage());
			return false;
		}
	}

	// �����ֻ���Ϣ
	public boolean catchToLocal(String url, String id) {
		try {

			FileDownLoader downLoader = new FileDownLoader();
			String price = downLoader.downloadFile(
					"http://p.3.cn/prices/get?skuid=J_" + id,
					Integer.parseInt(id));

			if (price.startsWith("-1") || price.equals(""))
				return false;// ���ޱ���֮��,�����½������

			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");

			NodeFilter tableFilter = new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith(tdTile)) {
						return true;
					}
					return false;
				}
			};

			NodeList nodeList = parser.extractAllNodesThatMatch(tableFilter);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(downloadPath + brand + "-" + version
							+ "-" + id + ".txt")));

			writer.write("price : " + price + "\r\n");

			for (int i = 0; i < nodeList.size(); i++) {
				Node tag = nodeList.elementAt(i);
				writer.write(tag.toPlainTextString() + ":"
						+ tag.getNextSibling().toPlainTextString() + "\r\n");
			}
			writer.close();
			return true;

		} catch (Exception e) {
			System.out.println("exception at catch to local");
			System.err.println("JD 132 | " + url + " " + e.getMessage());
			return false;
		}
	}

	/*
	 * //��һ�汾�Ķ��̶߳���run public void run(){ //��ȡ��ǰʱ�� try{ long s =
	 * System.currentTimeMillis();
	 * 
	 * for(int i = startId; i <endId; i++){
	 * 
	 * //������ֻ�ҳ�棬����ץȡ if(JD.isCellphonePage("http://item.jd.com/"+i+".html")){
	 * if(JD.catchToLocal("http://item.jd.com/"+i+".html",i)){
	 * System.out.println(brand+";"+version+";"+i);
	 * 
	 * //���ֻ�ID��¼��һ���ļ���,�����Ҫ���еڶ������������Դ������ʱ�� BufferedWriter writer = new
	 * BufferedWriter(new OutputStreamWriter( new
	 * FileOutputStream("F:\\test\\����\\"+"�ֻ���Ʒid.txt",true))); synchronized
	 * (writer) { writer.append(i+"\r\n"); writer.close(); }
	 * 
	 * } } } System.out.println(endId +" | "+(System.currentTimeMillis() -
	 * s)+"ms"); }catch(IOException e){ e.printStackTrace(); }
	 * 
	 * }
	 */

	public String getDownloadpath() {
		return downloadPath;
	}

	// �ڶ����汾���̶߳��ڵ�run
	public void run() {
		// ��ȡ��ǰʱ��
		long s = System.currentTimeMillis();

		File file = new File(downloadPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String i = "";

		while (true) {
			synchronized (queue) {
				if (queue.isEmpty())
					break;
				i = queue.deQueue();
				System.out.println(id + "\t| takes " + i);
				lastAliveTime = (System.currentTimeMillis() - s)/1000;
			}

			// ������ֻ�ҳ�棬����ץȡ
			try {
				if (this.isCellphonePage("http://item.jd.com/" + i + ".html")) {
					if (this.catchToLocal("http://item.jd.com/" + i + ".html",
							i)) {
						System.out.println("=========="+brand + ";" + version + ";" + i+"==========");
						// ���ֻ�ID��¼��һ���ļ���,�����Ҫ���еڶ������������Դ������ʱ��
						synchronized ("writer") {
							BufferedWriter writer = new BufferedWriter(
									new OutputStreamWriter(
											new FileOutputStream(downloadPath
													+ "�ֻ���Ʒid.txt", true)));
							writer.append(i + "\r\n");
							writer.close();
						}
					}
				}
			} catch (Exception e) {
				System.err.println("JD 189 | " + id + " " + e.getMessage());
			}
		}

		// ����Ϊ�գ��߳̽����˳�
		synchronized ("terminal") {
			System.out.println(id + "\t| " + (System.currentTimeMillis() - s)
					+ "ms | " + " " + (MulitThread.termail++) + " | queue size"+queue.getSize());
		}

	}

}