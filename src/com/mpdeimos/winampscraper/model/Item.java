package com.mpdeimos.winampscraper.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class holding information about
 * 
 * @author mpdeimos
 */
public class Item
{
	/** The unique identifier for the item. */
	public int ID;

	/** The type of the item. */
	public EItemType type;

	/** The name of the item. */
	public String name;

	/** The categories of the item. */
	public List<String> categories = new ArrayList<>();

	/** The ID of the user who created the item. */
	public int userID;

}
