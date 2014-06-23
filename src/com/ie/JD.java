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

	// 没有界面，为了调试方便，定义抓取的id上下限
	public int startId = 656101;
	public int endId = 656401;
	// 已经下架：656207

	// 下面是一些html的css样式(类选择器),作为过滤器识别标准
	private static final String breadcrumb = "div class=\"breadcrumb\"";
	private static final String tdTile = "td class=\"tdTitle\"";
	private static final String split = "&nbsp;&gt;&nbsp;";

	// 抓取数据存放的目录
	private static final String downloadPath = "京东\\手机信息\\";

	private static String brand = "";
	private static String version = "";
	private long lastAliveTime = 0;
	
	// 抓取队列
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

	// 判断是不是为手机页面
	public Boolean isCellphonePage(String url) {
		try {

			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");

			// 针对京东网站的手机网页识别
			// 得到所有经过过滤的标签
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

			if (categories[0].equals("手机") && categories[2].equals("手机")) {
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

	// 下载手机信息
	public boolean catchToLocal(String url, String id) {
		try {

			FileDownLoader downLoader = new FileDownLoader();
			String price = downLoader.downloadFile(
					"http://p.3.cn/prices/get?skuid=J_" + id,
					Integer.parseInt(id));

			if (price.startsWith("-1") || price.equals(""))
				return false;// 暂无报价之类,或者下降的情况

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
	 * //第一版本的多线程对于run public void run(){ //获取当前时间 try{ long s =
	 * System.currentTimeMillis();
	 * 
	 * for(int i = startId; i <endId; i++){
	 * 
	 * //如果是手机页面，进行抓取 if(JD.isCellphonePage("http://item.jd.com/"+i+".html")){
	 * if(JD.catchToLocal("http://item.jd.com/"+i+".html",i)){
	 * System.out.println(brand+";"+version+";"+i);
	 * 
	 * //把手机ID记录到一个文件中,如果需要进行第二次搜索将可以大大缩短时间 BufferedWriter writer = new
	 * BufferedWriter(new OutputStreamWriter( new
	 * FileOutputStream("F:\\test\\京东\\"+"手机商品id.txt",true))); synchronized
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

	// 第二个版本多线程对于的run
	public void run() {
		// 获取当前时间
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

			// 如果是手机页面，进行抓取
			try {
				if (this.isCellphonePage("http://item.jd.com/" + i + ".html")) {
					if (this.catchToLocal("http://item.jd.com/" + i + ".html",
							i)) {
						System.out.println("=========="+brand + ";" + version + ";" + i+"==========");
						// 把手机ID记录到一个文件中,如果需要进行第二次搜索将可以大大缩短时间
						synchronized ("writer") {
							BufferedWriter writer = new BufferedWriter(
									new OutputStreamWriter(
											new FileOutputStream(downloadPath
													+ "手机商品id.txt", true)));
							writer.append(i + "\r\n");
							writer.close();
						}
					}
				}
			} catch (Exception e) {
				System.err.println("JD 189 | " + id + " " + e.getMessage());
			}
		}

		// 队列为空，线程结束退出
		synchronized ("terminal") {
			System.out.println(id + "\t| " + (System.currentTimeMillis() - s)
					+ "ms | " + " " + (MulitThread.termail++) + " | queue size"+queue.getSize());
		}

	}

}