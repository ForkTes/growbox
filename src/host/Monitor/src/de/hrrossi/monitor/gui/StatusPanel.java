package de.hrrossi.monitor.gui;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hrrossi.monitor.tech.MonitorStatus;
import de.hrrossi.monitor.tech.MonitorStatusListener;
import de.hrrossi.monitor.tech.ProgData;
import de.hrrossi.monitor.tech.SensorAdapter;
import de.hrrossi.monitor.tech.SensorAdapter.Values;


public class StatusPanel extends JPanel implements MonitorStatusListener {

	/**  */
	private static final long serialVersionUID = 6713153734009677026L;

	private static interface StatusRunnable extends Runnable {

		public void setStatus(MonitorStatus monitorstatus);

	}

	private JLabel temperature;

	private JLabel humidity;

	private JLabel neon;

	private JLabel sodium;

	private JLabel vent;

	private JLabel programLabel;

	private JLabel program;

	private JLabel dayLabel;

	private JLabel day;

	private JLabel time;

	private JLabel lasttime;

	private int days, hours, minutes, seconds;

	private boolean statusIn;

	private StatusRunnable runner;

	public StatusPanel() {
		super(new GridBagLayout());
		temperature = new JLabel();
		humidity = new JLabel();
		neon = new JLabel();
		sodium = new JLabel();
		vent = new JLabel();
		programLabel = new JLabel();
		program = new JLabel();
		dayLabel = new JLabel();
		day = new JLabel();
		time = new JLabel();
		lasttime = new JLabel();
		runner = new StatusRunnable() {

			private MonitorStatus status;

			public void setStatus(MonitorStatus aStatus) {
				status = aStatus;
			}

			public void run() {
				statusIn = true;
				time.setForeground(Color.GREEN);
				Values tValues = SensorAdapter.getValues(status.getTemperature(), status.getHumidity());
				program.setText(String.valueOf(status.getProgram() + 1));
				days = status.getDays();
				hours = status.getHours();
				minutes = status.getMinutes();
				seconds = status.getSeconds();
				lasttime.setText((new StringBuilder(String.valueOf(HauptFenster.format(hours, 2, ' ', ":"))))
						.append(HauptFenster.format(minutes, 2, '0', ":"))
						.append(HauptFenster.format(seconds, 2, '0', null)).toString());
				setTime(days, hours, minutes, seconds);
				temperature.setText(HauptFenster.format(tValues.getTemperature(), 2, ' ', "\260C"));
				humidity.setText(HauptFenster.format(tValues.getHumidity(), 2, ' ', "%rH"));
				setWarningIndicator(vent, status.isVent(), status.isForceVent());
				setIndicator(neon, status.isNeon());
				setIndicator(sodium, status.isSodium());
				setWarningIndicator(temperature, true, status.isTemperatureError());
				setWarningIndicator(humidity, true, status.isHumidityError());
			}
		};
		setParameters();
		addComponents();
	}

	public void nextSecond(boolean isStatusExpected) {
		seconds++;
		if (seconds > 59) {
			minutes++;
			seconds = 0;
		}
		if (minutes > 59) {
			hours++;
			minutes = 0;
		}
		if (hours > 23) {
			days++;
			hours = 0;
		}
		if (isStatusExpected) {
			if (statusIn) {
				statusIn = false;
			} else {
				time.setForeground(Color.YELLOW);
			}
		}
		setTime(days, hours, minutes, seconds);
	}

	private void setTime(int days, int hours, int minutes, int seconds) {
		day.setText(HauptFenster.format(days, 3, ' ', null));
		time.setText((new StringBuilder(String.valueOf(HauptFenster.format(hours, 2, ' ', ":"))))
				.append(HauptFenster.format(minutes, 2, '0', ":")).append(HauptFenster.format(seconds, 2, '0', null))
				.toString());
	}

