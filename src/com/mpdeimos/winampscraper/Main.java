package com.mpdeimos.winampscraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Main entry point for scraping winamp.com plugin, skin and visualization
 * database.
 * 
 * @author mpdeimos
 */
public class Main {
	public static final String SAMPLE_URL = "http://www.winamp.com/plugin/details/222431";

	public static void main(String[] args) throws IOException {

		Document doc = Jsoup.connect(SAMPLE_URL).get();
		Element head = doc.getElementsByTag("title").first();
		String text = head.text();
		System.out.println(text.substring(0, text.length() - 9)); // removes
																	// " - Winamp"

		System.out.println(doc.select(".skinSimilar").first().child(0).text());

	}
}
