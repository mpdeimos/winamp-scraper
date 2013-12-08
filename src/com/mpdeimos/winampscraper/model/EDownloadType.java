package com.mpdeimos.winampscraper.model;

import com.mpdeimos.webscraper.conversion.DefaultConverter.ScrapedEnum;

/**
 * Enumeration of different item types as plugins, skins or visualizations.
 * 
 * @author mpdeimos
 */
public enum EDownloadType implements ScrapedEnum
{
	PLUGIN("PLUG-IN"), SKIN, VISUALIZATION, ONLINE_SERVICE("ONLINE SERVICES");

	/** The display name of the type. */
	private final String dataName;

	/** Constructor. */
	private EDownloadType(String displayName)
	{
		this.dataName = displayName;
	}

	/** Constructor. */
	private EDownloadType()
	{
		this.dataName = this.name();
	}

	@Override
	public boolean equalsScrapedData(String data)
	{
		return data.equals(dataName);
	}
}
