package dna.plot;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dna.io.Writer;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedValue;
import dna.util.Config;
import dna.util.Execute;
import dna.util.Log;

public class Plot {

	// default values
	private String dir = Config.get("GNUPLOT_DIR");

	private String filename = Config.get("GNUPLOT_FILENAME");

	private String scriptFilename = Config.get("GNUPLOT_SCRIPTFILENAME");

	// private variables
	private PlotData[] data;

	private DistributionPlotType distPlotType;

	private NodeValueListOrderBy orderBy;

	private NodeValueListOrder sortOrder;

	// constructors
	public Plot(PlotData[] data, String dir, String filename,
			String scriptFilename) {
		this(data, dir, filename, scriptFilename, Config
				.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"));
	}

	public Plot(PlotData[] data, String dir, String filename,
			String scriptFilename, DistributionPlotType distPlotType) {
		this(data, dir, filename, scriptFilename, distPlotType, Config
				.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"), Config
				.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"));
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
						temp += Config.get("PLOTDATA_DELIMITER") + values[k];
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
						temp += Config.get("PLOTDATA_DELIMITER") + values[k];
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
		default:
			Log.warn("distPlotType variable not set");
		}

		// write script data
		for (int i = 0; i < data.length; i++) {
			AggregatedValue[] tempValues = data[i].getValues();

			for (int j = 0; j < tempValues.length; j++) {
				double[] values = tempValues[j].getValues();
				String temp = "" + values[0];
				for (int k = 1; k < values.length; k++) {
					temp += Config.get("PLOTDATA_DELIMITER") + values[k];
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
				temp += Config.get("PLOTDATA_DELIMITER")
						+ data[i].getValues()[j];
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
					temp += Config.get("PLOTDATA_DELIMITER")
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

		script.add("set terminal " + Config.get("GNUPLOT_TERMINAL"));
		script.add("set output \"" + this.dir + this.filename + "."
				+ Config.get("GNUPLOT_EXTENSION") + "\"");
		if (!Config.get("GNUPLOT_KEY").equals("null")) {
			script.add("set key " + Config.get("GNUPOT_KEY"));
		}
		if (Config.getBoolean("GNUPLOT_GRID")) {
			script.add("set grid");
		}
		if (!Config.get("GNUPLOT_TITLE").equals("null")) {
			script.add("set title \"" + Config.get("GNUPLOT_TITLE") + "\"");
		}
		if (!Config.get("GNUPLOT_XLABEL").equals("null")) {
			script.add("set xlabel \"" + Config.get("GNUPLOT_XLABEL") + "\"");
		}
		if (!Config.get("GNUPLOT_XRANGE").equals("null")) {
			script.add("set xrange " + Config.get("GNUPLOT_XRANGE"));
		}
		if (!Config.get("GNUPLOT_YLABEL").equals("null")) {
			script.add("set ylabel \"" + Config.get("GNUPLOT_YLABEL") + "\"");
		}
		if (!Config.get("GNUPLOT_YRANGE").equals("null")) {
			script.add("set yrange " + Config.get("GNUPLOT_YRANGE"));
		}
		if (Config.getBoolean("GNUPLOT_XLOGSCALE")
				&& Config.getBoolean("GNUPLOT_YLOGSCALE")) {
			script.add("set logscale xy");
		} else if (Config.getBoolean("GNUPLOT_XLOGSCALE")) {
			script.add("set logscale x");
		} else if (Config.getBoolean("GNUPLOT_YLOGSCALE")) {
			script.add("set logscale y");
		}
		if (Config.getBoolean("GNUPLOT_PLOTDATETIME")) {
			script.add("set xdata time");
			script.add("set timeft " + Config.get("GNUPLOT_DATETIME"));
		}

		script.add("set style fill solid border -1");
		script.add("set boxwidth 0.2");

		for (int i = 0; i < this.data.length; i++) {
			String line = "";
			if (distPlotType == null)
				line = this.data[i].getEntry(i + 1,
						Config.getInt("GNUPLOT_LW"),
						Config.getDouble("GNUPLOT_XOFFSET") * i,
						Config.getDouble("GNUPLOT_YOFFSET") * i);
			else
				line = this.data[i].getEntry(i + 1,
						Config.getInt("GNUPLOT_LW"),
						Config.getDouble("GNUPLOT_XOFFSET") * i,
						Config.getDouble("GNUPLOT_YOFFSET") * i, distPlotType);
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
		Execute.exec(Config.get("GNUPLOT_PATH") + " " + this.dir
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
		Execute.exec(Config.get("GNUPLOT_PATH") + " " + this.dir
				+ this.scriptFilename, true);
	}

	public void setTitle(String title) {
		Config.overwrite("GNUPLOT_TITLE", title);
	}

	// datetime examples:
	// data . datetimeformat . comment
	// 2004/4/6 . %Y/%m/%d . 2004/04/06 works well
	// December/96 . %B/%y . warning if misspelled
	// 2004/Jan . %Y/%b . 3-letters abbreviation
	// 1970/240 . %Y/%j "%j" . is a day of the year (1-365)
	// 02:45:03 . %H:%M:%S "%H" . 24-hour
	// 1076909172 . %s . seconds since 1/1/1970 00:00
	public void setDateTime(String dateTime) {
		Config.overwrite("GNUPLOT_DATETIME", dateTime);
	}

	public void setPlotDateTime(boolean plotDateTime) {
		Config.overwrite("GNUPLOT_PLOTDATETIME", Boolean.toString(plotDateTime));
	}
}
