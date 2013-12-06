package com.mpdeimos.winampscraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mpdeimos.winampscraper.model.EItemType;
import com.mpdeimos.winampscraper.model.Item;
import com.mpdeimos.winampscraper.util.Collections;
import com.mpdeimos.winampscraper.util.Strings;

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
	public Item scrape() throws IOException, ScraperException
	{
		String url = String.format(ITEM_URL_TEMPLATE, this.id);
		Document doc = Jsoup.connect(url).get();

		Item item = new Item();
		item.ID = this.id;

		this.scrape(item, doc);

		return item;
	}

	/** Scrapes the item from data of the given html document. */
	private void scrape(Item item, Document doc) throws ScraperException
	{
		// scrape title
		Element head = doc.getElementsByTag("title").first();
		ScraperException.ifNull(head, "head");
		item.name = Strings.stripSuffix(head.text(), " - Winamp");

		Element similar = doc.select(".skinSimilar").first();
		ScraperException.ifNull(similar, "similar box");
		if (similar.children().size() < 3)
		{
			throw new ScraperException("Not enough elements in 'similar box'");
		}

		// scrape type
		item.type = scrapeItemType(similar);

		// scrape categories
		Collections.addIfNotEmpty(item.categories, similar.child(1).text());
		Collections.addIfNotEmpty(item.categories, similar.child(2).text());
	}

	/** Scrapes the type of an item. */
	private EItemType scrapeItemType(Element similar) throws ScraperException
	{
		Element child = similar.child(0);
		String text = child.text();
		for (EItemType type : EItemType.values())
		{
			if (text.contains(type.getDisplayName()))
			{
				return type;
			}
		}

		throw new ScraperException("Item type cannot be determined from: "
				+ text);
	}
}
