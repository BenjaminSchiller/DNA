package dna.series.aggdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.io.filesystem.Files;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.util.ArrayUtils;

/**
 * AggregatedNodeValueList is a class containing the values of an aggregated
 * NodeValueList. It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max,
 * median, variance, variance-low, variance-up, confidence-low, confidence-up,
 * sort-order } Note: Sort-order fields are for plotting purposes only.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedNodeValueList extends AggregatedData {

	// member variables
	private AggregatedValue[] values;
	private int[] sortIndex;

	// constructors
	public AggregatedNodeValueList(String name) {
		super(name);
	}

	public AggregatedNodeValueList(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}

	// average, median, minimum, maximum, variance, varianceLow, varianceUp
	// confidence1, confidence2
	// set methods
	public void setSort(NodeValueListOrderBy sortBy,
			NodeValueListOrder sortOrder) {

		double[] tempValues = new double[this.values.length];
		int[] sortedIndex = new int[this.values.length];

		switch (sortBy) {

		case average:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getAvg();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getAvg()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case median:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getMedian();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getMedian()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case minimum:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getMin();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getMin()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case maximum:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getMax();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getMax()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case variance:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getVariance();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getVariance()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case varianceLow:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getVarianceLow();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getVarianceLow()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case varianceUp:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getVarianceUp();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getVarianceUp()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case confidenceLow:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getConfidenceLow();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getConfidenceLow()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;
		case confidenceUp:
			for (int i = 0; i < this.values.length; i++) {
				tempValues[i] = this.values[i].getConfidenceUp();
			}
			Arrays.sort(tempValues);
			for (int i = 0; i < tempValues.length; i++) {
				for (int j = 0; j < this.values.length; j++) {
					if (tempValues[i] == this.values[j].getConfidenceUp()) {
						if (!ArrayUtils.isIncluded(sortedIndex, j))
							sortedIndex[i] = j;
					}
				}
			}
			break;

		}

		switch (sortOrder) {

		case ascending:
			this.sortIndex = sortedIndex;
			break;
		case descending:
			int[] reverse = new int[sortedIndex.length];
			for (int i = 0; i < sortedIndex.length; i++) {
				reverse[reverse.length - (i + 1)] = sortedIndex[i];
			}
			this.sortIndex = reverse;
			break;
		}
	}

	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}

	public String getName() {
		return super.getName();
	}

	public int[] getSortIndex() {
		return this.sortIndex;
	}

	// IO methods
	/**
	 * @param dir
	 *            String which contains the path to the directory the
	 *            AggregatedNodeValueList will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the Distribution will be read
	 *            from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            AggregatedNodeValueList will be created.
	 */
	public static AggregatedNodeValueList read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedNodeValueList(name);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<AggregatedValue> list = new ArrayList<AggregatedValue>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.aggregatedDataDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			double[] tempDouble = new double[temp.length - 1];
			for (int i = 0; i < tempDouble.length; i++) {
				tempDouble[i] = Double.parseDouble(temp[i + 1]);
			}

			AggregatedValue tempV = new AggregatedValue(name + temp[0],
					tempDouble);
			list.add(tempV);
			index++;
		}
		AggregatedValue[] values = new AggregatedValue[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();

		return new AggregatedNodeValueList(Files.getNodeValueListName(name),
				values);
	}

	public void write(String dir, String filename) throws IOException {
		this.write(dir, filename, false);
	}

	public void write(String dir, String filename, boolean writeSortIndex)
			throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = this.getValues();

		int counter = 0;
		for (AggregatedValue aggData : tempData) {
			String temp = "" + (int) aggData.getValues()[0]
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
			}
			if (writeSortIndex)
				temp += Keywords.aggregatedDataDelimiter
						+ this.getSortIndex()[counter];
			w.writeln(temp);
			counter++;
		}
		w.close();
	}

	public static void write(String dir, String filename, double[][] values)
			throws IOException {
		Writer w = new Writer(dir, filename);

		for (int i = 0; i < values.length; i++) {
			String temp = "";
			for (int j = 0; j < values[i].length; j++) {
				if (j == 0)
					temp += (int) values[i][j]
							+ Keywords.aggregatedDataDelimiter;
				else {
					if (j == values[i].length - 1)
						temp += values[i][j];
					else
						temp += values[i][j] + Keywords.aggregatedDataDelimiter;
				}
			}
			w.writeln(temp);
		}
		w.close();
	}

}
