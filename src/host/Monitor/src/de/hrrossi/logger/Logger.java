package de.hrrossi.logger;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {

	public static final int LOGLEVEL_EXCEPTION = 0;

	public static final int LOGLEVEL_ERROR = 1;

	public static final int LOGLEVEL_WARNING = 2;

	public static final int LOGLEVEL_INFO = 3;

	public static final int LOGLEVEL_FINE = 4;

	public static final int LOGLEVEL_FINER = 5;

	public static final int LOGLEVEL_FINEST = 6;

	private final SimpleDateFormat FORMAT_ZEIT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

	private String modulName;

	private int logLevel;

	public Logger(String aName, int aLevel) {
		modulName = aName;
		logLevel = aLevel;
	}

	public void exception(String aMeldung, Throwable aThrowable) {
		System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|EXCEPTION|")
				.append(modulName).append("| ").append(aMeldung).append(" - ").append(aThrowable.getMessage())
				.toString());
	}

	public void error(String aMeldung) {
		if (logLevel >= 1) {
			System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|ERROR    |")
					.append(modulName).append("| ").append(aMeldung).toString());
		}
	}

	public void warning(String aMeldung) {
		if (logLevel >= 2) {
			System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|WARNING  |")
					.append(modulName).append("| ").append(aMeldung).toString());
		}
	}

	public void info(String aMeldung) {
		if (logLevel >= 3) {
			System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|INFO     |")
					.append(modulName).append("| ").append(aMeldung).toString());
		}
	}

	public void fine(String aMeldung) {
		if (logLevel >= 4) {
			System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|FINE     |")
					.append(modulName).append("| ").append(aMeldung).toString());
		}
	}

	public void finer(String aMeldung) {
		if (logLevel >= 5) {
			System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|FINER    |")
					.append(modulName).append("| ").append(aMeldung).toString());
		}
	}

	public void finest(String aMeldung) {
		if (logLevel >= 6) {
			System.out.println((new StringBuffer(String.valueOf(FORMAT_ZEIT.format(new Date())))).append("|FINEST   |")
					.append(modulName).append("| ").append(aMeldung).toString());
		}
	}

}
