package de.hrrossi.serial;

public class SerielleParameter {

	private String portName;

	private int baudRate;

	private int flowControlIn;

	private int flowControlOut;

	private int databits;

	private int stopbits;

	private int parity;

	public SerielleParameter() {
		this("", 9600, 0, 0, 8, 1, 0);
	}

	public SerielleParameter(String aPortName, int aBaudRate, int aFlowControlIn, int aFlowControlOut, int aDatabits,
			int aStopbits, int aParity) {
		portName = aPortName;
		baudRate = aBaudRate;
		flowControlIn = aFlowControlIn;
		flowControlOut = aFlowControlOut;
		databits = aDatabits;
		stopbits = aStopbits;
		parity = aParity;
	}

	public void setPortName(String aPortName) {
		portName = aPortName;
	}

	public String getPortName() {
		return portName;
	}

	public void setBaudRate(int aBaudRate) {
		baudRate = aBaudRate;
	}

	public void setBaudRate(String aBaudRate) {
		baudRate = Integer.parseInt(aBaudRate);
	}

	public int getBaudRate() {
		return baudRate;
	}

	public String getBaudRateString() {
		return Integer.toString(baudRate);
	}

	public void setFlowControlIn(int aFlowControlIn) {
		flowControlIn = aFlowControlIn;
	}

	public void setFlowControlIn(String aFlowControlIn) {
		flowControlIn = stringToFlow(aFlowControlIn);
	}

	public int getFlowControlIn() {
		return flowControlIn;
	}

	public String getFlowControlInString() {
		return flowToString(flowControlIn);
	}

	public void setFlowControlOut(int aFlowControlOut) {
		flowControlOut = aFlowControlOut;
	}

	public void setFlowControlOut(String aFlowControlOut) {
		flowControlOut = stringToFlow(aFlowControlOut);
	}

	public int getFlowControlOut() {
		return flowControlOut;
	}

	public String getFlowControlOutString() {
		return flowToString(flowControlOut);
	}

	public void setDatabits(int aDatabits) {
		databits = aDatabits;
	}

	public void setDatabits(String aDatabits) {
		if (aDatabits.equals("5")) {
			databits = 5;
		} else if (aDatabits.equals("6")) {
			databits = 6;
		} else if (aDatabits.equals("7")) {
			databits = 7;
		} else {
			databits = 8;
		}
	}

	public int getDatabits() {
		return databits;
	}

	public String getDatabitsString() {
		switch (databits) {
		case 5:
			return "5";
		case 6:
			return "6";
		case 7:
			return "7";
		default:
			return "8";
		}
	}

	public void setStopbits(int aStopbits) {
		stopbits = aStopbits;
	}

	public void setStopbits(String aStopbits) {
		if (aStopbits.equals("1.5")) {
			stopbits = 3;
		} else if (aStopbits.equals("2")) {
			stopbits = 2;
		} else {
			stopbits = 1;
		}
	}

	public int getStopbits() {
		return stopbits;
	}

	public String getStopbitsString() {
		switch (stopbits) {
		case 3:
			return "1.5";
		case 2:
			return "2";
		default:
			return "1";
		}
	}

	public void setParity(int aParity) {
		parity = aParity;
	}

	public void setParity(String aParity) {
		if (aParity.equals("Even")) {
			parity = 2;
		} else if (aParity.equals("Odd")) {
			parity = 1;
		} else {
			parity = 0;
		}
	}

	public int getParity() {
		return parity;
	}

	public String getParityString() {
		switch (parity) {
		case 2:
			return "Even";
		case 1:
			return "Odd";
		default:
			return "None";
		}
	}

	private int stringToFlow(String aFlowControl) {
		if (aFlowControl.equals("None")) {
			return 0;
		}
		if (aFlowControl.equals("Xon/Xoff Out")) {
			return 8;
		}
		if (aFlowControl.equals("Xon/Xoff In")) {
			return 4;
		}
		if (aFlowControl.equals("RTS/CTS In")) {
			return 1;
		}
		return !aFlowControl.equals("RTS/CTS Out") ? 0 : 2;
	}

	private String flowToString(int aFlowControl) {
		switch (aFlowControl) {
		case 0:
			return "None";
		case 8:
			return "Xon/Xoff Out";
		case 4:
			return "Xon/Xoff In";
		case 1:
			return "RTS/CTS In";
		case 2:
			return "RTS/CTS Out";
		default:
			return "None";
		}
	}

}
