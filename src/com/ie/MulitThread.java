package com.ie;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MulitThread {
	
	static int termail = 0; 
	
	public MulitThread() {		
	}
	
	public static void main(String[] args){
		
		long s = System.currentTimeMillis();
		//fixed pool
		ExecutorService executor = Executors.newFixedThreadPool(40);		
		
		/*
		//第一个版本的多线程
		for(int i = 0; i < 14; i++){
			executor.execute(new JD(1000000+i*100,1000100+i*100));
		}
		executor.shutdown();
		*/
		
		//第二个版本的多线程272600
		Queue queue = new Queue();
		for(int i = 1120000; i < 1150001; i++){
			if(i > 1130000) break;
			queue.enQueue(String.valueOf(i));			
		}		
			
		/*for(int i = 0; i < 30; i++){
			executor.execute(new JD(queue,i));				
		}
		executor.shutdown();*/
		
		ArrayList<Thread> list = new ArrayList<Thread>();		
		for(int i = 0; i < 30; i++){			
			Thread t = new Thread(new JD(queue,i));			
			list.add(t);
			t.start();
		}
		
		new Thread(new ThreadControler(list,queue)).start();		
				
		long e = System.currentTimeMillis();
		System.out.println("time used " + (e-s) + "ms");
	}
}
