package com.mpdeimos.winampscraper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main entry point for scraping winamp.com plugin, skin and visualization
 * database.
 * 
 * @author mpdeimos
 */
public class Main
{

	/** Main program execution. */
	public static void main(String[] args)
	{
		ExecutorService executor = Executors.newFixedThreadPool(16);
		// for (int i = 0; i < 10000; i++)
		// executor.submit(new DownloadScraper(i));
		executor.submit(new DownloadScraper(1));
		executor.submit(new DownloadScraper(222431));
		executor.submit(new DownloadScraper(221984));
		executor.submit(new DownloadScraper(222088));
		executor.submit(new DownloadScraper(222647));
		executor.shutdown();
	}
}
