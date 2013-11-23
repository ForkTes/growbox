package de.hrrossi.monitor.tech;

public class ThresholdData extends AbstractData {

	private int lowT;

	private int highT;

	private int lowH;

	private int highH;

	public void setLowT(int lowT) {
		this.lowT = lowT;
	}

	public void setHighT(int highT) {
		this.highT = highT;
	}

	public void setLowH(int lowH) {
		this.lowH = lowH;
	}

	public void setHighH(int highH) {
		this.highH = highH;
	}

	@Override
	public byte[] getBytes() {
		return new byte[] {(byte)0xf7, (byte)lowT, (byte)highT, (byte)lowH, (byte)highH};
	}

}
