package de.hrrossi.monitor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.hrrossi.monitor.Monitor;
import de.hrrossi.monitor.tech.*;
import de.hrrossi.monitor.tech.SensorAdapter.RawData;


public class ControlPanel extends JPanel implements MonitorStatusListener {

	/**  */
	private static final long serialVersionUID = 6981733405136264164L;

	private JLabel temperatureValue;

	private JLabel lowTemperatureLabel;

	private JTextField lowTemperature;

	private JLabel lowTemperatureValue;

	private JLabel highTemperatureLabel;

	private JTextField highTemperature;

	private JLabel highTemperatureValue;

	private JLabel humidityValue;

	private JLabel lowHumidityLabel;

	private JTextField lowHumidity;

	private JLabel lowHumidityValue;

	private JLabel highHumidityLabel;

	private JTextField highHumidity;

	private JLabel highHumidityValue;

	private JButton requestReset;

	private JButton setThresholds;

	private JLabel daysLabel;

	private JTextField days;

	private JLabel hoursLabel;

	private JTextField hours;

	private JLabel minutesLabel;

	private JTextField minutes;

	private JLabel secondsLabel;

	private JTextField seconds;

	private JButton setTime;

	private JLabel progLabel;

	private JTextField prog;

	private JLabel neon1StartLabel;

	private JTextField neon1Start;

	private JLabel neon1EndLabel;

	private JTextField neon1End;

	private JLabel neon2StartLabel;

	private JTextField neon2Start;

	private JLabel neon2EndLabel;

	private JTextField neon2End;

	private JLabel sont1StartLabel;

	private JTextField sont1Start;

	private JLabel sont1EndLabel;

	private JTextField sont1End;

	private JLabel sont2StartLabel;

	private JTextField sont2Start;

	private JLabel sont2EndLabel;

	private JTextField sont2End;

	private JButton setProgram;

	public ControlPanel() {
		super(new GridBagLayout());
		temperatureValue = new JLabel();
		lowTemperatureLabel = new JLabel();
		lowTemperature = new JTextField();
		lowTemperatureValue = new JLabel();
		highTemperatureLabel = new JLabel();
		highTemperature = new JTextField();
		highTemperatureValue = new JLabel();
		humidityValue = new JLabel();
		lowHumidityLabel = new JLabel();
		lowHumidity = new JTextField();
		lowHumidityValue = new JLabel();
		highHumidityLabel = new JLabel();
		highHumidity = new JTextField();
		highHumidityValue = new JLabel();
		requestReset = new JButton();
		setThresholds = new JButton();
		daysLabel = new JLabel();
		days = new JTextField();
		hoursLabel = new JLabel();
		hours = new JTextField();
		minutesLabel = new JLabel();
		minutes = new JTextField();
		secondsLabel = new JLabel();
		seconds = new JTextField();
		setTime = new JButton();
		progLabel = new JLabel();
		prog = new JTextField();
		neon1StartLabel = new JLabel();
		neon1Start = new JTextField();
		neon1EndLabel = new JLabel();
		neon1End = new JTextField();
		neon2StartLabel = new JLabel();
		neon2Start = new JTextField();
		neon2EndLabel = new JLabel();
		neon2End = new JTextField();
		sont1StartLabel = new JLabel();
		sont1Start = new JTextField();
		sont1EndLabel = new JLabel();
		sont1End = new JTextField();
		sont2StartLabel = new JLabel();
		sont2Start = new JTextField();
		sont2EndLabel = new JLabel();
		sont2End = new JTextField();
		setProgram = new JButton();

		setParameters();
		addCompononts();
	}

	private RawData getLowThresholds() {
		int tT = 25, tH = 40;
		try {
			tT = Integer.parseInt(lowTemperature.getText());
			if (tT < 10) {
				lowTemperature.setText("10");
				tT = 10;
			} else if (tT > 50) {
				lowTemperature.setText("50");
				tT = 50;
			}
		} catch (NumberFormatException e) {
			lowTemperature.setText("25");
		}
		try {
			tH = Integer.parseInt(lowHumidity.getText());
			if (tH < 5) {
				lowHumidity.setText("5");
				tH = 5;
			} else if (tH > 95) {
				lowHumidity.setText("95");
				tH = 95;
			}
		} catch (NumberFormatException e) {
			lowHumidity.setText("40");
		}
		RawData tData = SensorAdapter.getRawData(tT, tH);
		lowTemperatureValue.setText(String.valueOf(tData.getSensorT()));
		lowHumidityValue.setText(String.valueOf(tData.getSensorH()));

		return tData;
	}

