package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import dna.util.Config;
import dna.util.MathHelper;
import dna.util.expr.Expr;
import dna.util.expr.Parser;
import dna.util.expr.SyntaxException;
import dna.util.expr.Variable;

/** Represents a table in a tex document. **/
public class TexTable {
	// static textable fields
	protected static final String hline = "\\hline";
	protected static final String defaultColumnSetting = "l";
	protected static final String tableNumberSetting = "r";
	protected static final String tableDelimiter = " & ";
	protected static final long unsetLong = -1;

	// table mode enum
	public static enum TableMode {
		alternatingSeries, alternatingValues
	};

	// table flags
	public static enum TableFlag {
		Average, Min, Max, Median, Var, VarLow, VarUp, ConfLow, ConfUp, all
	};

	// variables
	protected TexFile parent;
	protected TableFlag[] tableFlags;
	protected String[] headRow;
	protected SimpleDateFormat dateFormat;
	protected String scaling;
	protected HashMap<Long, Long> map;

	// alignment variables
	protected int columns;
	protected int lineCounter;
	protected int horizontalTableCounter;
	protected int tableCounter;

	// constructor
	public TexTable(TexFile parent, String[] headRow,
			SimpleDateFormat dateFormat, TableFlag... tableFlags)
			throws IOException {
		this.parent = parent;
		this.headRow = headRow;
		this.columns = headRow.length;
		this.dateFormat = dateFormat;
		this.tableFlags = tableFlags;
		this.lineCounter = 0;
		this.horizontalTableCounter = 0;
		this.tableCounter = 0;
	}

	public TexTable(TexFile parent, String[] headRow,
			SimpleDateFormat dateFormat) throws IOException {
		this(parent, headRow, dateFormat, TableFlag.all);
	}

	public TexTable(TexFile parent, String[] headRow,
			SimpleDateFormat dateFormat, String scaling,
			HashMap<Long, Long> mapping) throws IOException {
		this(parent, headRow, dateFormat);
		this.scaling = scaling;
		this.map = mapping;
	}

	// class methods
	/** Begins the table, writes the head row etc. **/
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

	protected void addHorizontalLine() throws IOException {
		this.parent.writeLine(TexTable.hline);
	}

	public void close() throws IOException {
		this.parent.writeLine(TexUtils.end("tabular"));
	}

	public void addRow(double[] values) throws IOException {
		String line = "\t";
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

	public void addRow(double[] values, long timestamp) throws IOException {
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

		String line = "\t" + tempTimestamp + TexTable.tableDelimiter;
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

	public void addBlankRow(int rows, long timestamp) throws IOException {
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

		String line = "\t" + tempTimestamp + TexTable.tableDelimiter;
		for (int i = 0; i < rows; i++) {
			if (i == rows - 1)
				line += "-" + " " + TexUtils.newline + " " + TexTable.hline;
			else
				line += "-" + TexTable.tableDelimiter;
		}
		this.writeLine(line);
	}

	public TableFlag[] getTableFlags() {
		return this.tableFlags;
	}

	/** Scales the timestamp according to the expression. **/
	public static long scaleTimestamp(long timestamp, String expression) {
		// parse expression
		Expr expr = null;
		try {
			expr = Parser.parse(expression);
		} catch (SyntaxException e) {
			// print what went wrong
			if (Config.getBoolean("CUSTOM_PLOT_EXPLAIN_EXPRESSION_FAILURE"))
				System.out.println(e.explain());
			else
				e.printStackTrace();
		}

		// define variable
		Variable v = Variable.make(Config.get("LATEX_SCALING_VARIABLE"));
		v.setValue(timestamp);

		// return
		return (long) expr.value();
	}

	/** Returns the horizontal table counter. **/
	public int getHorizontalTableCounter() {
		return this.horizontalTableCounter;
	}

	/** Returns the table counter. **/
	public int getTableCounter() {
		return this.tableCounter;
	}
}
