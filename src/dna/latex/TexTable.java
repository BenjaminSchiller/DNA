package dna.latex;

import java.io.IOException;

import dna.io.Writer;
import dna.util.Log;

/** Represents a table in a tex document. **/
public class TexTable {

	private static final String hline = "\\hline";
	private static final String defaultColumnSetting = "l";
	private static final String tableDelimiter = " & ";

	// variables
	private TexFile parent;
	private boolean open;

	private int columns;

	// constructor
	public TexTable(TexFile parent, String[] headRow) throws IOException {
		this.parent = parent;
		this.columns = headRow.length;
		this.open = true;
		this.begin(headRow);
	}

	// class methods
	private void begin(String[] headRow) throws IOException {
		String line = TexUtils.begin("tabular") + "{";
		for (int i = 0; i < headRow.length; i++) {
			line += TexTable.defaultColumnSetting + "|";
			if (i == 0)
				line += "|";
		}
		line += "}";
		this.parent.writeLine(line);
		this.addHorizontalLine();

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
				if (i == values.length - 1)
					line += values[i] + " " + TexUtils.newline + " "
							+ TexTable.hline;
				else
					line += values[i] + TexTable.tableDelimiter;
			}
			this.parent.writeLine(line);
		} else {
			Log.warn("Attempt to write to closed TexTable" + this.toString()
					+ "!");
		}
	}

	public void addRow(double[] values, long timestamp) throws IOException {
		if (open) {
			String line = "\t" + timestamp + TexTable.tableDelimiter;
			for (int i = 0; i < values.length; i++) {
				if (i == values.length - 1)
					line += values[i] + " " + TexUtils.newline + " "
							+ TexTable.hline;
				else
					line += values[i] + TexTable.tableDelimiter;
			}
			this.parent.writeLine(line);
		} else {
			Log.warn("Attempt to write to closed TexTable" + this.toString()
					+ "!");
		}
	}

}
