package com.ie;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

public class test {

	public static void pocline(String url) {
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("GBK");

			NodeFilter nodeFilter = new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith("tr class=\"quality\"")) {
						return true;
					}
					return false;
				}
			};

			NodeList nodes = parser.extractAllNodesThatMatch(nodeFilter);
			for (int i = 0; i < nodes.size(); i++) {
				Node tag = nodes.elementAt(i);
				System.out.println(tag.toPlainTextString().trim()
						.replace("\n", "\t"));
			}

		} catch (Exception e) {

		}
	}
	

	
	public static void main(String[] args) {
		// pocline("http://product.pconline.com.cn/mobile/samsung/517260_detail.html");

		String url = "http://item.jd.com/692918.html";
		
		for (int i = 0; i < 1; i++) {
			long s1 = System.currentTimeMillis();

			JD jd = new JD();
			jd.isCellphonePage(url);
			long s2 = System.currentTimeMillis();

			jd.catchToLocal(
					url,
					url.substring(url.lastIndexOf("/") + 1,
							url.lastIndexOf(".")));
			long s3 = System.currentTimeMillis();

			System.out.println("ÅÐ¶Ï£º" + (s2 - s1) + " |ÏÂÔØ£º" + (s3 - s2)
					+ " s | " + jd.getDownloadpath()+" "+i);
		}
		
		
				
	}

}
