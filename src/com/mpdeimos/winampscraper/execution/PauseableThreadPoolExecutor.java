package com.mpdeimos.winampscraper.execution;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Pauseable thread pool executor similar to the one fund in the JavaDoc of
 * {@link ThreadPoolExecutor} and posts on Stackoverflow.
 * 
 * @author http://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/
 *         ThreadPoolExecutor.html
 * @author 
 *         http://stackoverflow.com/questions/9748710/how-to-pause-resume-all-threads
 *         -in-an-executorservice-in-java?answertab=votes#tab-top
 * 
 */
public class PauseableThreadPoolExecutor extends ScheduledThreadPoolExecutor
{
	/** Flag indicating whether the executor is paused. */
	private boolean isPaused = false;

	/** The pause lock. */
	private final ReentrantLock pauseLock = new ReentrantLock();

	/** The pause condition. */
	private final Condition unpaused = this.pauseLock.newCondition();

	/** Constructor. */
	public PauseableThreadPoolExecutor(int poolSize)
	{
		super(poolSize);
	}

	/** {@inheritDoc} */
	@Override
	protected void beforeExecute(Thread t, Runnable r)
	{
		super.beforeExecute(t, r);
		this.pauseLock.lock();
		try
		{
			while (this.isPaused)
			{
				this.unpaused.await();
			}
		}
		catch (InterruptedException ie)
		{
			t.interrupt();
		}
		finally
		{
			this.pauseLock.unlock();
		}
	}

	/** Pauses threadpool execution. */
	public void pause()
	{
		this.pauseLock.lock();
		try
		{
			this.isPaused = true;
		}
		finally
		{
			this.pauseLock.unlock();
		}
	}

	/** Resumes threadpool execution. */
	public void resume()
	{
		this.pauseLock.lock();
		try
		{
			this.isPaused = false;
			this.unpaused.signalAll();
		}
		finally
		{
			this.pauseLock.unlock();
		}
	}
}