package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import dna.series.aggdata.AggregatedValue;
import dna.util.Log;

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
		if (dataType.equals(TableFlag.all))
			this.dataType = TableFlag.Average;
		else
			this.dataType = dataType;
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

		line = TexUtils.textBf("DataType =") + TexTable.tableDelimiter
				+ TexUtils.textBf(this.dataType.toString());
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

	/** Adds a new row with the values. **/
	public void addDataRow(AggregatedValue[] values, long timestamp)
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

		this.addRow(tempValues, timestamp);
	}
}
