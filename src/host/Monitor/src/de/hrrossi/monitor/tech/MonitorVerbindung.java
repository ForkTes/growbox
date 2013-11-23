package de.hrrossi.monitor.tech;

import gnu.io.SerialPortEvent;

import java.io.IOException;
import java.util.Vector;

import de.hrrossi.serial.SerielleParameter;
import de.hrrossi.serial.SerielleVerbindung;
import de.hrrossi.serial.SerielleVerbindungException;


public class MonitorVerbindung extends SerielleVerbindung {

	private Vector<MonitorStatusListener> listener;

	private long lastByte;

	private static enum Modus {
		NONE, STATUS, SETTIME, SETTHRESH, SETPROG, PROG;
	}

	private Modus modus = Modus.NONE;

	private byte status[];

	private int statusPointer;

	public void addListener(MonitorStatusListener aListener) {
		listener.add(aListener);
	}

	public void removeListeners() {
		listener.clear();
	}

	public MonitorVerbindung(SerielleParameter aParameter) {
		super(aParameter);
		listener = new Vector<MonitorStatusListener>(2);
		lastByte = 0L;
		status = new byte[18];
	}

	public void setData(AbstractData someData) {
		try {
			sendeSeriell(someData.getBytes());
			lastByte = System.currentTimeMillis();
		} catch (SerielleVerbindungException e) {
			logger.exception("setData", e);
		}
	}

	public void requestStatus() {
		try {
			sendeSeriell(new byte[] {(byte)0xf5});
			lastByte = System.currentTimeMillis();
		} catch (SerielleVerbindungException e) {
			logger.exception("requestStatus", e);
		}
	}

	public void serialEvent(SerialPortEvent anEvent) {
		if (anEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			if (System.currentTimeMillis() - lastByte > 500L) {
				statusPointer = 0;
				modus = Modus.NONE;
			}

			do {
				int tInput;
				try {
					tInput = is.read();
				} catch (IOException ex) {
					logger.exception("serialEvent", ex);
					return;
				}
				if (tInput == -1) {
					break;
				}
				lastByte = System.currentTimeMillis();
				status[statusPointer++] = (byte)tInput;

				switch (modus) {
				case NONE:
					if (tInput == 0xf5) {
						modus = Modus.STATUS;
					} else if (tInput == 0xf6) {
						modus = Modus.SETTIME;
					} else if (tInput == 0xf7) {
						modus = Modus.SETTHRESH;
					} else if (tInput >= 0xf8 && tInput <= 0xfd) {
						modus = Modus.SETPROG;
					} else if (tInput == 0xfe || tInput == 0xff) {
						modus = Modus.PROG;
					} else {
						statusPointer = 0;
					}
					break;
				case STATUS:
					if (statusPointer == 8) {
						MonitorStatus tStatus = new MonitorStatus(status);
						for (MonitorStatusListener tListener : listener) {
							tListener.newStatus(tStatus);
						}
						statusPointer = 0;
						modus = Modus.NONE;
					}
					break;
				case SETPROG:
				case SETTHRESH:
				case SETTIME:
					for (MonitorStatusListener tListener : listener) {
						tListener.response(status[0]);
					}
					statusPointer = 0;
					modus = Modus.NONE;
					break;
				case PROG:
					if (statusPointer == 17) {
						ProgData tData = new ProgData(status);
						for (MonitorStatusListener tListener : listener) {
							tListener.newProg(tData);
						}
						statusPointer = 0;
						modus = Modus.NONE;
					}
					break;
				}
			} while (true);
		}
	}

}
