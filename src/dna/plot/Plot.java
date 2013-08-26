package dna.plot;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedValue;
import dna.util.Execute;
import dna.util.Log;
import dna.util.Settings;

public class Plot {

	private String terminal = "png large";

	private String extension = "png";

	private String dir = null;

	private String filename = null;

	private String scriptFilename = null;

	private String key = null;

	private String title = null;

	private String xLabel = null;

	private boolean xLogscale = false;

	private String xRange = null;

	private double xOffset = 0.0;

	private String yLabel = null;

	private boolean yLogscale = false;

	private String yRange = null;

	private double yOffset = 0.0;

	private boolean grid = true;

	private int lw = 1;

	private PlotData[] data;

	private NodeValueListOrderBy orderBy;

	private NodeValueListOrder sortOrder;

	private DistributionPlotType distPlotType;

	private String dateTime = "%Y-%m-%d";

	private boolean plotDateTime = false;

	public Plot(PlotData[] data, String dir, String filename,
			String scriptFilename) {
		this(data, dir, filename, scriptFilename,
				Plotting.defaultDistributionPlotType,
				Plotting.defaultNodeValueListOrderBy,
				Plotting.defaultNodeValueListOrder);
	}

	public Plot(PlotData[] data, String dir, String filename,
			String scriptFilename, DistributionPlotType distPlotType) {
		this(data, dir, filename, scriptFilename, distPlotType,
				Plotting.defaultNodeValueListOrderBy,
				Plotting.defaultNodeValueListOrder);
	}

	public Plot(PlotData[] data, String dir, String filename,
			String scriptFilename, DistributionPlotType distPlotType,
			NodeValueListOrderBy orderBy, NodeValueListOrder sortOrder) {
		this.data = data;
		this.dir = dir;
		this.filename = filename;
		this.scriptFilename = scriptFilename;
		this.distPlotType = distPlotType;
		this.orderBy = orderBy;
		this.sortOrder = sortOrder;
	}

	// old
	public void write(String dir, String filename) throws IOException {
		Writer writer = new Writer(dir, filename);
		List<String> script = this.getScript();
		for (String line : script) {
			writer.writeln(line);
		}
		writer.close();
	}

	/**
	 * Writes the gnuplot script for an array of AggregatedNodeValueList objects
	 * to the destination directory
	 * 
	 * @param dir
	 *            destination directory
	 * @param filename
	 *            script filename
	 * @param data
	 *            input data gathered in Plotting.plotDistribution
	 * @param sortBy
	 *            argument the NodeValueList will be sorted by
	 * @param sortOrder
	 *            sorting order
	 * @throws IOException
	 *             thrown by the writer
	 */
	public void writeScript(String dir, String filename,
			AggregatedNodeValueList[] data) throws IOException {
		Writer w = new Writer(dir, filename);

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}

