package com.mpdeimos.winampscraper.util;

/**
 * Various utility functions regarding strings.
 * 
 * @author mpdeimos
 */
public class Strings
{
	/**
	 * Returns the input string without the given suffix. If the String does not
	 * end with the suffix, the original string is returned.
	 * 
	 * @param input
	 *            The input string the suffix should be removed from
	 * @param suffix
	 *            The suffix to remove
	 * @return The string without suffix
	 */
	public static String stripSuffix(String input, String suffix)
	{
		if (input.endsWith(suffix))
		{
			return input.substring(0, input.length() - suffix.length());
		}
		return input;
	}
}
