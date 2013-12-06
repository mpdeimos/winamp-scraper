package com.mpdeimos.winampscraper.model;

/**
 * Enumeration of different item types as plugins, skins or visualizations.
 * 
 * @author mpdeimos
 */
public enum EItemType
{
	PLUGIN("plug-in"), SKIN, VISUALIZATION, ONLINE_SERVICE;

	/** The display name of the type. */
	private final String displayName;

	/** Constructor. */
	private EItemType(String displayName)
	{
		this.displayName = displayName;
	}

	/** Constructor. */
	private EItemType()
	{
		this.displayName = this.name().toLowerCase().replace('_', ' ');
	}

	/** @return The display name of item type. */
	public String getDisplayName()
	{
		return displayName;
	}
}
