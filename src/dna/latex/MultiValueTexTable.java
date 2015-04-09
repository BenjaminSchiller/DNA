package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import dna.series.aggdata.AggregatedValue;
import dna.util.Config;
import dna.util.Log;
import dna.util.MathHelper;

/** A TexTable which compares multiple values. **/
public class MultiValueTexTable extends TexTable {

	private TableFlag dataType;

	// constructor
	public MultiValueTexTable(TexFile parent, String[] headRow,
			SimpleDateFormat dateFormat, String scaling,
			HashMap<Long, Long> mapping, TableFlag dataType) throws IOException {
		super(parent, headRow, dateFormat, dataType);
		this.scaling = scaling;
		this.map = mapping;
		if (dataType != null) {
			if (dataType.equals(TableFlag.all))
				this.dataType = TableFlag.Average;
			else
				this.dataType = dataType;
		}
		this.begin(headRow);
	}

	/** Begins the table. **/
	protected void begin(String[] headRow) throws IOException {
		String line = TexUtils.begin("tabular") + "{" + "|";
		for (int i = 0; i < headRow.length; i++) {
			line += TexTable.defaultColumnSetting + "|";
			if (i == 0)
				line += "|";
		}
		line += "}";
		this.parent.writeLine(line);
		this.addHorizontalLine();

		if (this.dataType == null)
			line = "";
		else
			line = TexUtils.textBf(this.dataType.toString());

		for (int i = 1; i < headRow.length; i++) {
			line += TexTable.tableDelimiter;
		}
		this.tableCounter++;
		line += "\\# " + this.tableCounter;
		line += TexUtils.newline + TexTable.hline;
		this.writeLine(line);

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
		this.writeLine(line);
		this.addHorizontalLine();
	}

	/** Adds a data row to the table. Null values will be added as '-'. **/
	public void addDataRow(AggregatedValue[] values, long timestamp)
			throws IOException {
		this.addDataRow(values, timestamp, this.dataType);
	}

	/** Adds a data row to the table. Null values will be added as '-'. **/
	public void addDataRow(AggregatedValue[] values, long timestamp,
			TableFlag dataType) throws IOException {
		TableFlag[] tempArray = new TableFlag[values.length];
		for (int i = 0; i < values.length; i++) {
			tempArray[i] = dataType;
		}
		this.addDataRow(values, timestamp, tempArray);
	}

	/** Adds a data row to the table. Null values will be added as '-'. **/
	public void addDataRow(AggregatedValue[] values, long timestamp,
			TableFlag[] dataType) throws IOException {
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

		String buff = "\t" + tempTimestamp + TexTable.tableDelimiter;
		for (int i = 0; i < values.length; i++) {

			String value = "";

			if (values[i] == null) {
				value = "-";
			} else {
				double temp = 0.0;

				switch (dataType[i]) {
				case Average:
					temp = values[i].getAvg();
					break;
				case ConfLow:
					temp = values[i].getConfidenceLow();
					break;
				case ConfUp:
					temp = values[i].getConfidenceUp();
					break;
				case Max:
					temp = values[i].getMax();
					break;
				case Median:
					temp = values[i].getMedian();
					break;
				case Min:
					temp = values[i].getMin();
					break;
				case Var:
					temp = values[i].getVariance();
					break;
				case VarLow:
					temp = values[i].getVarianceLow();
					break;
				case VarUp:
					temp = values[i].getVarianceUp();
					break;
				case all:
					Log.warn("MultiValueTexTable: wrong flag! Adding 0.0");
					temp = 0.0;
					break;
				}

				value = "" + temp;

				// if formatting is on, format
				if (Config.getBoolean("LATEX_DATA_FORMATTING"))
					value = MathHelper.format(temp);
			}

			if (i == values.length - 1)
				buff += value + " " + TexUtils.newline + " " + TexTable.hline;
			else
				buff += value + TexTable.tableDelimiter;
		}
		this.writeLine(buff);
	}

	protected void writeLine(String line) throws IOException {
		// only write line if max lines is not exceeded
		if (this.lineCounter < Config.getInt("LATEX_TABLE_MAX_LINES")) {
			this.lineCounter++;
			this.parent.writeLine(line);
		} else {
			// to many lines, start new table
			this.close();
			this.horizontalTableCounter++;

			// align multiple tables with each other
			if ((this.horizontalTableCounter + 1) * this.columns > Config
					.getInt("LATEX_TABLE_MAX_COLUMNS")) {
				this.parent.writeLine();
				this.horizontalTableCounter = 0;
			}

			// reset counter
			this.lineCounter = 0;

			// begin new table
			this.begin(this.headRow);
			this.writeLine(line);
		}
	}
}
