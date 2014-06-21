package com.ie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class FileDownLoader {
	
	
	/**保存网页字节数组到本地文件
	 * filePath 为要保存的文件的相对地址
	 */
	private void saveToLocal(byte[] data,String filePath)
	{
		try {
			DataOutputStream out=new DataOutputStream(
			new FileOutputStream(new File(filePath)));
			for(int i=0;i<data.length;i++)
			out.write(data[i]);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	/*下载 url 指向的网页*/
	public String  downloadFile(String url,int id)
	{		  
		  /* 1.生成 HttpClinet 对象并设置参数*/
		  HttpClient httpClient=new HttpClient();
		  //设置 Http 连接超时 5s
		  httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		  
		  /*2.生成 GetMethod 对象并设置参数*/
		  GetMethod getMethod=new GetMethod(url);	 
		  //设置 get 请求超时 5s
		  getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,5000);
		  //设置请求重试处理
		  getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			new DefaultHttpMethodRetryHandler());
		  
		  /*3.执行 HTTP GET 请求*/
		  try{ 
			  int statusCode = httpClient.executeMethod(getMethod);
			  //判断访问的状态码
			  if (statusCode != HttpStatus.SC_OK) 
			  {
				  System.err.println("Method failed: "+ getMethod.getStatusLine());				 
			  }			    			  
			  
			  /*4.处理 HTTP 响应内容*/
//			  byte[] responseBody = getMethod.getResponseBody();//读取为字节数组			 
			  			  			  			 
//			  saveToLocal(responseBody,filePath);//byte[]
//			  saveToLocal(responseBodyAsStream, filePath);//inputStream
			  
//			  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("F:\\a.txt")));			 
//			  while(true){
//				  String string = reader.readLine();
//				  if(string == null) break;
//				  writer.write(string);
//				  System.out.println(string);
//				  
//			  }
			  
			  InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();//读取流
			  BufferedReader reader = new BufferedReader(new InputStreamReader(responseBodyAsStream));
			  
			  String string = reader.readLine();			  
			  responseBodyAsStream.close();
			  reader.close();
//			  writer.close();
			  
			  return string.substring(string.indexOf("\"p\":\"")+5,string.indexOf(",\"m\":")-1);
			  
		  } catch (HttpException e) {
				   // 发生致命的异常，可能是协议不对或者返回的内容有问题
				   System.out.println("Please check your provided http address!");
				   System.err.println("File download 95 "+id+" "+e.getMessage());
				  } catch (IOException e) {
				   // 发生网络异常
				   System.err.println("File download 98 "+id+" "+e.getMessage());
				  } finally {
				   // 释放连接
				   getMethod.releaseConnection();		   
				  }
				  return "";
	}
	//测试的 main 方法
	public static void main(String[]args)
	{
		long s = System.currentTimeMillis();			
		FileDownLoader downLoader = new FileDownLoader();
		String get = downLoader.downloadFile("http://p.3.cn/prices/get?skuid=J_657899",1);
		System.out.println(get.equals("-1"));
		System.out.println(System.currentTimeMillis() - s+"ms");											
	}
}