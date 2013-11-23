package de.hrrossi.serial;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import de.hrrossi.logger.Logger;


public class SerielleVerbindung implements SerialPortEventListener, CommPortOwnershipListener {

	public static final byte COM_STX = 2;

	public static final byte COM_ETX = 3;

	public static final byte COM_EOT = 4;

	public static final byte COM_ENQ = 5;

	public static final byte COM_ACK = 6;

	public static final byte COM_BEL = 7;

	public static final byte COM_DLE = 16;

	public static final byte COM_NAK = 21;

	public static final byte COM_SYN = 22;

	public static final byte COM_CAN = 24;

	public static final byte COM_ESC = 27;

	public static final byte COM_FS = 28;

	public static final int TIMEOUT_NEVER = -1;

	private static final String KOMPONENTE = "SerielleVerbindung";

	protected Logger logger;

	private CommPortIdentifier portId;

	private SerialPort sPort;

	private SerielleParameter parameters;

	protected OutputStream os;

	protected InputStream is;

	private boolean istOffen;

	private byte eingabePuffer[];

	private int writeIndex;

	private int readIndex;

	private static final int PUFFER_LEN = 4096;

	public SerielleVerbindung(SerielleParameter aSerielleParameter) {
		parameters = aSerielleParameter;
		istOffen = false;
		eingabePuffer = new byte[PUFFER_LEN];
		writeIndex = 0;
		readIndex = 0;
		logger = new Logger(KOMPONENTE, 3);
	}

	public SerielleVerbindung() {
		istOffen = false;
		eingabePuffer = new byte[PUFFER_LEN];
		writeIndex = 0;
		readIndex = 0;
		logger = new Logger(KOMPONENTE, 3);
	}

