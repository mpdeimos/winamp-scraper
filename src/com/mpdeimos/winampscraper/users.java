package com.mpdeimos.winampscraper;

import com.mpdeimos.webscraper.Scraper;
import com.mpdeimos.webscraper.Scraper.Builder;
import com.mpdeimos.webscraper.ScraperException;
import com.mpdeimos.winampscraper.model.Download;
import com.mpdeimos.winampscraper.model.User;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class users
{
	private static final String ITEM_URL_TEMPLATE = "http://www.winamp.com/user/details/%d";
	private static Gson gson = new Gson();

	public static void main(String[] args) throws IOException
	{
		Map<Integer, User> users = new HashMap<>();
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
			String json = new String(Files.readAllBytes(data.toPath()));
			Download item = gson.fromJson(json, Download.class);
			if (!users.containsKey(item.userID))
			{
				users.put(item.userID, downloadUser(folder, item));
				if (users.size() % 100 == 0)
				{
					System.out.println(users.size());
				}
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path jsonFile = new File("data/users.json").toPath(); //$NON-NLS-1$
		byte[] jsonBytes = gson.toJson(users).getBytes();
		try
		{
			Files.write(jsonFile, jsonBytes);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static User downloadUser(File folder, Download item)
	{
		try
		{
			User user = new User(item.userID);
			String url = String.format(ITEM_URL_TEMPLATE, item.userID);
			Document doc = Jsoup.connect(url).get();
			Builder builder = new Scraper.Builder();
			Scraper scraper = builder.setSource(doc).setTarget(user)
					.build();
			scraper.scrape();
			return user;
		}
		catch (ScraperException | IOException e)
		{
			System.err.println(item.userID + " - " + item.ID + ": "
					+ e.getMessage());
		}
		return null;
	}
}
