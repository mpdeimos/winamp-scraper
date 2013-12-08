package com.mpdeimos.winampscraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mpdeimos.webscraper.Scraper;
import com.mpdeimos.webscraper.Scraper.Builder;
import com.mpdeimos.webscraper.ScraperException;
import com.mpdeimos.webscraper.validation.Validator.ScraperValidationException;
import com.mpdeimos.winampscraper.model.Download;

/**
 * Parses a Winamp plugin, skin, etc. website and parses all relevant
 * information to store it in a plain java object.
 * 
 * @author mpdeimos
 */
public class ItemScraper
{
	/** Template URL for accessing the item online. */
	private static final String ITEM_URL_TEMPLATE = "http://www.winamp.com/online-service/details/%d";

	/** The id of the template to be accessed. */
	private final int id;

	/** Constructor. */
	public ItemScraper(int id)
	{
		this.id = id;
	}

	/**
	 * Scrapes the website for the given item id.
	 * 
	 * @return The scraped item or null if no item with the given ID exists.
	 * @throws IOException
	 *             If the connection cannot be established.
	 * @throws ScraperException
	 *             If any other scraping error occurs.
	 */
	public Download scrape() throws IOException, ScraperException
	{
		String url = String.format(ITEM_URL_TEMPLATE, this.id);
		Document doc = Jsoup.connect(url).get();

		Download item = new Download();
		item.ID = this.id;

		try
		{
			Builder builder = new Scraper.Builder();
			Scraper scraper = builder.setSource(doc).setDestination(item)
					.build();
			scraper.scrape();
		}
		catch (ScraperValidationException e)
		{
			// if the validation failed for scraping the title, return null
			if (e.getField().getName().equals("name"))
			{
				return null;
			}
			throw e;
		}

		return item;
	}
}
