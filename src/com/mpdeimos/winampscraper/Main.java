package com.mpdeimos.winampscraper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.mpdeimos.webscraper.ScraperException;

/**
 * Main entry point for scraping winamp.com plugin, skin and visualization
 * database.
 * 
 * @author mpdeimos
 */
public class Main
{

	public static void main(String[] args) throws IOException, ScraperException
	{
		ExecutorService executor = Executors.newFixedThreadPool(16);
		executor.submit(createScraper(1));
		executor.submit(createScraper(222431));
		executor.submit(createScraper(221984));
		executor.submit(createScraper(222088));
		executor.submit(createScraper(222647));
		executor.shutdown();
	}

	private static Runnable createScraper(final int id) throws IOException,
			ScraperException
	{
		return new Runnable()
		{

			@Override
			public void run()
			{
				ItemScraper scraper = new ItemScraper(id);
				Gson gson = new Gson();
				try
				{
					System.out.println(gson.toJson(scraper.scrape()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	}
}
