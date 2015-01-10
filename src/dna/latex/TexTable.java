package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dna.util.Config;
import dna.util.Log;
import dna.util.MathHelper;

/** Represents a table in a tex document. **/
public class TexTable {
	// static textable fields
	private static final String hline = "\\hline";
	private static final String defaultColumnSetting = "l";
	private static final String tableDelimiter = " & ";
	private static final long unsetLong = -1;

	// table flags
	public static enum TableFlag {
		Average, Min, Max, Median, Var, VarLow, VarUp, ConfLow, ConfUp, all
	};

	// variables
	private TexFile parent;
	private boolean open;
	private int columns;
	private TableFlag[] tableFlags;
	private SimpleDateFormat dateFormat;

	// constructor
	public TexTable(TexFile parent, String[] headRow, long timestamp,
			TableFlag... tableFlags) throws IOException {
		this.parent = parent;
		this.columns = headRow.length;
		this.open = true;
		this.tableFlags = tableFlags;
		this.begin(headRow, timestamp);
	}

	public TexTable(TexFile parent, String[] headRow) throws IOException {
		this(parent, headRow, unsetLong, TableFlag.all);
	}

	public TexTable(TexFile parent, String[] headRow,
			SimpleDateFormat dateFormat) throws IOException {
		this(parent, headRow);
		this.dateFormat = dateFormat;
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

		// if timestamp set, add timestamp row
		if (timestamp != unsetLong) {
			line = TexUtils.textBf("Timestamp =") + TexTable.tableDelimiter
					+ TexUtils.textBf("" + timestamp);
			for (int i = 2; i < headRow.length; i++) {
				line += TexTable.tableDelimiter;
			}
			line += TexUtils.newline + TexTable.hline;
			this.parent.writeLine(line);
		}

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
		if (timestamp != unsetLong)
			this.parent.writeLine(TexTable.hline);
	}

	private void addHorizontalLine() throws IOException {
		if (open) {
			this.parent.writeLine(TexTable.hline);
		} else {
			Log.warn("Attempt to write to closed TexTable" + this.toString()
					+ "!");
		}
	}

	public boolean isOpen() {
		return this.open;
	}

	public void close() throws IOException {
		if (open) {
			this.parent.writeLine(TexUtils.end("tabular"));
		} else {
			Log.warn("Attempt to close TexTable '" + this.toString()
					+ "' but its already closed!");
		}

		this.open = false;
	}

	public void addRow(double[] values) throws IOException {
		if (open) {
			String line = "\t";
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

	public void addRow(double[] values, long timestamp) throws IOException {
		String tempTimestamp = "" + timestamp;

		// if dateFormat is set, transform timestamp
		if (this.dateFormat != null)
			tempTimestamp = this.dateFormat.format(new Date(timestamp));

		if (open) {
			String line = "\t" + tempTimestamp + TexTable.tableDelimiter;
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

	public void addBlankRow(int rows, long timestamp) throws IOException {
		String tempTimestamp = "" + timestamp;

		// if dateFormat is set, transform timestamp
		if (this.dateFormat != null)
			tempTimestamp = this.dateFormat.format(new Date(timestamp));

		if (open) {
			String line = "\t" + tempTimestamp + TexTable.tableDelimiter;
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

	public TableFlag[] getTableFlags() {
		return this.tableFlags;
	}

}
