package com.ie;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

public class test {

	public static  void pocline(String url){
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("GBK");
			
			NodeFilter nodeFilter = new NodeFilter() {							
				public boolean accept(Node node) {
						if(node.getText().startsWith("tr class=\"quality\"")){
							return true;
						}
					return false;
				}
			};
			
			NodeList nodes = parser.extractAllNodesThatMatch(nodeFilter);
			for(int i = 0; i < nodes.size(); i++){
				Node tag = nodes.elementAt(i);
				System.out.println(tag.toPlainTextString().trim().replace("\n", "\t"));
			}
			
		} catch (Exception e) {
 
		} 
	}
	public static void main(String[] args) {
		// TODO Auto-generated constructor stub						
		pocline("http://product.pconline.com.cn/mobile/samsung/517260_detail.html");	
	}

}
