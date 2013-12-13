package com.mpdeimos.winampscraper.execution;

import com.mpdeimos.webscraper.Scraper;
import com.mpdeimos.webscraper.Scraper.Builder;
import com.mpdeimos.webscraper.ScraperException;
import com.mpdeimos.webscraper.util.Strings;
import com.mpdeimos.webscraper.validation.Validator.ScraperValidationException;
import com.mpdeimos.winampscraper.ILogger;
import com.mpdeimos.winampscraper.model.Download;
import com.mpdeimos.winampscraper.model.EDownloadType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Parses a Winamp plugin, skin, etc. website and parses all relevant
 * information to store it in a plain java object.
 * 
 * @author mpdeimos
 */
public class DownloadScraper implements Callable<Integer>
{
	/** Template URL for accessing the item online. */
	private static final String ITEM_URL_TEMPLATE = "http://www.winamp.com/online-service/details/%d"; //$NON-NLS-1$

	/** The maximum amount of retries in case of connection problems. */
	private static final int MAX_RETRIES = 3;

	/** The folder download items will be stored. */
	private static final String downloadFolder = "data/download"; //$NON-NLS-1$

	/** The id of the template to be accessed. */
	private final int id;

	/** Amount of retries to execute a query. */
	private int retries = 0;

	/** The logger for error output. */
	private final ILogger logger;

	/** Constructor. */
	public DownloadScraper(int id, ILogger logger)
	{
		this.id = id;
		this.logger = logger;
	}

	/** {@inheritDoc} */
	@Override
	public Integer call()
	{
		try
		{
			Download item = this.scrape();
			if (item != null)
			{
				persistItem(item);
			}
			return this.id;
		}
		catch (SocketTimeoutException socketTimeout)
		{
			return retryRun(socketTimeout);
		}
		catch (Exception e)
		{
			logException(e);
		}

		return null;
	}

	/** Persists the item data on the hard drive. */
	private void persistItem(Download item)
	{
		File itemFolder = new File(DownloadScraper.downloadFolder,
				Integer.toString(item.ID));
		itemFolder.mkdirs();

		item.thumbnail = downloadData(
				item.thumbnailOriginal,
				itemFolder, "thumb_"); //$NON-NLS-1$
		item.screenshot = downloadData(item.screenshotOriginal, itemFolder);

		if (item.type != EDownloadType.ONLINE_SERVICE)
		{
			HttpURLConnection connection;
			try
			{
				URL url = new URL(item.downloadOriginal);
				connection = (HttpURLConnection) url.openConnection();
				connection.setInstanceFollowRedirects(false);
				item.downloadOriginal = connection.getHeaderField("Location"); //$NON-NLS-1$
				item.download = downloadData(item.downloadOriginal, itemFolder);
			}
			catch (IOException e)
			{
				logException(e);
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path jsonFile = new File(itemFolder, "data.json").toPath(); //$NON-NLS-1$
		byte[] jsonBytes = gson.toJson(item).getBytes();
		try
		{
			Files.write(jsonFile, jsonBytes);
		}
		catch (IOException e)
		{
			logException(e);
		}
	}

	/** Downloads data to the given file. */
	private String downloadData(
			String url,
			File itemFolder,
			String filePrefix)
	{

		try
		{
			String filename = getFilename(url);
			if (filePrefix != null)
			{
				filename = filePrefix + filename;
			}
			File file = new File(itemFolder, filename);
			if (file.exists())
			{
				return filename;
			}
			InputStream input = new URL(url).openStream();
			Files.copy(
					input,
					file.toPath());
			input.close();

			return filename;
		}
		catch (IOException e)
		{
			logException(e);
			return null;
		}
	}

	/** Downloads data to the given file. */
	private String downloadData(
			String url,
			File itemFolder)
	{
		return downloadData(url, itemFolder, null);
	}

	/** @return the filename of an URL. */
	private static String getFilename(String url)
	{
		return url.replaceAll(".*/", Strings.EMPTY); //$NON-NLS-1$
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

	/** Logs an exception to the application log. */
	private void logException(Exception e)
	{
		this.logger.log("Scraping download item " + this.id + ": " //$NON-NLS-1$ //$NON-NLS-2$
				+ e.getMessage() + " " + e.getClass()); //$NON-NLS-1$
	}

	/** Retries to re-run the task if the amount of retries has not reached. */
	private Integer retryRun(SocketTimeoutException socketTimeout)
	{
		this.retries++;
		if (this.retries > MAX_RETRIES)
		{
			logException(socketTimeout);
			return null;
		}

		this.logger.log(String.format("Socket timeout, retry %d/%d", //$NON-NLS-1$
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

		return call();
	}
}