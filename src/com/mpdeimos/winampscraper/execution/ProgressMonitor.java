package com.mpdeimos.winampscraper.execution;

import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JProgressBar;

/**
 * Monitors the execution process of the executor service.
 * 
 * @author mpdeimos
 */
public class ProgressMonitor extends TimerTask
{
	/** The executor service. */
	private final ThreadPoolExecutor executor;

	/** The progress bar that is indicating the scraping progress. */
	private final JProgressBar progress;

	/** Constructor. */
	public ProgressMonitor(
			ThreadPoolExecutor executor,
			JProgressBar progress)
	{
		this.executor = executor;
		this.progress = progress;
	}

	/** {@inheritDoc} */
	@Override
	public void run()
	{
		long taskCount = this.executor.getTaskCount();
		long completedTaskCount = this.executor.getCompletedTaskCount();

		if (taskCount == 0)
		{
			return;
		}

		this.progress.setMaximum((int) taskCount);
		this.progress.setValue((int) completedTaskCount);
		this.progress.setToolTipText("Completed " + completedTaskCount
				+ " of " + taskCount + " tasks");
	}
}