	public void serialEvent(SerialPortEvent anEvent) {
		if (anEvent.getEventType() == 1) {
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
				eingabePuffer[writeIndex++] = (byte)(tInput & 0xff);
				if (writeIndex == PUFFER_LEN) {
					writeIndex = 0;
				}
			} while (true);
		}
	}

	public void ownershipChange(int aTyp) {
		if (aTyp == 3) {
			logger.error("Port ownership change requested.");
		}
	}

	public void setDTR(boolean anIsOn) {
		sPort.setDTR(anIsOn);
	}

	public void oeffneVerbindung() throws SerielleVerbindungException {
		schliesseVerbindung();
		try {
			logger.info((new StringBuffer("Portname: ")).append(parameters.getPortName()).toString());
			portId = CommPortIdentifier.getPortIdentifier(parameters.getPortName());
		} catch (NullPointerException ex) {
			throw new SerielleVerbindungException("Couldn't find comm driver.", ex);
		} catch (NoSuchPortException ex) {
			throw new SerielleVerbindungException("Couldn't find comm driver.", ex);
		}
		try {
			sPort = (SerialPort)portId.open("Monitor", 1000);
		} catch (PortInUseException ex) {
			throw new SerielleVerbindungException("Port open failed.", ex);
		}
		try {
			setzeVerbindungsParameter();
		} catch (SerielleVerbindungException ex) {
			sPort.close();
			throw new SerielleVerbindungException("Couldn't set parameters.", ex);
		}
		try {
			os = sPort.getOutputStream();
			is = sPort.getInputStream();
		} catch (IOException ex) {
			sPort.close();
			throw new SerielleVerbindungException("Couldn't get input or output stream.", ex);
		}
		try {
			sPort.addEventListener(this);
		} catch (TooManyListenersException ex) {
			sPort.close();
			throw new SerielleVerbindungException("Too many listeners.", ex);
		}
		sPort.notifyOnDataAvailable(true);
		sPort.notifyOnBreakInterrupt(false);
		try {
			sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException ex) {
			logger.warning((new StringBuffer("Couldn't enable receive timeout: ")).append(ex.getMessage()).toString());
		}
		if (!sPort.isReceiveTimeoutEnabled()) {
			logger.warning("Enabling receive timeout failed.");
		}
		portId.addPortOwnershipListener(this);
		istOffen = true;
		try {
			Thread.sleep(200L);
		} catch (InterruptedException interruptedexception) {
		}
		loeschePuffer();
	}

	public void setzeVerbindungsParameter() throws SerielleVerbindungException {
		int tAlteBaudrate = sPort.getBaudRate();
		int tAlteDatenbits = sPort.getDataBits();
		int tAlteStopbits = sPort.getStopBits();
		int tAlteParitaet = sPort.getParity();
		try {
			sPort.setSerialPortParams(parameters.getBaudRate(), parameters.getDatabits(), parameters.getStopbits(),
					parameters.getParity());
		} catch (UnsupportedCommOperationException ex) {
			parameters.setBaudRate(tAlteBaudrate);
			parameters.setDatabits(tAlteDatenbits);
			parameters.setStopbits(tAlteStopbits);
			parameters.setParity(tAlteParitaet);
			throw new SerielleVerbindungException("Parameter error.", ex);
		}
		try {
			sPort.setFlowControlMode(parameters.getFlowControlIn() | parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException ex) {
			throw new SerielleVerbindungException("Unsupported flow control.", ex);
		}
	}

	public void wechsleBaudrate(int aBaudrate) throws SerielleVerbindungException {
		try {
			sPort.setSerialPortParams(aBaudrate, parameters.getDatabits(), parameters.getStopbits(),
					parameters.getParity());
		} catch (UnsupportedCommOperationException ex) {
			throw new SerielleVerbindungException("Parameter error.", ex);
		}
	}

	public void schliesseVerbindung() {
		if (!istOffen) {
			return;
		}
		if (sPort != null) {
			try {
				os.close();
				is.close();
			} catch (IOException ex) {
				logger.exception("schliesseVerbindung", ex);
			}
			sPort.close();
		}
		portId.removePortOwnershipListener(this);
		istOffen = false;
	}

	public boolean istOffen() {
		return istOffen;
	}

	public void sendeSeriell(byte aNachricht[]) throws SerielleVerbindungException {
		if (os == null) {
			throw new SerielleVerbindungException("Output stream is null");
		}
		try {
			os.write(aNachricht);
			os.flush();
		} catch (IOException ex) {
			throw new SerielleVerbindungException("Error while writing to output stream.", ex);
		}
	}

	public byte[] empfangeSeriell() {
		int tAktuell = writeIndex;
		byte tRueckgabe[];
		if (readIndex > tAktuell) {
			int tLen = PUFFER_LEN - readIndex;
			tRueckgabe = new byte[tLen + tAktuell];
			System.arraycopy(eingabePuffer, readIndex, tRueckgabe, 0, tLen);
			System.arraycopy(eingabePuffer, 0, tRueckgabe, tLen, tAktuell);
		} else if (readIndex < tAktuell) {
			int tLen = tAktuell - readIndex;
			tRueckgabe = new byte[tLen];
			System.arraycopy(eingabePuffer, readIndex, tRueckgabe, 0, tLen);
		} else {
			tRueckgabe = new byte[0];
		}
		readIndex = tAktuell;
		return tRueckgabe;
	}

	public boolean empfangeKommando(byte anErgebnis[], int aTimeoutMs) {
		long tTimeout = System.currentTimeMillis() + (long)aTimeoutMs;
		do {
			if (writeIndex != readIndex) {
				anErgebnis[0] = eingabePuffer[readIndex++];
				if (readIndex >= PUFFER_LEN) {
					readIndex = 0;
				}
				return true;
			}
			try {
				Thread.sleep(30L);
			} catch (InterruptedException interruptedexception) {
			}
		} while (System.currentTimeMillis() < tTimeout || aTimeoutMs == -1);
		logger.warning("empfangeKommando timeout !");
		return false;
	}

	public void loeschePuffer() {
		if (sPort == null) {
			logger.error("loeschePuffer: port not open");
			return;
		} else {
			sPort.notifyOnDataAvailable(false);
			eingabePuffer = new byte[PUFFER_LEN];
			writeIndex = 0;
			readIndex = 0;
			sPort.notifyOnDataAvailable(true);
			return;
		}
	}

	public int getAnzahlEmpfangeneBytes() {
		int tAnzahl = writeIndex - readIndex;
		if (tAnzahl < 0) {
			tAnzahl += PUFFER_LEN;
		}
		return tAnzahl;
	}

	public boolean getWurdenBytesEmpfangen() {
		return writeIndex != readIndex;
	}

}
