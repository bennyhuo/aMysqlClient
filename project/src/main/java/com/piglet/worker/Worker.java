package com.piglet.worker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class Worker {
	private ThreadPoolExecutor taskExecutor;
	private static Worker executor = new Worker();

	private Worker() {
		this.taskExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public static Worker getInstance() {
		return executor;
	}

	public void addTask(Runnable task) {
		if (!this.taskExecutor.isShutdown()) {
			this.taskExecutor.execute(task);
		}
	}
	
	public void shutdown() {
		this.taskExecutor.shutdown();
	}
}
