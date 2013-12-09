package com.mpdeimos.winampscraper;

import com.mpdeimos.webscraper.util.Assert;
import com.mpdeimos.winampscraper.execution.DownloadScraper;
import com.mpdeimos.winampscraper.execution.EmptyRunnable;
import com.mpdeimos.winampscraper.execution.PauseableThreadPoolExecutor;
import com.mpdeimos.winampscraper.execution.ProgressMonitor;
import com.mpdeimos.winampscraper.execution.StatusManager;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/**
 * Application UI class.
 * 
 * @author mpdeimos
 */
public class App extends JFrame
{
	/**
	 * as of 2013-12-09:
	 * 
	 * <ul>
	 * <li>newest skin 222663</li>
	 * <li>newest plugin 222666</li>
	 * <li>newest visualization 222665</li>
	 * <li>newest online service 222647</li>
	 * <ul>
	 * 
	 * Hence we're scraping till 223000 to be safe
	 */
	private static final int MAX_ITEMS = 223000;

	/** Singleton instance of the app. */
	public static App app;

	/** The executor service. */
	private final PauseableThreadPoolExecutor executor = new PauseableThreadPoolExecutor(
			16);

	/** The logging output text area. */
	private final JTextArea output;

	/** Monitor for updating the scraping progress. */
	private final Timer monitor = new Timer();

	/** The status manager for persisting and retrieving the application status. */
	private final StatusManager statusManager = new StatusManager();

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void createAndDisplay(final String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				app = new App();
				app.setVisible(true);
			}
		});
	}

	/** Constructor. */
	private App()
	{
		setNativeLookAndFeel();

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				App.this.monitor.cancel();
				App.this.executor.shutdownNow();
				App.this.statusManager.persist();
			}
		});

		this.setTitle("Winamp Scraper");

		this.setLayout(new BorderLayout(5, 5));

		JProgressBar progress = new JProgressBar(0, MAX_ITEMS);
		progress.setStringPainted(true);
		this.add(progress, BorderLayout.NORTH);
		this.monitor.schedule(
				new ProgressMonitor(this.executor, progress),
				0,
				500);

		this.output = new JTextArea();
		this.output.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(this.output);
		this.add(scrollPane, BorderLayout.CENTER);

		final JButton pause = new JButton("Start");
		pause.addActionListener(this.PAUSE_CLICK_LISTENER);
		this.add(pause, BorderLayout.SOUTH);

		this.pack();

		this.setBounds(50, 50, 400, 300);
	}

	/**
	 * Sets the native look and feel for the application. Terminates if not
	 * possible.
	 */
	private void setNativeLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			Assert.notCaught(e, "native look and feel is recent in JREs."); //$NON-NLS-1$
		}
	}

	/** @return The singleton instance of the application. */
	public static App getApp()
	{
		return app;
	}

	/** Adds a message to the logging output. */
	public void log(String message)
	{
		this.output.append(message + "\n"); //$NON-NLS-1$
	}

	/** Create and enqueue execution tasks. */
	private void enqueueAndStartTasks()
	{
		this.statusManager.restore();

		for (int i = 0; i < MAX_ITEMS; i++)
		{
			if (this.statusManager.isDownloadDone(i))
			{
				this.executor.submit(new EmptyRunnable());
			}
			else
			{
				Future<Integer> feature = this.executor.submit(new DownloadScraper(
						i));
				this.statusManager.addDownloadFuture(feature);
			}
		}

		this.executor.shutdown();
	}

	/** Listener for clicking the pause button. */
	private final ActionListener PAUSE_CLICK_LISTENER = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton pause = (JButton) e.getSource();
			if (App.this.executor.getTaskCount() == 0)
			{
				enqueueAndStartTasks();
				pause.setText("Pause");
			}
			else if (pause.getText().equals("Pause"))
			{
				pause.setText("Resume");
				App.this.executor.pause();
			}
			else
			{
				pause.setText("Pause");
				App.this.executor.resume();
			}
		}
	};
}
