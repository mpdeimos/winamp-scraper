package com.mpdeimos.winampscraper;

import java.io.IOException;

import com.google.gson.Gson;

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
		Gson gson = new Gson();

		// skin
		ItemScraper scraper = new ItemScraper(222431);
		System.out.println(gson.toJson(scraper.scrape()));

		// plugin
		scraper = new ItemScraper(221984);
		System.out.println(gson.toJson(scraper.scrape()));

		// visualization
		scraper = new ItemScraper(222088);
		System.out.println(gson.toJson(scraper.scrape()));

		// online service
		scraper = new ItemScraper(222647);
		System.out.println(gson.toJson(scraper.scrape()));
	}
}
