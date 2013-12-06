package com.mpdeimos.winampscraper.util;

import org.jsoup.nodes.Element;

import com.mpdeimos.winampscraper.ScraperException;

/**
 * Utility functions for dealing with HTML Documents. For querying functions
 * returns one element, an exception is thrown if the element is not found.
 * 
 * @author mpdeimos
 */
public class Html
{
	/** @return The first element with the given tag or throws an exception. */
	public static Element firstElementByTag(Element doc, String tag)
			throws ScraperException
	{
		Element element = doc.getElementsByTag(tag).first();
		ScraperException.ifNull(element, tag);

		return element;
	}

	/** @return The first element matching a css selector or throws an exception. */
	public static Element firstElement(Element doc, String cssSelector)
			throws ScraperException
	{
		Element element = doc.select(cssSelector).first();
		ScraperException.ifNull(element, cssSelector);

		return element;
	}
}