	private RawData getHighThresholds() {
		int tT = 25, tH = 40;
		try {
			tT = Integer.parseInt(highTemperature.getText());
			if (tT < 10) {
				highTemperature.setText("10");
				tT = 10;
			} else if (tT > 50) {
				highTemperature.setText("50");
				tT = 50;
			}
		} catch (NumberFormatException e) {
			highTemperature.setText("25");
		}
		try {
			tH = Integer.parseInt(highHumidity.getText());
			if (tH < 5) {
				highHumidity.setText("5");
				tH = 5;
			} else if (tH > 95) {
				highHumidity.setText("95");
				tH = 95;
			}
		} catch (NumberFormatException e) {
			highHumidity.setText("40");
		}
		RawData tData = SensorAdapter.getRawData(tT, tH);
		highTemperatureValue.setText(String.valueOf(tData.getSensorT()));
		highHumidityValue.setText(String.valueOf(tData.getSensorH()));

		return tData;
	}

	private ActionListener thresholdListener = new ActionListener() {

		public void actionPerformed(ActionEvent anEvent) {
			String tName = ((Component)anEvent.getSource()).getName();
			if (tName.startsWith("low")) {
				getLowThresholds();
			} else if (tName.startsWith("high")) {
				getHighThresholds();
			}
		}
	};

	private ActionListener setThresholdListener = new ActionListener() {

		public void actionPerformed(ActionEvent anEvent) {
			RawData tLows = getLowThresholds();
			RawData tHighs = getHighThresholds();

			ThresholdData tData = new ThresholdData();
			tData.setLowT(tLows.getSensorT());
			tData.setHighT(tHighs.getSensorT());
			tData.setLowH(tLows.getSensorH());
			tData.setHighH(tHighs.getSensorH());

			Monitor.getMonitor().setData(tData);
			// TODO
		}
	};

	private ActionListener setTimeListener = new ActionListener() {

		public void actionPerformed(ActionEvent anEvent) {
			int tDays, tHours, tMinutes, tSeconds;
			try {
				tDays = Integer.parseInt(days.getText());
				tHours = Integer.parseInt(hours.getText());
				tMinutes = Integer.parseInt(minutes.getText());
				tSeconds = Integer.parseInt(seconds.getText());
			} catch (NumberFormatException e) {
				days.setText("0");
				hours.setText("0");
				minutes.setText("0");
				seconds.setText("0");
				return;
			}
			if (tDays < 0 || tDays > 255 || tHours < 0 || tHours > 23 || tMinutes < 0 || tMinutes > 59 || tSeconds < 0
					|| tSeconds > 59) {
				days.setText("0");
				hours.setText("0");
				minutes.setText("0");
				seconds.setText("0");
				return;
			}

			TimeData tData = new TimeData();
			tData.setDays(tDays);
			tData.setHours(tHours);
			tData.setMinutes(tMinutes);
			tData.setSeconds(tSeconds);

			Monitor.getMonitor().setData(tData);
			// TODO
		}
	};

	private static class TimeWindow {

		private int startHours = -1;

		private int startMinutes = -1;

		private int endHours = -1;

		private int endMinutes = -1;

