package com.ie;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulitThread {
	static int termail = 0;

	public MulitThread() {
	}

	public static void main(String[] args) {
		
		// fixed pool
		// ExecutorService executor = Executors.newFixedThreadPool(100);

		/*
		 * 第一个版本的多线程 for(int i = 0; i < 14; i++){
		 * executor.execute(newJD(1000000+i*100,1000100+i*100)); }
		 * executor.shutdown();
		 */

		// 第二个版本的多线程272600
		Queue queue = new Queue();
		for (int i = 1157500; i < 1160001; i++) {
			if (i > 1159500)
				break;
			queue.enQueue(String.valueOf(i));
		}

		/*
		 * for (int i = 0; i < 20; i++) { executor.execute(new JD(queue, i)); }
		 * new Thread(new ThreadControler(executor, queue)).start();
		 * executor.shutdown();
		 */
		
		for (int i = 0; i < 50; i++) {
			Thread t = new Thread(new JD(queue, i));
			t.setDaemon(true);
			t.start();
		}
		new Thread(new NewControler(queue)).start();				
	}
}
