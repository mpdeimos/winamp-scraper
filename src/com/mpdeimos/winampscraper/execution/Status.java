package com.mpdeimos.winampscraper.execution;

/**
 * Class that is serialized using JSON for storing the application task status
 * between sessions.
 * 
 * @author mpdeimos
 */
public class Status
{
	/** The IDs of successfully finished tasks. */
	public int[] successfulDownloadTasks = new int[0];
}
