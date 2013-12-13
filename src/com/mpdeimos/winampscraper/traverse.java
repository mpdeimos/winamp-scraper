package com.mpdeimos.winampscraper;

import com.mpdeimos.winampscraper.execution.Status;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class traverse
{
	public static void main(String[] args) throws IOException
	{
		Path path = new File("data/log.txt").toPath(); //$NON-NLS-1$
		List<String> lines = Files.readAllLines(path, Charset.defaultCharset());

		String json = new String(
				Files.readAllBytes(new File("data/status.json").toPath()));
		Status status = new Gson().fromJson(json, Status.class);

		HashSet<Integer> done = new HashSet<>();
		for (int id : status.successfulDownloadTasks)
		{
			done.add(id);
		}

		Pattern p = Pattern.compile("Scraping download item ([0-9]*):.*");
		for (String line : lines)
		{
			Matcher matcher = p.matcher(line);
			if (matcher.matches())
			{
				done.remove(Integer.parseInt(matcher.group(1)));
			}
		}

		Status s = new Status();
		s.successfulDownloadTasks = new int[done.size()];
		int i = 0;
		for (int id : done)
		{
			s.successfulDownloadTasks[i++] = id;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		byte[] jsonBytes = gson.toJson(s).getBytes();
		try
		{
			Files.write(new File("data/status.json").toPath(), jsonBytes);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
