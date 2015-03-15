package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dna.util.Config;
import dna.util.MathHelper;

/** A TexTable for multiscalar values. **/
public class MultiScalarTexTable extends TexTable {

	private long timestamp;

	// constructor
	public MultiScalarTexTable(TexFile parent, String[] headRow,
			long timestamp, SimpleDateFormat dateFormat,
			TableFlag... tableFlags) throws IOException {
		super(parent, headRow, dateFormat, tableFlags);
		this.timestamp = timestamp;
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

		line = TexUtils.textBf("Timestamp =") + TexTable.tableDelimiter
				+ TexUtils.textBf(tempTimestamp);
		for (int i = 2; i < headRow.length; i++) {
			line += TexTable.tableDelimiter;
		}
		line += "\\# " + this.tableCounter;
		this.tableCounter++;
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
		this.writeLine(line);
		this.addHorizontalLine();
	}

	/** Adds a data row with the given index. **/
	public void addRow(double[] values, int index) throws IOException {
		String line = "\t" + index + TexTable.tableDelimiter;
		for (int i = 0; i < values.length; i++) {
			String value = "" + values[i];

			// if formatting is on, format
			if (Config.getBoolean("LATEX_DATA_FORMATTING"))
				value = MathHelper.format(values[i]);

			if (i == values.length - 1)
				line += value + " " + TexUtils.newline + " " + TexTable.hline;
			else
				line += value + TexTable.tableDelimiter;
		}
		this.writeLine(line);
	}

	/** Adds a blank row with the given index. **/
	public void addBlankRow(int rows, int index) throws IOException {
		String line = "\t" + index + TexTable.tableDelimiter;
		for (int i = 0; i < rows; i++) {
			if (i == rows - 1)
				line += "-" + " " + TexUtils.newline + " " + TexTable.hline;
			else
				line += "-" + TexTable.tableDelimiter;
		}
		this.writeLine(line);
	}

	/** Writes a line. **/
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
			if ((this.horizontalTableCounter + 1) * this.columns >= Config
					.getInt("LATEX_TABLE_MAX_COLUMNS")) {
				this.parent.writeLine();
				this.horizontalTableCounter = 0;
			}

			// reset counter
			this.lineCounter = 0;

			// begin new table
			this.begin(this.headRow, this.timestamp);
			this.writeLine(line);
		}
	}

}
