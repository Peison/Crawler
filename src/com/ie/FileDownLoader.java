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
	
	
	/**������ҳ�ֽ����鵽�����ļ�
	 * filePath ΪҪ������ļ�����Ե�ַ
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
	
	

	/*���� url ָ�����ҳ*/
	public String  downloadFile(String url,int id)
	{		  
		  /* 1.���� HttpClinet �������ò���*/
		  HttpClient httpClient=new HttpClient();
		  //���� Http ���ӳ�ʱ 5s
		  httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		  
		  /*2.���� GetMethod �������ò���*/
		  GetMethod getMethod=new GetMethod(url);	 
		  //���� get ����ʱ 5s
		  getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,5000);
		  //�����������Դ���
		  getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			new DefaultHttpMethodRetryHandler());
		  
		  /*3.ִ�� HTTP GET ����*/
		  try{ 
			  int statusCode = httpClient.executeMethod(getMethod);
			  //�жϷ��ʵ�״̬��
			  if (statusCode != HttpStatus.SC_OK) 
			  {
				  System.err.println("Method failed: "+ getMethod.getStatusLine());				 
			  }			    			  
			  
			  /*4.���� HTTP ��Ӧ����*/
//			  byte[] responseBody = getMethod.getResponseBody();//��ȡΪ�ֽ�����			 
			  			  			  			 
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
			  
			  InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();//��ȡ��
			  BufferedReader reader = new BufferedReader(new InputStreamReader(responseBodyAsStream));
			  
			  String string = reader.readLine();			  
			  responseBodyAsStream.close();
			  reader.close();
//			  writer.close();
			  
			  return string.substring(string.indexOf("\"p\":\"")+5,string.indexOf(",\"m\":")-1);
			  
		  } catch (HttpException e) {
				   // �����������쳣��������Э�鲻�Ի��߷��ص�����������
				   System.out.println("Please check your provided http address!");
				   System.err.println("File download 95 "+id+" "+e.getMessage());
				  } catch (IOException e) {
				   // ���������쳣
				   System.err.println("File download 98 "+id+" "+e.getMessage());
				  } finally {
				   // �ͷ�����
				   getMethod.releaseConnection();		   
				  }
				  return "";
	}
	//���Ե� main ����
	public static void main(String[]args)
	{
		long s = System.currentTimeMillis();			
		FileDownLoader downLoader = new FileDownLoader();
		String get = downLoader.downloadFile("http://p.3.cn/prices/get?skuid=J_657899",1);
		System.out.println(get.equals("-1"));
		System.out.println(System.currentTimeMillis() - s+"ms");											
	}
}