	private void setParameters() {
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(400, 110));
		setMinimumSize(new Dimension(400, 110));
	}

	private void addComponents() {
		Font tLabelFont = Font.decode("Arial 10");
		Font tEnvFont = Font.decode("Lucida Console 20");
		Font tWertFont = Font.decode("Lucida Console 48");
		Insets tInsets = new Insets(4, 8, 4, 8);
		neon.setForeground(Color.DARK_GRAY);
		neon.setFont(tLabelFont);
		neon.setText("Neon");
		GridBagConstraints tConstraints = new GridBagConstraints();
		tConstraints.gridx = 0;
		tConstraints.gridy = 0;
		tConstraints.insets = tInsets;
		add(neon, tConstraints);
		sodium.setForeground(Color.DARK_GRAY);
		sodium.setFont(tLabelFont);
		sodium.setText("Sodium");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 1;
		tConstraints.gridy = 0;
		tConstraints.insets = tInsets;
		add(sodium, tConstraints);
		vent.setForeground(Color.DARK_GRAY);
		vent.setFont(tLabelFont);
		vent.setText("Vent");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 0;
		tConstraints.insets = tInsets;
		add(vent, tConstraints);
		temperature.setForeground(Color.GREEN);
		temperature.setFont(tEnvFont);
		temperature.setText("--\260C");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 3;
		tConstraints.gridy = 0;
		tConstraints.weightx = 0.5d;
		tConstraints.insets = tInsets;
		add(temperature, tConstraints);
		humidity.setForeground(Color.GREEN);
		humidity.setFont(tEnvFont);
		humidity.setText("--%rH");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 0;
		tConstraints.weightx = 0.5d;
		tConstraints.insets = tInsets;
		add(humidity, tConstraints);
		programLabel.setForeground(Color.GREEN);
		programLabel.setFont(tLabelFont);
		programLabel.setText("Prg");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 0;
		tConstraints.gridy = 1;
		tConstraints.insets = tInsets;
		add(programLabel, tConstraints);
		dayLabel.setForeground(Color.GREEN);
		dayLabel.setFont(tLabelFont);
		dayLabel.setText("Day");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 1;
		tConstraints.gridy = 1;
		tConstraints.insets = tInsets;
		add(dayLabel, tConstraints);
		lasttime.setForeground(Color.GREEN);
		lasttime.setFont(tLabelFont);
		lasttime.setText("--:--:--");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 4;
		tConstraints.gridy = 1;
		tConstraints.insets = tInsets;
		tConstraints.anchor = GridBagConstraints.EAST;
		add(lasttime, tConstraints);
		program.setForeground(Color.GREEN);
		program.setHorizontalAlignment(4);
		program.setFont(tWertFont);
		program.setText("-");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 0;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(program, tConstraints);
		day.setForeground(Color.GREEN);
		day.setHorizontalAlignment(4);
		day.setFont(tWertFont);
		day.setText("---");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 1;
		tConstraints.gridy = 2;
		tConstraints.insets = tInsets;
		add(day, tConstraints);
		time.setForeground(Color.GREEN);
		time.setFont(tWertFont);
		time.setText("--:--:--");
		tConstraints = new GridBagConstraints();
		tConstraints.gridx = 2;
		tConstraints.gridy = 2;
		tConstraints.gridwidth = GridBagConstraints.REMAINDER;
		tConstraints.insets = tInsets;
		add(time, tConstraints);
	}

	public void newStatus(MonitorStatus aStatus) {
		runner.setStatus(aStatus);
		EventQueue.invokeLater(runner);
	}

	public void newProg(ProgData progdata) {
	}

	public void response(byte response) {
	}

	private void setIndicator(JComponent aComponent, boolean isActive) {
		if (isActive) {
			aComponent.setForeground(Color.GREEN);
		} else {
			aComponent.setForeground(Color.DARK_GRAY);
		}
	}

	private void setWarningIndicator(JComponent aComponent, boolean isActive, boolean isWarning) {
		if (isWarning) {
			aComponent.setForeground(Color.RED);
		} else if (isActive) {
			aComponent.setForeground(Color.GREEN);
		} else {
			aComponent.setForeground(Color.DARK_GRAY);
		}
	}

}
