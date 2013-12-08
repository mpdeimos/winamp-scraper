package com.mpdeimos.winampscraper.model;

import java.util.Date;

import com.mpdeimos.webscraper.Scrape;
import com.mpdeimos.webscraper.conversion.DateFormatConverter;
import com.mpdeimos.webscraper.conversion.NumberFormatConverter;
import com.mpdeimos.webscraper.validation.NotEmptyValidator;

/**
 * Model class holding information about downloadable items.
 * 
 * @author mpdeimos
 */
public class Download
{
	/** The unique identifier for the item. */
	public int ID;

	/** The name of the item. */
	@Scrape(value = "head > title", regex = "(.*)- Winamp",
			validator = NotEmptyValidator.class)
	public String name;

	/** The type of the item. */
	@Scrape(value = ".detailCont .hdrTitle span", regex = "(.*) DETAILS")
	public EDownloadType type;

	/** The categories of the item. */
	@Scrape(value = ".skinSimilar > div > a", empty = false)
	public String[] categories;

	/** The ID of the user who created the item. */
	@Scrape(value = ".skinMain dt > b > a", attribute = "href",
			regex = ".*/(.*)")
	public int userID;

	/** The submission date of the item. */
	@Scrape(value = ".skinMain dl dt", resultIndex = 2,
			convertor = DateFormatConverter.class)
	@DateFormatConverter.Option("MMM DD, YYY")
	public Date date;

	/** The amount of downloads for the item. */
	@Scrape(value = ".skinMain dl dt", resultIndex = 3,
			regex = "(.*) downloads", convertor = NumberFormatConverter.class)
	public long downloads;

	/** The item thumbnail. */
	@Scrape(value = ".skinMain > .lftDiv > a > img", attribute = "src")
	public String thumbnail;

	/** The item screenshot. */
	@Scrape(value = ".lftDetail > a > img", attribute = "src")
	public String screenshot;

	/** The item download (executable, skin, etc.) */
	@Scrape(value = ".skinMain > .lftDiv > a", attribute = "href")
	public String download;

	/** The title of the item description. */
	@Scrape(value = ".longComment b", resultIndex = 0)
	public String descriptionTitle;

	/** The item description. */
	@Scrape(value = ".longComment p", resultIndex = 0)
	public String descriptionDetail;

	/** The title of the item review. */
	@Scrape(value = ".longComment b", resultIndex = 1, lenient = true)
	public String reviewTitle;

	/** The item review. */
	@Scrape(value = ".longComment p", resultIndex = 1, lenient = true)
	public String reviewDetail;

	/** The overall review rating. */
	@Scrape(value = ".reviewtype .rateView", attribute = "title",
			resultIndex = 3, regex = ".*: (.*)", lenient = true)
	public Float reviewOverall;

	/** The review rating for originality. */
	@Scrape(value = ".reviewtype .rateView", attribute = "title",
			resultIndex = 0, regex = ".*: (.*)", lenient = true)
	public Float reviewOriginality;

	/** The review rating for completeness. */
	@Scrape(value = ".reviewtype .rateView", attribute = "title",
			resultIndex = 1, regex = ".*: (.*)", lenient = true)
	public Float reviewCompleteness;

	/** The review rating for aesthetics. */
	@Scrape(value = ".reviewtype .rateView", attribute = "title",
			resultIndex = 2, regex = ".*: (.*)", lenient = true)
	public Float reviewAesthetics;
}
