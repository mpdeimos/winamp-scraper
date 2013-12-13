package com.mpdeimos.winampscraper.model;

import com.mpdeimos.webscraper.Scrape;
import com.mpdeimos.webscraper.conversion.DateFormatConverter;

import java.util.Date;

/**
 * Model class holding information about users.
 * 
 * @author mpdeimos
 */
public class User
{
	/** Constructor. */
	public User(int userID)
	{
		this.ID = userID;
	}

	/** The unique identifier for the user. */
	public int ID;

	/** The name of the user. */
	@Scrape(".qa .questions .welcome")
	public String name;

	/** The date since the user is registered. */
	@Scrape(
			value = ".profileDetails > div > span:containsOwn(Member Since) + span",
			convertor = DateFormatConverter.class)
	@DateFormatConverter.Option("MMM dd, yyyy")
	public Date date;

	/** The location of the user. */
	@Scrape(".profileDetails > div > span:containsOwn(Location) + span")
	public String location;

	/** The about field of the user. */
	@Scrape(".profileDetails .publicAbout > p")
	public String about;
}
