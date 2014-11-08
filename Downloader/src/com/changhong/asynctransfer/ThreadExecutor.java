package com.changhong.asynctransfer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadExecutor {
	
	private ExecutorService executorService = null;
	private static ThreadExecutor instance = null;

	private ThreadExecutor() {
		if (this.executorService == null) {
			int count = Math.min(3, (int) (Runtime.getRuntime()
					.availableProcessors() * 1.2 + 1));
			this.executorService = new ThreadPoolExecutor(count, count, 10000,
					TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(20,
							true));
		}
	}
	
	public void doTask(Runnable task){
		this.executorService.execute(task);
	}
	
	public <T> Future<T> doTask(Callable<T> task){
		FutureTask<T> future = new FutureTask<T>(task);
		this.executorService.execute(future);
		return future;
	}

	public void shutdown() {
		this.executorService.shutdown();
	}

	public static ThreadExecutor defaultInstance() {
		if (instance == null)
			instance = new ThreadExecutor();
		return instance;
	}
}
