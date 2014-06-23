package com.ie;

import java.util.ArrayList;
import java.util.Iterator;

public class ThreadControler implements Runnable{
	
	private ArrayList<Thread> list;
	private Queue queue;
	private int loopTimeGap = 5000;
	private int killWait = 120000;
	
	public ThreadControler(){		
	}
	
	public ThreadControler(ArrayList<Thread> list,Queue queue){
		this.list = list;
		this.queue = queue;
	}
	
	//为了保证整个进程正常结束，监控线程的情况，把长时间不结束的线程手动结束
	@Override
	public void run() {					
		//首先暂停10秒，作为新建线程的缓冲时间
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("=================== controler is running ===================");
		
		//每隔1秒查询任务队列是不是为空，查询无法正常结束的线程的线程栈，并手动结束；
		while(true){			
			
			//队列是否为空
			if(queue.isEmpty()){												
				//为了防止手动结束  可以正常结束的进程，当查询到队列为空后，先等待
				System.out.println("=================== queue is empty ===================");
				
				try {
					Thread.sleep(killWait);//等待时间必须由队列大小确定？？
				} catch (InterruptedException e1) {					
					e1.printStackTrace();
				}
				
				System.out.println("=================== stop all thread ===================");
				
				Iterator<Thread> iterator = list.iterator();
				while(iterator.hasNext()){				
					Thread t = iterator.next();				
					if(t.isAlive()){
						for(StackTraceElement e : t.getStackTrace()){
							System.out.println(t.getId() + " 方法名："+e.getMethodName()+" at "+e.getLineNumber());
						}						
						System.err.println("----------"+t.getName()+" "+t.getId()+" has terminaled by controler!");
						t.interrupt();
					}	
				}
				break;
			}				

			//每隔n秒查询一次，n怎么确定
			try{
				Iterator<Thread> iterator = list.iterator();
				while(iterator.hasNext()){				
					Thread t = iterator.next();														
					if(!t.isAlive()){
						iterator.remove();
					}
				}
				
				Thread.sleep(loopTimeGap);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
			//如果线程队列为空，监测线程结束
			if(list.isEmpty())
				break;
		}		
		System.out.println("=================== controler has stoped ===================");
		
	}

}
