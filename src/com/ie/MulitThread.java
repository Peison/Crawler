package com.ie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MulitThread {
	
	static int termail = 0; 
	
	public MulitThread() {		
	}
	
	public static void main(String[] args){
		
		long s = System.currentTimeMillis();
		//fixed pool
		ExecutorService executor = Executors.newFixedThreadPool(100);		
		
		/*
		//第一个版本的多线程
		for(int i = 0; i < 14; i++){
			executor.execute(new JD(1000000+i*100,1000100+i*100));
		}
		executor.shutdown();
		*/
		
		//第二个版本的多线程
		Queue queue = new Queue();
		for(int i = 272400; i < 1150001; i++){
			if(i > 272600) break;
			queue.enQueue(String.valueOf(i));			
		}		
			
		for(int i = 0; i < 10; i++){
			executor.execute(new JD(queue,i));						
		}
		executor.shutdown();		
		
		long e = System.currentTimeMillis();
		System.out.println("time used " + (e-s) + "ms");
	}
}
