package com.mpdeimos.winampscraper;

/**
 * Exception that is thrown upon scraping errors.
 * 
 * @author mpdeimos
 */
public class ScraperException extends Exception
{
	/** Constructor. */
	public ScraperException(String message)
	{
		super(message);
	}

	/**
	 * Throws a {@link ScraperException} if the given value is <code>null</code>
	 * .
	 * 
	 * @param message
	 *            The name or message for identifying the value.
	 * @param value
	 *            The value to check.
	 * @throws ScraperException
	 *             if value is null.
	 */
	public static void ifNull(Object value, String message)
			throws ScraperException
	{
		if (value == null)
		{
			throw new ScraperException("Item is null: " + message);
		}
	}
}
