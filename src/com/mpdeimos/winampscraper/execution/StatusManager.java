package com.mpdeimos.winampscraper.execution;

import com.mpdeimos.webscraper.util.Assert;
import com.mpdeimos.winampscraper.App;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Manages and preserves the execution status between application sessions.
 * 
 * @author mpdeimos
 */
public class StatusManager
{
	/** The status file containing serialized json data. */
	private final Path statusFile = new File("data", "status.json").toPath(); //$NON-NLS-1$ //$NON-NLS-2$

	/** List of futures that download items. */
	private final List<Future<Integer>> downloadFutures = new ArrayList<>();

	/** The application status from the last execution. */
	private Status status = new Status();

	/** Adds a download future for progress monitoring. */
	public void addDownloadFuture(Future<Integer> future)
	{
		this.downloadFutures.add(future);
	}

	/** Restores an application status. */
	public void restore()
	{
		if (!Files.exists(this.statusFile))
		{
			return;
		}

		try
		{
			String json = new String(Files.readAllBytes(this.statusFile));
			this.status = new Gson().fromJson(json, Status.class);
		}
		catch (IOException e)
		{
			App.getApp().log("could not read status file."); //$NON-NLS-1$
		}
	}

	/** Persists the application status. */
	public void persist()
	{
		ArrayList<Integer> completed = new ArrayList<Integer>();
		for (int id : this.status.successfulDownloadTasks)
		{
			completed.add(id);
		}

		for (Future<Integer> future : this.downloadFutures)
		{
			if (future.isDone() && !future.isCancelled())
			{
				try
				{
					Integer id = future.get();
					if (id != null)
					{
						completed.add(id);
					}
				}
				catch (InterruptedException | ExecutionException e)
				{
					Assert.notCaught(e, "we checked that the future is done"); //$NON-NLS-1$
				}
			}
		}

		Status status = new Status();
		status.successfulDownloadTasks = new int[completed.size()];

		int i = 0;
		for (Integer id : completed)
		{
			status.successfulDownloadTasks[i++] = id;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		byte[] jsonBytes = gson.toJson(status).getBytes();
		try
		{
			Files.write(this.statusFile, jsonBytes);
		}
		catch (IOException e)
		{
			App.getApp().log("Could not write status file."); //$NON-NLS-1$
		}
	}

	/**
	 * Checks whether the download has already been completed in a previous run.
	 */
	public boolean isDownloadDone(int id)
	{
		for (int sicessfulDownloadId : this.status.successfulDownloadTasks)
		{
			if (sicessfulDownloadId == id)
			{
				return true;
			}
		}
		return false;
	}

}
