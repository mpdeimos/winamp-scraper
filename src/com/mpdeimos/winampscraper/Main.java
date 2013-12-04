package com.mpdeimos.winampscraper;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Main entry point for scraping winamp.com plugin, skin and visualization
 * database.
 * 
 * @author mpdeimos
 */
public class Main {
	public static final String SAMPLE_URL = "http://www.winamp.com/plugin/details/222431";

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(SAMPLE_URL);
		HttpResponse response = client.execute(request);

		System.out.println(EntityUtils.toString(response.getEntity()));
	}
}
