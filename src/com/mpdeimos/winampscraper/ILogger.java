package com.mpdeimos.winampscraper;

/**
 * Interface for defining a simple logging mechanism.
 * 
 * @author mpdeimos
 * 
 */
public interface ILogger
{
	/** Logs a message. */
	public void log(String message);

	/** Logger that prints to standard error output. */
	public class StdErrLogger implements ILogger
	{
		/** {@inheritdoc} */
		@Override
		public void log(String message)
		{
			System.err.println(message);
		}
	}
}
