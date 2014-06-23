package com.ie;

import java.util.ArrayList;
import java.util.Iterator;

public class ThreadControler implements Runnable{
	
	private ArrayList<Thread> list;
	Queue queue;
	
	public ThreadControler(){		
	}
	
	public ThreadControler(ArrayList<Thread> list,Queue queue){
		this.list = list;
		this.queue = queue;
	}
	
	//Ϊ�˱�֤����������������������̵߳�������ѳ�ʱ�䲻�������߳��ֶ�����
	@Override
	public void run() {					
		//������ͣ10�룬��Ϊ�½��̵߳Ļ���ʱ��
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("=================== controler is running ===================");
		
		//ÿ��1���ѯ��������ǲ���Ϊ�գ���ѯ�޷������������̵߳��߳�ջ�����ֶ�������
		while(true){			
			//�����Ƿ�Ϊ��
			if(queue.isEmpty()){												
				//Ϊ�˷�ֹ�ֶ�����  �������������Ľ��̣�����ѯ������Ϊ�պ��ȵȴ�
				System.out.println("=================== queue is empty ===================");
				Thread.yield();
				
				System.out.println("=================== stop all thread ===================");
				
				Iterator<Thread> iterator = list.iterator();
				while(iterator.hasNext()){				
					Thread t = iterator.next();				
					if(t.isAlive()){
						for(StackTraceElement e : t.getStackTrace()){
							System.out.println(t.getId() + " ��������"+e.getMethodName()+" at "+e.getLineNumber());
						}						
						System.err.println("----------"+t.getName()+" "+t.getId()+" has terminaled by controler!");
						t = null;
					}	
				}
				break;
			}				
			//ÿ��һ���ѯһ��
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}		
		System.out.println("=================== controler has stoped ===================");
		
	}

}
