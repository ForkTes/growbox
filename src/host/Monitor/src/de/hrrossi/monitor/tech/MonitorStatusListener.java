package de.hrrossi.monitor.tech;

public interface MonitorStatusListener {

	public void newStatus(MonitorStatus monitorstatus);

	public void response(byte response);

	public void newProg(ProgData progdata);

	public void nextSecond(boolean isStatusExpected);

}
