package com.ie;

public class NewControler implements Runnable{
	
	Queue queue;
	
	public NewControler(Queue queue){
		this.queue = queue;
	}
	
	public void run(){
		
		long s = System.currentTimeMillis();		
		while(!queue.isEmpty()){
			Thread.yield();
		}
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		long e = System.currentTimeMillis();
		System.out.println("------Crawler Finished------"+(e-s)/1000+"s used!");
	}
}
