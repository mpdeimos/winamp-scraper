package com.mpdeimos.winampscraper;

import com.mpdeimos.webscraper.Scraper;
import com.mpdeimos.webscraper.Scraper.Builder;
import com.mpdeimos.webscraper.ScraperException;
import com.mpdeimos.webscraper.validation.Validator.ScraperValidationException;
import com.mpdeimos.winampscraper.model.Download;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;

/**
 * Parses a Winamp plugin, skin, etc. website and parses all relevant
 * information to store it in a plain java object.
 * 
 * @author mpdeimos
 */
public class DownloadScraper implements Runnable
{
	/** Template URL for accessing the item online. */
	private static final String ITEM_URL_TEMPLATE = "http://www.winamp.com/online-service/details/%d"; //$NON-NLS-1$

	/** The maximum amount of retries in case of connection problems. */
	private static final int MAX_RETRIES = 3;

	/** The id of the template to be accessed. */
	private final int id;

	/** Amount of retries to execute a query. */
	private int retries = 0;

	/** Constructor. */
	public DownloadScraper(int id)
	{
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public void run()
	{
		DownloadScraper scraper = new DownloadScraper(this.id);
		Gson gson = new Gson();
		try
		{
			Download item = scraper.scrape();
			if (item != null)
			{
				System.out.println(gson.toJson(item));
			}
		}
		catch (SocketTimeoutException socketTimeout)
		{
			retryRun(socketTimeout);
		}
		catch (Exception e)
		{
			logException(e);
		}
	}

	/**
	 * Scrapes the HTML document for the given item id.
	 * 
	 * @return The scraped item or null if no item with the given ID exists.
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
			Scraper scraper = builder.setSource(doc).setTarget(item)
					.build();
			scraper.scrape();
		}
		catch (ScraperValidationException e)
		{
			Field targetField = e.getContext().getTargetField();
			// if the validation failed for scraping the title, return null
			if (targetField.getName().equals("name")) //$NON-NLS-1$
			{
				return null;
			}
			throw e;
		}

		return item;
	}

	/** Logs an exception to standard err. */
	private void logException(Exception e)
	{
		System.err.println("Scraping downloat item " + this.id + ": " //$NON-NLS-1$ //$NON-NLS-2$
				+ e.getMessage());

	}

	/** Retries to re-run the task if the amount of retries has not reached. */
	private void retryRun(SocketTimeoutException socketTimeout)
	{
		this.retries++;
		if (this.retries > MAX_RETRIES)
		{
			logException(socketTimeout);
			return;
		}

		System.err.println(String.format("Socket timeout, retry %d/%d", //$NON-NLS-1$
				this.retries,
				MAX_RETRIES));
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			logException(e);
		}

		run();
	}
}