		private TimeWindow(JTextField aStartField, JTextField anEndField) {
			String[] tStart = aStartField.getText().split(":");
			String[] tEnd = anEndField.getText().split(":");
			if (tStart.length != 2 || tEnd.length != 2) {
				aStartField.setText("--:--");
				anEndField.setText("--:--");
				return;
			}
			int tStartH, tStartM, tEndH, tEndM;
			try {
				tStartH = Integer.parseInt(tStart[0]);
				tStartM = Integer.parseInt(tStart[1]);
				tEndH = Integer.parseInt(tEnd[0]);
				tEndM = Integer.parseInt(tEnd[1]);
			} catch (NumberFormatException e) {
				aStartField.setText("--:--");
				anEndField.setText("--:--");
				return;
			}
			if (tStartH < 0 || tStartH > 23 || tStartM < 0 || tStartM > 59 || tEndH < 0 || tEndH > 23 || tEndM < 0
					|| tEndM > 59 || tStartH * 100 + tStartM >= tEndH * 100 + tEndM) {
				aStartField.setText("--:--");
				anEndField.setText("--:--");
				return;
			}
			startHours = tStartH;
			startMinutes = tStartM;
			endHours = tEndH;
			endMinutes = tEndM;
		}

	}

	private ActionListener setProgListener = new ActionListener() {

		public void actionPerformed(ActionEvent anEvent) {
			int tProg = 8;
			try {
				tProg = Integer.parseInt(prog.getText());
			} catch (NumberFormatException e) {
				prog.setText("8");
			}
			if (tProg < 1 || tProg > 8) {
				prog.setText("8");
				tProg = 8;
			}
			TimeWindow tNeon1 = new TimeWindow(neon1Start, neon1End);
			TimeWindow tNeon2 = new TimeWindow(neon2Start, neon2End);
			TimeWindow tSont1 = new TimeWindow(sont1Start, sont1End);
			TimeWindow tSont2 = new TimeWindow(sont2Start, sont2End);

			ProgData tData = new ProgData();
			tData.setProgram(tProg);
			tData.setNeon1StartH(tNeon1.startHours);
			tData.setNeon1StartM(tNeon1.startMinutes);
			tData.setNeon1EndH(tNeon1.endHours);
			tData.setNeon1EndM(tNeon1.endMinutes);
			tData.setNeon2StartH(tNeon2.startHours);
			tData.setNeon2StartM(tNeon2.startMinutes);
			tData.setNeon2EndH(tNeon2.endHours);
			tData.setNeon2EndM(tNeon2.endMinutes);
			tData.setSont1StartH(tSont1.startHours);
			tData.setSont1StartM(tSont1.startMinutes);
			tData.setSont1EndH(tSont1.endHours);
			tData.setSont1EndM(tSont1.endMinutes);
			tData.setSont2StartH(tSont2.startHours);
			tData.setSont2StartM(tSont2.startMinutes);
			tData.setSont2EndH(tSont2.endHours);
			tData.setSont2EndM(tSont2.endMinutes);

			Monitor.getMonitor().setData(tData);
			// TODO
		}
	};

	private ActionListener resetListener = new ActionListener() {

		public void actionPerformed(ActionEvent anEvent) {
			// initiate reset (signaled by baudrate 300)
			Monitor.getMonitor().wechsleBaudrate(300);
		}
	};

	private void setParameters() {
		setPreferredSize(new Dimension(640, 200));
		setMinimumSize(new Dimension(640, 200));
	}

