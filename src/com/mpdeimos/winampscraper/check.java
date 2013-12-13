package com.mpdeimos.winampscraper;

import com.mpdeimos.webscraper.Scrape;
import com.mpdeimos.webscraper.Scraper;
import com.mpdeimos.webscraper.Scraper.Builder;
import com.mpdeimos.webscraper.ScraperException;
import com.mpdeimos.webscraper.conversion.DateFormatConverter;
import com.mpdeimos.winampscraper.model.Download;
import com.mpdeimos.winampscraper.model.EDownloadType;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class check
{
	private static final String ITEM_URL_TEMPLATE = "http://www.winamp.com/online-service/details/%d"; //$NON-NLS-1$

	public static void main(String[] args)
	{
		Gson gson = new Gson();
		for (File folder : new File("data/download").listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.isDirectory();
			}
		}))
		{
			File data = new File(folder, "data.json");
			try
			{
				String json = new String(Files.readAllBytes(data.toPath()));
				Download item = gson.fromJson(json, Download.class);
				updateDate(folder, item);
				validateItem(folder, item);
			}
			catch (IOException e)
			{
				logError(folder, e.getMessage());
			}
		}

	}

	private static void updateDate(File folder, Download item)
	{
		String url = String.format(ITEM_URL_TEMPLATE, item.ID);

		try
		{
			Document doc = Jsoup.connect(url).get();
			Builder builder = new Scraper.Builder();
			d d = new d();
			Scraper scraper = builder.setSource(doc).setTarget(d)
					.build();
			scraper.scrape();
			item.date = d.date;

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Path jsonFile = new File(folder, "data.json").toPath(); //$NON-NLS-1$
			byte[] jsonBytes = gson.toJson(item).getBytes();
			Files.write(jsonFile, jsonBytes);
		}
		catch (IOException e1)
		{
			logError(folder, e1.getMessage());
		}
		catch (ScraperException e)
		{
			logError(folder, e.getMessage());
		}
	}

	public static class d
	{
		/** The submission date of the item. */
		@Scrape(value = ".skinMain dl dt", resultIndex = 2,
				convertor = DateFormatConverter.class)
		@DateFormatConverter.Option("MMM dd, yyyy")
		public Date date;
	}

	private static void validateItem(File folder, Download item)
	{
		assertThat(item.categories.length > 0, "categories empty", folder);
		assertThat(item.date != null, "date null", folder);
		assertThat(item.descriptionDetail != null, "descriptionDetail", folder);
		assertThat(
				item.descriptionTitle != null,
				"descriptionTitle null",
				folder);
		if (item.type != EDownloadType.ONLINE_SERVICE)
		{
			assertThat(item.download != null, "download null", folder);
			assertThat(
					new File(folder, item.download).exists(),
					"download does not exist",
					folder);
		}
		assertThat(item.thumbnail != null, "thumbnail null", folder);
		assertThat(
				new File(folder, item.thumbnail).exists(),
				"thumbnail does not exist",
				folder);
		assertThat(item.screenshot != null, "screenshot null", folder);
		assertThat(
				new File(folder, item.screenshot).exists(),
				"screenshot does not exist",
				folder);
	}

	private static void assertThat(boolean b, String message, File folder)
	{
		if (!b)
		{
			logError(folder, message);
		}
	}

	private static void logError(File folder, String string)
	{
		System.err.println(folder.getName() + ": " + string);

	}
}
