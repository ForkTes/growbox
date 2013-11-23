package de.hrrossi.monitor.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class HauptFenster extends JFrame {

	/**  */
	private static final long serialVersionUID = -2347493061201870507L;

	public static final int MIN_WIDTH = 400;

	private JPanel contentPane;

	private StatusPanel statusPanel;

	private ControlPanel controlPanel;

	public HauptFenster() {
		super("Monitor");
		contentPane = new JPanel();
		statusPanel = new StatusPanel();
		controlPanel = new ControlPanel();
		setParameters();
		addCompononts();
	}

	private void setParameters() {
		setDefaultCloseOperation(3);
		contentPane.setLayout(new BoxLayout(contentPane, 1));
		setContentPane(contentPane);
	}

	private void addCompononts() {
		contentPane.add(statusPanel);
		contentPane.add(controlPanel);
		pack();
	}

	public StatusPanel getStatusPanel() {
		return statusPanel;
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}

	public void start() {
		setVisible(true);
	}

	public static String format(int aZahl, int aStellen, char aFiller, String aUnit) {
		StringBuilder tZahl = new StringBuilder(aStellen);
		tZahl.append(aZahl);
		int tOversize = tZahl.length() - aStellen;
		if (aUnit != null) {
			tZahl.append(aUnit);
		}
		if (tOversize > 0) {
			return tZahl.substring(tOversize);
		}
		if (tOversize < 0) {
			char tChars[] = new char[-tOversize];
			for (; tOversize != 0; tOversize++) {
				tChars[-tOversize - 1] = aFiller;
			}

			tZahl.insert(0, tChars);
		}
		return tZahl.toString();
	}

}