	private void addCompononts() {
		Insets tInsetsLabel = new Insets(4, 8, 4, 2);
		Insets tInsets = new Insets(4, 2, 4, 8);

		requestReset.setText("#");
		requestReset.addActionListener(resetListener);
		GridBagConstraints tConstraints = new GridBagConstraints();
		tConstraints.gridx = 0;
		tConstraints.gridy = 0;
		tConstraints.gridwidth = 2;
		tConstraints.gridheight = 2;
		tConstraints.insets = tInsets;
		add(requestReset, tConstraints);

		temperatureValue.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 0;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(temperatureValue, tConstraints);
		lowTemperatureLabel.setText("T low:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 3;
		tConstraints.gridy = 0;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(lowTemperatureLabel, tConstraints);
		lowTemperature.setName("lowTemperature");
		lowTemperature.setColumns(3);
		lowTemperature.setText("25");
		lowTemperature.addActionListener(thresholdListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 0;
		tConstraints.insets = tInsets;
		add(lowTemperature, tConstraints);
		lowTemperatureValue.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 5;
		tConstraints.gridy = 0;
		tConstraints.anchor = GridBagConstraints.WEST;
		tConstraints.insets = tInsetsLabel;
		add(lowTemperatureValue, tConstraints);
		highTemperatureLabel.setText("T high:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 6;
		tConstraints.gridy = 0;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(highTemperatureLabel, tConstraints);
		highTemperature.setName("highTemperature");
		highTemperature.setColumns(3);
		highTemperature.setText("25");
		highTemperature.addActionListener(thresholdListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 7;
		tConstraints.gridy = 0;
		tConstraints.insets = tInsets;
		add(highTemperature, tConstraints);
		highTemperatureValue.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 8;
		tConstraints.gridy = 0;
		tConstraints.anchor = GridBagConstraints.WEST;
		tConstraints.insets = tInsetsLabel;
		add(highTemperatureValue, tConstraints);
		humidityValue.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 1;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(humidityValue, tConstraints);
		lowHumidityLabel.setText("H low:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 3;
		tConstraints.gridy = 1;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(lowHumidityLabel, tConstraints);
		lowHumidity.setName("lowHumidity");
		lowHumidity.setColumns(3);
		lowHumidity.setText("40");
		lowHumidity.addActionListener(thresholdListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 1;
		tConstraints.insets = tInsets;
		add(lowHumidity, tConstraints);
		lowHumidityValue.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 5;
		tConstraints.gridy = 1;
		tConstraints.anchor = GridBagConstraints.WEST;
		tConstraints.insets = tInsetsLabel;
		add(lowHumidityValue, tConstraints);
		highHumidityLabel.setText("H high:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 6;
		tConstraints.gridy = 1;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(highHumidityLabel, tConstraints);
		highHumidity.setName("highHumidity");
		highHumidity.setColumns(3);
		highHumidity.setText("40");
		highHumidity.addActionListener(thresholdListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 7;
		tConstraints.gridy = 1;
		tConstraints.insets = tInsets;
		add(highHumidity, tConstraints);
		highHumidityValue.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 8;
		tConstraints.gridy = 1;
		tConstraints.anchor = GridBagConstraints.WEST;
		tConstraints.insets = tInsetsLabel;
		add(highHumidityValue, tConstraints);
		setThresholds.setText("Set");
		setThresholds.addActionListener(setThresholdListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 10;
		tConstraints.gridy = 0;
		tConstraints.gridheight = 2;
		tConstraints.insets = tInsets;
		add(setThresholds, tConstraints);

		daysLabel.setText("ddd");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 2;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(daysLabel, tConstraints);
		days.setName("days");
		days.setColumns(3);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 3;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(days, tConstraints);
		hoursLabel.setText("hh");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 2;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(hoursLabel, tConstraints);
		hours.setName("hours");
		hours.setColumns(2);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 5;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(hours, tConstraints);
		minutesLabel.setText("mm");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 6;
		tConstraints.gridy = 2;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(minutesLabel, tConstraints);
		minutes.setName("minutes");
		minutes.setColumns(2);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 7;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(minutes, tConstraints);
		secondsLabel.setText("ss");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 8;
		tConstraints.gridy = 2;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(secondsLabel, tConstraints);
		seconds.setName("seconds");
		seconds.setColumns(2);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 9;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(seconds, tConstraints);
		setTime.setText("Set");
		setTime.addActionListener(setTimeListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 10;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(setTime, tConstraints);

		progLabel.setText("Prg");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 0;
		tConstraints.gridy = 3;
		tConstraints.gridheight = 2;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(progLabel, tConstraints);
		prog.setName("prog");
		prog.setColumns(1);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 1;
		tConstraints.gridy = 3;
		tConstraints.gridheight = 2;
		tConstraints.insets = tInsets;
		add(prog, tConstraints);
		neon1StartLabel.setText("Neon 1 Start:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 3;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(neon1StartLabel, tConstraints);
		neon1Start.setName("neon1Start");
		neon1Start.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 3;
		tConstraints.gridy = 3;
		tConstraints.insets = tInsets;
		add(neon1Start, tConstraints);
		neon1EndLabel.setText("End:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 3;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(neon1EndLabel, tConstraints);
		neon1End.setName("neon1End");
		neon1End.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 5;
		tConstraints.gridy = 3;
		tConstraints.insets = tInsets;
		add(neon1End, tConstraints);
		neon2StartLabel.setText("2 Start:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 6;
		tConstraints.gridy = 3;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(neon2StartLabel, tConstraints);
		neon2Start.setName("neon2Start");
		neon2Start.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 7;
		tConstraints.gridy = 3;
		tConstraints.insets = tInsets;
		add(neon2Start, tConstraints);
		neon2EndLabel.setText("End:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 8;
		tConstraints.gridy = 3;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(neon2EndLabel, tConstraints);
		neon2End.setName("neon2End");
		neon2End.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 9;
		tConstraints.gridy = 3;
		tConstraints.insets = tInsets;
		add(neon2End, tConstraints);
		sont1StartLabel.setText("SONT 1 Start:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 4;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(sont1StartLabel, tConstraints);
		sont1Start.setName("sont1Start");
		sont1Start.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 3;
		tConstraints.gridy = 4;
		tConstraints.insets = tInsets;
		add(sont1Start, tConstraints);
		sont1EndLabel.setText("End:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 4;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(sont1EndLabel, tConstraints);
		sont1End.setName("sont1End");
		sont1End.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 5;
		tConstraints.gridy = 4;
		tConstraints.insets = tInsets;
		add(sont1End, tConstraints);
		sont2StartLabel.setText("2 Start:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 6;
		tConstraints.gridy = 4;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(sont2StartLabel, tConstraints);
		sont2Start.setName("sont2Start");
		sont2Start.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 7;
		tConstraints.gridy = 4;
		tConstraints.insets = tInsets;
		add(sont2Start, tConstraints);
		sont2EndLabel.setText("End:");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 8;
		tConstraints.gridy = 4;
		tConstraints.anchor = GridBagConstraints.EAST;
		tConstraints.insets = tInsetsLabel;
		add(sont2EndLabel, tConstraints);
		sont2End.setName("sont2End");
		sont2End.setColumns(5);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 9;
		tConstraints.gridy = 4;
		tConstraints.insets = tInsets;
		add(sont2End, tConstraints);
		setProgram.setText("Set");
		setProgram.addActionListener(setProgListener);
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 10;
		tConstraints.gridy = 3;
		tConstraints.gridheight = 2;
		tConstraints.insets = tInsets;
		add(setProgram, tConstraints);
	}

	public void newProg(ProgData progdata) {
		neon1Start.setText(HauptFenster.format(progdata.getNeon1StartH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getNeon1StartM(), 2, '0', null));
		neon1End.setText(HauptFenster.format(progdata.getNeon1EndH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getNeon1EndM(), 2, '0', null));
		neon2Start.setText(HauptFenster.format(progdata.getNeon2StartH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getNeon2StartM(), 2, '0', null));
		neon2End.setText(HauptFenster.format(progdata.getNeon2EndH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getNeon2EndM(), 2, '0', null));
		sont1Start.setText(HauptFenster.format(progdata.getSont1StartH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getSont1StartM(), 2, '0', null));
		sont1End.setText(HauptFenster.format(progdata.getSont1EndH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getSont1EndM(), 2, '0', null));
		sont2Start.setText(HauptFenster.format(progdata.getSont2StartH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getSont2StartM(), 2, '0', null));
		sont2End.setText(HauptFenster.format(progdata.getSont2EndH(), 2, '0', ":")
				+ HauptFenster.format(progdata.getSont2EndM(), 2, '0', null));
		new TimeWindow(neon1Start, neon1End);
		new TimeWindow(neon2Start, neon2End);
		new TimeWindow(sont1Start, sont1End);
		new TimeWindow(sont2Start, sont2End);
	}

	public void newStatus(MonitorStatus monitorstatus) {
		temperatureValue.setText(HauptFenster.format(monitorstatus.getTemperature(), 3, ' ', null));
		humidityValue.setText(HauptFenster.format(monitorstatus.getHumidity(), 3, ' ', null));
	}

	public void nextSecond(boolean isStatusExpected) {
	}

	public void response(byte response) {
		// TODO Auto-generated method stub

	}

}
