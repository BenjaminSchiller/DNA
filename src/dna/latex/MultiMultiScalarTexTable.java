package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dna.series.aggdata.AggregatedValue;
import dna.util.Config;
import dna.util.Log;
import dna.util.MathHelper;

/** A TexTable for multiple multiscalar values. **/
public class MultiMultiScalarTexTable extends TexTable {

	private TableFlag dataType;

	// constructor
	public MultiMultiScalarTexTable(TexFile parent, String[] headRow,
			long timestamp, SimpleDateFormat dateFormat, TableFlag dataType)
			throws IOException {
		super(parent, headRow, dateFormat, dataType);
		if (dataType.equals(TableFlag.all))
			this.dataType = TableFlag.Average;
		else
			this.dataType = dataType;
		this.begin(headRow, timestamp);
	}

	// class methods
	/** Begins the table, writes the head row etc. **/
	private void begin(String[] headRow, long timestamp) throws IOException {
		String line = TexUtils.begin("tabular") + "{" + "|";
		for (int i = 0; i < headRow.length; i++) {
			line += TexTable.defaultColumnSetting + "|";
			if (i == 0)
				line += "|";
		}
		line += "}";
		this.parent.writeLine(line);
		this.addHorizontalLine();

		// write datatype row
		line = TexUtils.textBf("DataType =") + TexTable.tableDelimiter
				+ TexUtils.textBf(this.dataType.toString());
		for (int i = 2; i < headRow.length; i++) {
			line += TexTable.tableDelimiter;
		}
		line += TexUtils.newline + TexTable.hline;
		this.parent.writeLine(line);

		// add timestamp row
		long tTimestamp = timestamp;

		// if mapping, map
		if (this.map != null) {
			if (this.map.containsKey(tTimestamp))
				tTimestamp = this.map.get(tTimestamp);
		}

		// if scaling, scale
		if (this.scaling != null)
			tTimestamp = TexTable.scaleTimestamp(tTimestamp, this.scaling);

		String tempTimestamp = "" + tTimestamp;

		// if dateFormat is set, transform timestamp
		if (this.dateFormat != null)
			tempTimestamp = this.dateFormat.format(new Date(tTimestamp));

		// write timestamp row
		line = TexUtils.textBf("Timestamp =") + TexTable.tableDelimiter
				+ TexUtils.textBf(tempTimestamp);
		for (int i = 2; i < headRow.length; i++) {
			line += TexTable.tableDelimiter;
		}
		line += TexUtils.newline + TexTable.hline;
		this.parent.writeLine(line);

		for (String s : headRow) {
			line = "\t";
			for (int i = 0; i < headRow.length; i++) {
				if (i == headRow.length - 1)
					line += TexUtils.textBf(headRow[i]) + " "
							+ TexUtils.newline + " " + TexTable.hline;
				else
					line += TexUtils.textBf(headRow[i])
							+ TexTable.tableDelimiter;
			}
		}
		this.parent.writeLine(line);
		this.parent.writeLine(TexTable.hline);
	}

	/** Adds a data row with the given index. **/
	public void addRow(double[] values, int index) throws IOException {
		if (open) {
			String line = "\t" + index + TexTable.tableDelimiter;
			for (int i = 0; i < values.length; i++) {
				String value = "" + values[i];

				// if formatting is on, format
				if (Config.getBoolean("LATEX_DATA_FORMATTING"))
					value = MathHelper.format(values[i]);

				if (i == values.length - 1)
					line += value + " " + TexUtils.newline + " "
							+ TexTable.hline;
				else
					line += value + TexTable.tableDelimiter;
			}
			this.parent.writeLine(line);
		} else {
			Log.warn("Attempt to write to closed TexTable" + this.toString()
					+ "!");
		}
	}

	/** Adds a blank row with the given index. **/
	public void addBlankRow(int rows, int index) throws IOException {
		if (open) {
			String line = "\t" + index + TexTable.tableDelimiter;
			for (int i = 0; i < rows; i++) {
				if (i == rows - 1)
					line += "-" + " " + TexUtils.newline + " " + TexTable.hline;
				else
					line += "-" + TexTable.tableDelimiter;
			}
			this.parent.writeLine(line);
		} else {
			Log.warn("Attempt to write to closed TexTable" + this.toString()
					+ "!");
		}
	}

	/** Adds a new row with the values. **/
	public void addDataRow(AggregatedValue[] values, int index)
			throws IOException {
		double[] tempValues = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			switch (this.dataType) {
			case Average:
				tempValues[i] = values[i].getAvg();
				break;
			case ConfLow:
				tempValues[i] = values[i].getConfidenceLow();
				break;
			case ConfUp:
				tempValues[i] = values[i].getConfidenceUp();
				break;
			case Max:
				tempValues[i] = values[i].getMax();
				break;
			case Median:
				tempValues[i] = values[i].getMedian();
				break;
			case Min:
				tempValues[i] = values[i].getMin();
				break;
			case Var:
				tempValues[i] = values[i].getVariance();
				break;
			case VarLow:
				tempValues[i] = values[i].getVarianceLow();
				break;
			case VarUp:
				tempValues[i] = values[i].getVarianceUp();
				break;
			case all:
				Log.warn("MultiValueTexTable: wrong flag! Adding 0.0");
				tempValues[i] = 0.0;
				break;
			}
		}

		this.addRow(tempValues, index);
	}

}
