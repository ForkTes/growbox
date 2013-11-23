package de.hrrossi.monitor.tech;

public class TimeData extends AbstractData {

	private int days;

	private int hours;

	private int minutes;

	private int seconds;

	public void setDays(int days) {
		this.days = days;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	@Override
	public byte[] getBytes() {
		return new byte[] {(byte)0xf6, (byte)days, (byte)hours, (byte)minutes, (byte)seconds};
	}

}
