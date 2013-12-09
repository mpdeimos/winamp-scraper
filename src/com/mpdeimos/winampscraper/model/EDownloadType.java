package com.mpdeimos.winampscraper.model;

import com.mpdeimos.webscraper.conversion.DefaultConverter.ScrapedEnum;

/**
 * Enumeration of different item types as plugins, skins or visualizations.
 * 
 * @author mpdeimos
 */
public enum EDownloadType implements ScrapedEnum
{
	/** Plug-in download type. */
	PLUGIN("PLUG-IN"), //$NON-NLS-1$

	/** Skin download type. */
	SKIN,
	/** Visualization download type. */
	VISUALIZATION,

	/** Online service download type. */
	ONLINE_SERVICE("ONLINE SERVICES"); //$NON-NLS-1$

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

	/** {@inheritDoc} */
	@Override
	public boolean equalsScrapedData(String data)
	{
		return data.equals(this.dataName);
	}
}