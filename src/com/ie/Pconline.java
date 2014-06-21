package com.ie;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Pconline {
	
	static HashSet<String > set = new HashSet<String>();
	static HashSet<String>  phoneLink = new HashSet<String>();
	static int num = 0; 
	
	public Pconline() {
		
	}
	
	public static void CatchAllFat(String url){
		try{
			Parser parser = new Parser(url);
			parser.setEncoding("GBK");
			
			NodeFilter allfatFilter = new NodeFilter() {				
				@Override
				public boolean accept(Node node) {
					if(node.getText().matches("a href=\"/mobile/.+\" target=\"_self\" id=\".*\""))
						return true;
					else {
						return false;
					}
					
				}
			};			
			NodeList nodeList = parser.extractAllNodesThatMatch(allfatFilter);
			
			for(int i = 0; i < nodeList.size(); i++){
				Node tag = nodeList.elementAt(i);
				//System.err.println(tag.getText().substring(tag.getText().indexOf("\"/")+2,tag.getText().indexOf("/\"") ));
				set.add(tag.getText().substring(tag.getText().indexOf("\"/")+2,tag.getText().indexOf("/\"")) + "/");
			}
			
		}catch(ParserException e){
			e.printStackTrace();
		}
	}
	
	
	public static void CatchAllPhonePage(){
		try{
			for(String  url : set){
				Parser parser = new Parser("http://product.pconline.com.cn/"+url);
				parser.setEncoding("GBK");
				
				//获得该品牌有多少手机
				NodeFilter numOfFat = new NodeFilter() {					
					@Override
					public boolean accept(Node node) {
						if(node.getText().startsWith("p class=\"cur_page\""))
							return true;
						
						return false;
					}
				};
				
				String str = parser.extractAllNodesThatMatch(numOfFat).elementAt(0).getLastChild().getPreviousSibling().toPlainTextString() ;
				//System.out.println("=========http://product.pconline.com.cn/"+url+"==========");
				CathAllPhoneLink("http://product.pconline.com.cn/"+url, Integer.parseInt(str));
				
				num	+= Integer.parseInt(str);				
			}
		}catch(ParserException e){
			e.printStackTrace();
		}
	}
	
	
	public static void CathAllPhoneLink(String url,int n){	
		try {
				for(int i = 0; i < (n-1)/25 + 1; i++){
					
					String suffix = "";
					if(i > 0 ) suffix = (25*i) +"s1.shtml";
					
					Parser parser = new Parser(url + suffix);
					//System.out.println(url +suffix);					
					parser.setEncoding("GBK");
					
					NodeFilter phoneLinkFilter = new NodeFilter() {						
						@Override
						public boolean accept(Node node) {
							if(node.getText().matches("a class=\"name\".*"))
								return true;
							return false;
						}
					};
					
					NodeList nodeList = parser.extractAllNodesThatMatch(phoneLinkFilter);
					
					for(int v = 0; v < nodeList.size(); v++){
						Node tag = nodeList.elementAt(v);
						String str =tag.getText();
						str = str.substring(str.indexOf("http"),str.indexOf("html")+4);
						
						phoneLink.add(str);												
					}
					
				}
		} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}		
		
	}
	public static void main(String[] args) throws IOException{
		long s = System.currentTimeMillis();
		CatchAllFat("http://product.pconline.com.cn/mobile/");
		CatchAllPhonePage();
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("F:\\Pconline.csv")));
		for(String string : phoneLink){
			writer.write(string+"\r\n");
		}
		writer.close();
		
		System.out.println("手机总理"+num + "|" + (num-3035) + "size:" + phoneLink.size());
		System.out.println("used:"+ (System.currentTimeMillis() - s)/1000 + "s");
	}
	//手机总理6025|2990size:3035
}

/* 调用浏览器小技巧
 * Desktop desktop = Desktop.getDesktop();
		try{
			desktop.browse(new URI("www.baidu.com"));
		}catch(Exception e){
			e.printStackTrace();
		}*/