		// write script data
		if (data[0].getSortIndex() == null) {
			for (int i = 0; i < data.length; i++) {
				AggregatedValue[] tempValues = data[i].getValues();

				for (int j = 0; j < tempValues.length; j++) {
					String temp = "" + j;
					double[] values = tempValues[j].getValues();
					for (int k = 0; k < values.length; k++) {
						temp += Keywords.plotDataDelimiter + values[k];
					}
					w.writeln(temp);
				}
				// end-of-file indicates the end of this nodevaluelist data
				w.writeln("EOF");
			}
		} else {
			for (int i = 0; i < data.length; i++) {
				AggregatedValue[] tempValues = data[i].getValues();

				for (int j = 0; j < tempValues.length; j++) {
					String temp = "" + j;
					double[] values = tempValues[data[i].getSortIndex()[j]]
							.getValues();
					for (int k = 0; k < values.length; k++) {
						temp += Keywords.plotDataDelimiter + values[k];
					}
					w.writeln(temp);
				}
				// end-of-file indicates the end of this nodevaluelist data
				w.writeln("EOF");
			}
		}
		w.close();
	}

	/**
	 * Writes the gnuplot script for an array of AggregatedDistribution objects
	 * to the destination directory
	 * 
	 * @param dir
	 *            destination directory
	 * @param filename
	 *            script filename
	 * @param data
	 *            input data gathered in Plotting.plotDistribution
	 * @throws IOException
	 *             thrown by the writer
	 */
	public void writeScript(String dir, String filename,
			AggregatedDistribution[] data) throws IOException {
		Writer w = new Writer(dir, filename);

		// write script header
		switch (this.distPlotType) {
		case distOnly:
			List<String> scriptDist = this.getScript(this.distPlotType);
			for (String line : scriptDist) {
				w.writeln(line);
			}
			break;
		case cdfOnly:
			List<String> scriptCdf = this.getScript(this.distPlotType);
			for (String line : scriptCdf) {
				w.writeln(line);
			}
			break;
		}

		// write script data
		for (int i = 0; i < data.length; i++) {
			AggregatedValue[] tempValues = data[i].getValues();

			for (int j = 0; j < tempValues.length; j++) {
				double[] values = tempValues[j].getValues();
				String temp = "" + values[0];
				for (int k = 1; k < values.length; k++) {
					temp += Keywords.plotDataDelimiter + values[k];
				}
				w.writeln(temp);
			}
			// end-of-file indicates the end of this distribution data
			w.writeln("EOF");
		}
		w.close();
	}

	/**
	 * Writes the gnuplot script for an array of AggregatedValue objects to the
	 * destination directory
	 * 
	 * @param dir
	 *            destination directory
	 * @param filename
	 *            script filename
	 * @param data
	 *            input data gathered in Plotting.plotValue
	 * @throws IOException
	 *             thrown by the writer
	 */
	public void writeScript(String dir, String filename, AggregatedValue[] data)
			throws IOException {
		Writer w = new Writer(dir, filename);

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}

		// write script data
		for (int i = 0; i < data.length; i++) {
			String temp = "" + i;

			for (int j = 0; j < data[i].getValues().length; j++) {
				temp += Keywords.plotDataDelimiter + data[i].getValues()[j];
			}
			w.writeln(temp);
		}
		// end-of-file indicates the end of this value data
		w.writeln("EOF");
		w.close();
	}

	/**
	 * Writes the gnuplot script for runtime data of a whole series to the
	 * destination directory
	 * 
	 * @param dir
	 *            destination directory
	 * @param filename
	 *            script filename
	 * @param data
	 *            input data gathered in Plotting.plotRuntimes
	 * @throws IOException
	 *             thrown by the writer
	 */
	public void writeScript(String dir, String filename,
			AggregatedValue[][] data) throws IOException {
		Writer w = new Writer(dir, filename);

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}

		// write script data
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				String temp = "" + j;
				for (int k = 0; k < data[i][j].getValues().length; k++) {
					temp += Keywords.plotDataDelimiter
							+ data[i][j].getValues()[k];
				}
				w.writeln(temp);
			}
			// end-of-file indicates the end of this runtime data
			w.writeln("EOF");
		}
		w.close();
	}

	protected List<String> getScript() {
		return this.getScript(null);
	}

	/**
	 * Method used to generate the header of a gnuplot script
	 * 
	 * @return the header part of the gnuplot script
	 */
	protected List<String> getScript(DistributionPlotType distPlotType) {
		List<String> script = new LinkedList<String>();

		script.add("set terminal " + this.terminal);
		script.add("set output \"" + this.dir + this.filename + "."
				+ this.extension + "\"");
		if (this.key != null) {
			script.add("set key " + this.key);
		}
		if (this.grid) {
			script.add("set grid");
		}
		if (this.title != null) {
			script.add("set title \"" + this.title + "\"");
		}
		if (this.xLabel != null) {
			script.add("set xlabel \"" + this.xLabel + "\"");
		}
		if (this.xRange != null) {
			script.add("set xrange " + this.xRange);
		}
		if (this.yLabel != null) {
			script.add("set ylabel \"" + this.yLabel + "\"");
		}
		if (this.yRange != null) {
			script.add("set yrange " + this.yRange);
		}
		if (this.xLogscale && this.yLogscale) {
			script.add("set logscale xy");
		} else if (this.xLogscale) {
			script.add("set logscale x");
		} else if (this.yLogscale) {
			script.add("set logscale y");
		}
		if (this.plotDateTime) {
			script.add("set xdata time");
			script.add("set timeft " + this.dateTime);
		}

		script.add("set style fill empty");
		script.add("set boxwidth 0.2");

		// plot "data" u 1:(1./100.) smooth cumulative

		for (int i = 0; i < this.data.length; i++) {
			String line = "";
			if (distPlotType == null)
				line = this.data[i].getEntry(i + 1, this.lw, this.xOffset * i,
						this.yOffset * i);
			else
				line = this.data[i].getEntry(i + 1, this.lw, this.xOffset * i,
						this.yOffset * i, distPlotType);
			if (i == 0) {
				line = "plot " + line;
			}
			if (i < this.data.length - 1) {
				line = line + " , \\";
			}
			script.add(line);
		}
		return script;
	}

	// old
	public void generate() throws IOException, InterruptedException {
		Log.info("  => \"" + this.filename + "\" in " + this.dir);
		this.write(this.dir, this.scriptFilename);
		Execute.exec(Settings.gnuplotPath + " " + this.dir
				+ this.scriptFilename, true);
	}

	/**
	 * generates the gnuplot script depending on the type of the given inputData
	 * 
	 * @param inputData
	 *            data to be plotted
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public void generate(AggregatedData[] inputData) throws IOException,
			InterruptedException {
		Log.info("  => \"" + this.filename + "\" in " + this.dir);
		if (inputData[0] instanceof AggregatedValue)
			this.writeScript(this.dir, this.scriptFilename,
					(AggregatedValue[]) inputData);
		if (inputData[0] instanceof AggregatedDistribution)
			this.writeScript(this.dir, this.scriptFilename,
					(AggregatedDistribution[]) inputData);
		if (inputData[0] instanceof AggregatedNodeValueList)
			this.writeScript(this.dir, this.scriptFilename,
					(AggregatedNodeValueList[]) inputData);
		Execute.exec(Settings.gnuplotPath + " " + this.dir
				+ this.scriptFilename, true);
	}

	/**
	 * generates the gnuplot script for runtimes
	 * 
	 * @param inputData
	 *            2-dimensional array of AggregatedValue objects created in
	 *            Plotting.plotRuntimes
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public void generate(AggregatedValue[][] inputData) throws IOException,
			InterruptedException {
		Log.info("  => \"" + this.filename + "\" in " + this.dir);
		this.writeScript(this.dir, this.scriptFilename, inputData);
		Execute.exec(Settings.gnuplotPath + " " + this.dir
				+ this.scriptFilename, true);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setPlotDateTime(boolean plotDateTime) {
		this.plotDateTime = plotDateTime;
	}
}
