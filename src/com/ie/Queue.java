package com.ie;

import java.util.LinkedList;
/**
 * 数据结构队列
 */
public class Queue {

	private LinkedList<String> queue = new LinkedList<String>();
	
	public Queue(){		
	}
	
	public void enQueue(String url){
		queue.add(url);		
	}
	
	public String deQueue(){
		return queue.removeFirst();
	}
	
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public int getSize(){
		return queue.size();
	}
}