package de.hrrossi.monitor.tech;

public class MonitorStatus {

	private short program;

	private int days;

	private int hours;

	private int minutes;

	private int seconds;

	private int temperature;

	private int humidity;

	private boolean neon;

	private boolean sodium;

	private boolean vent;

	private boolean temperatureError;

	private boolean humidityError;

	private boolean forceVent;

	public MonitorStatus(byte aStatus[]) {
		//program = (short)(aStatus[0] & 0xf);
		//forceVent = (aStatus[1] & 1) != 0;
		//vent = (aStatus[1] & 2) != 0;
		program = (short)(aStatus[1] & 0x7);
		forceVent = (aStatus[1] & 0x40) != 0;
		vent = (aStatus[1] & 0x80) != 0;
		neon = (aStatus[1] & 0x10) != 0;
		sodium = (aStatus[1] & 0x20) != 0;
		//temperatureError = (aStatus[1] & 0x40) != 0;
		//humidityError = (aStatus[1] & 0x80) != 0;
		temperatureError = false;
		humidityError = false;
		temperature = aStatus[2] & 0xff;
		humidity = aStatus[3] & 0xff;
		days = aStatus[4] & 0xff;
		hours = aStatus[5] & 0xff;
		minutes = aStatus[6] & 0xff;
		seconds = aStatus[7] & 0xff;
	}

	public short getProgram() {
		return program;
	}

	public int getDays() {
		return days;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public int getTemperature() {
		return temperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public boolean isNeon() {
		return neon;
	}

	public boolean isSodium() {
		return sodium;
	}

	public boolean isVent() {
		return vent;
	}

	public boolean isTemperatureError() {
		return temperatureError;
	}

	public boolean isHumidityError() {
		return humidityError;
	}

	public boolean isForceVent() {
		return forceVent;
	}

}
