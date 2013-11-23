package de.hrrossi.monitor;

import java.util.Timer;
import java.util.TimerTask;

import de.hrrossi.logger.Logger;
import de.hrrossi.monitor.gui.HauptFenster;
import de.hrrossi.monitor.tech.AbstractData;
import de.hrrossi.monitor.tech.MonitorVerbindung;
import de.hrrossi.serial.SerielleParameter;
import de.hrrossi.serial.SerielleVerbindungException;


public class Monitor {

	private static final String KOMPONENTE = "Applikation";

	private static final int STATUS_INTERVAL = 20;

	private Logger logger;

	private HauptFenster hauptFenster;

	private MonitorVerbindung verbindung;

	private static Monitor monitor;

	public Monitor() {
	}

	public static void main(String args[]) {
		monitor = new Monitor();
		monitor.start();
	}

	public static Monitor getMonitor() {
		return monitor;
	}

	private void start() {
		logger = new Logger(KOMPONENTE, 3);
		if (!initSerial()) {
			return;
		}
		initGui();
		startGui();
		new Timer("Monitor Status", true).scheduleAtFixedRate(new TimerTask() {

			private int counter = 0;

			@Override
			public void run() {
				hauptFenster.getStatusPanel().nextSecond(counter % STATUS_INTERVAL == 1);
				if (counter % STATUS_INTERVAL == 0) {
					verbindung.requestStatus();
				}
				counter++;
			}
		}, 1000, 1000);
	}

	private boolean initSerial() {
		verbindung = new MonitorVerbindung(new SerielleParameter("COM5", 115200, 0, 0, 8, 1, 0));
		try {
			verbindung.oeffneVerbindung();
		} catch (SerielleVerbindungException e) {
			logger.exception("Couldn't open serial port.", e);
			return false;
		}
		return true;
	}

	private void initGui() {
		hauptFenster = new HauptFenster();
		verbindung.addListener(hauptFenster.getStatusPanel());
		verbindung.addListener(hauptFenster.getControlPanel());
		logger.info("GUI initialized.");
	}

	private void startGui() {
		hauptFenster.start();
		logger.info("GUI started.");
	}

	public void requestStatus() {
		verbindung.requestStatus();
	}

	public void setData(AbstractData someData) {
		verbindung.setData(someData);
	}

	public void wechsleBaudrate(int aBaudrate) {
		try {
			verbindung.wechsleBaudrate(aBaudrate);
		} catch (SerielleVerbindungException e) {
			logger.exception("Baudrate error.", e);
		}
	}

}
