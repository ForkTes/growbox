package de.hrrossi.monitor.tech;

public class SensorAdapter {

	public static class RawData {

		private int sensorT;

		private int sensorH;

		public int getSensorT() {
			return sensorT;
		}

		public int getSensorH() {
			return sensorH;
		}

	}

	public static class Values {

		private int temperature;

		private int humidity;

		public int getTemperature() {
			return temperature;
		}

		public int getHumidity() {
			return humidity;
		}

	}

	public static Values getValues(int aSensorT, int aSensorH) {
		Values tValues = new Values();

		// Auswertung NE555 Sensor
		//tValues.temperature = (17 * (aSensorT - 144)) / 40 + 18;
		//tValues.humidity = (65 * (128 - aSensorH)) / 117 + 30;

		// Auswertung SHT-11
		double tT = aSensorT, tH, tSH = aSensorH;
		tT = -39.64d + 0.64d * tT;
		tValues.temperature = (int)(tT + 0.5d);
		tH = -4.0d + 0.648d * tSH - 0.00072d * tSH * tSH + (tT - 25.0d) * (0.01d + 0.00128d * tSH);
		tValues.humidity = (int)(tH + 0.5d);

		return tValues;
	}

	public static RawData getRawData(int aTemperature, int aHumidity) {
		RawData tRawData = new RawData();

		// Auswertung SHT-11
		double tT = aTemperature, tST, tSH = aHumidity, tHelp;
		tST = (tT + 39.64d) / 0.64d;
		tRawData.sensorT = (int)(tST + 0.5d);
		tHelp = (0.616d + 0.00128d * tT) / 0.00144d;
		tSH = tHelp - Math.sqrt(tHelp * tHelp - (4.25d + tSH - 0.01d * tT) / 0.00072d);
		tRawData.sensorH = (int)(tSH + 0.5d);

		return tRawData;
	}

}
