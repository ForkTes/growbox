package de.hrrossi.monitor.tech;

public class ProgData extends AbstractData {

	private int program;

	private int neon1StartH;

	private int neon1StartM;

	private int neon1EndH;

	private int neon1EndM;

	private int neon2StartH;

	private int neon2StartM;

	private int neon2EndH;

	private int neon2EndM;

	private int sont1StartH;

	private int sont1StartM;

	private int sont1EndH;

	private int sont1EndM;

	private int sont2StartH;

	private int sont2StartM;

	private int sont2EndH;

	private int sont2EndM;

	public ProgData() {
	}

	public ProgData(byte[] someData) {
		neon1StartH = someData[1];
		neon1StartM = someData[2];
		neon1EndH = someData[3];
		neon1EndM = someData[4];
		neon2StartH = someData[5];
		neon2StartM = someData[6];
		neon2EndH = someData[7];
		neon2EndM = someData[8];
		sont1StartH = someData[9];
		sont1StartM = someData[10];
		sont1EndH = someData[11];
		sont1EndM = someData[12];
		sont2StartH = someData[13];
		sont2StartM = someData[14];
		sont2EndH = someData[15];
		sont2EndM = someData[16];
	}

	public int getProgram() {
		return program + 1;
	}

	public int getNeon1StartH() {
		return neon1StartH;
	}

	public int getNeon1StartM() {
		return neon1StartM;
	}

	public int getNeon1EndH() {
		return neon1EndH;
	}

	public int getNeon1EndM() {
		return neon1EndM;
	}

	public int getNeon2StartH() {
		return neon2StartH;
	}

	public int getNeon2StartM() {
		return neon2StartM;
	}

	public int getNeon2EndH() {
		return neon2EndH;
	}

	public int getNeon2EndM() {
		return neon2EndM;
	}

	public int getSont1StartH() {
		return sont1StartH;
	}

	public int getSont1StartM() {
		return sont1StartM;
	}

	public int getSont1EndH() {
		return sont1EndH;
	}

	public int getSont1EndM() {
		return sont1EndM;
	}

	public int getSont2StartH() {
		return sont2StartH;
	}

	public int getSont2StartM() {
		return sont2StartM;
	}

	public int getSont2EndH() {
		return sont2EndH;
	}

	public int getSont2EndM() {
		return sont2EndM;
	}

	public void setProgram(int program) {
		this.program = program - 1;
	}

	public void setNeon1StartH(int neon1StartH) {
		this.neon1StartH = neon1StartH;
	}

	public void setNeon1StartM(int neon1StartM) {
		this.neon1StartM = neon1StartM;
	}

	public void setNeon1EndH(int neon1EndH) {
		this.neon1EndH = neon1EndH;
	}

	public void setNeon1EndM(int neon1EndM) {
		this.neon1EndM = neon1EndM;
	}

	public void setNeon2StartH(int neon2StartH) {
		this.neon2StartH = neon2StartH;
	}

	public void setNeon2StartM(int neon2StartM) {
		this.neon2StartM = neon2StartM;
	}

	public void setNeon2EndH(int neon2EndH) {
		this.neon2EndH = neon2EndH;
	}

	public void setNeon2EndM(int neon2EndM) {
		this.neon2EndM = neon2EndM;
	}

	public void setSont1StartH(int sont1StartH) {
		this.sont1StartH = sont1StartH;
	}

	public void setSont1StartM(int sont1StartM) {
		this.sont1StartM = sont1StartM;
	}

	public void setSont1EndH(int sont1EndH) {
		this.sont1EndH = sont1EndH;
	}

	public void setSont1EndM(int sont1EndM) {
		this.sont1EndM = sont1EndM;
	}

	public void setSont2StartH(int sont2StartH) {
		this.sont2StartH = sont2StartH;
	}

	public void setSont2StartM(int sont2StartM) {
		this.sont2StartM = sont2StartM;
	}

	public void setSont2EndH(int sont2EndH) {
		this.sont2EndH = sont2EndH;
	}

	public void setSont2EndM(int sont2EndM) {
		this.sont2EndM = sont2EndM;
	}

	@Override
	public byte[] getBytes() {
		return new byte[] {(byte)(program + 0xf8), (byte)neon1StartH, (byte)neon1StartM, (byte)neon1EndH,
				(byte)neon1EndM, (byte)neon2StartH, (byte)neon2StartM, (byte)neon2EndH, (byte)neon2EndM,
				(byte)sont1StartH, (byte)sont1StartM, (byte)sont1EndH, (byte)sont1EndM, (byte)sont2StartH,
				(byte)sont2StartM, (byte)sont2EndH, (byte)sont2EndM};
	}

}
