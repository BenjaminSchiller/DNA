package dna.plot;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dna.io.Writer;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedValue;
import dna.util.Config;
import dna.util.Execute;
import dna.util.Log;

/**
 * The plot class is used to create a gnuplot script file, add data to it and
 * execute it. Each object of the PlotData[] data field represents one line in
 * the plot.
 * 
 * @author RWilmes
 * @date 19.05.2014
 */
public class Plot {

	// plot data
	private PlotData[] data;

	// writer
	private String dir;
	private String plotFilename;
	private String scriptFilename;
	private Writer fileWriter;
	private int dataWriteCounter;
	private int dataQuantity;

	// plot styles
	private String title;
	private String dateTime;
	private boolean plotDateTime;
	private DistributionPlotType distPlotType;
	private NodeValueListOrderBy orderBy;
	private NodeValueListOrder sortOrder;

	/**
	 * Creates a plot object which will can written to a gnuplot script file.
	 * Data to be plotted can be added via appendData methods.
	 * 
	 * @param dir
	 *            Destination directory of the plot and script file.
	 * @param plotFilename
	 *            Filename of the plotted file.
	 * @param scriptFilename
	 *            Filename of the scriptfile.
	 * @param data
	 *            Array of PlotData objects, each representing a type of "data",
	 *            which will be plotted into the same plot.
	 * @throws IOException
	 *             Might be thrown by the writer.
	 */
	public Plot(String dir, String plotFilename, String scriptFilename,
			String title, PlotData[] data) throws IOException {
		this.dir = dir;
		this.plotFilename = plotFilename;
		this.scriptFilename = scriptFilename;
		this.title = title;
		this.data = data;

		// load default values
		this.sortOrder = Config
				.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER");
		this.orderBy = Config
				.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY");
		this.dateTime = Config.get("GNUPLOT_DATETIME");
		this.plotDateTime = Config.getBoolean("GNUPLOT_PLOTDATETIME");

		// init writer
		this.fileWriter = new Writer(dir, scriptFilename);
		this.dataWriteCounter = 0;
		this.dataQuantity = 1;
	}

	// new methods
	public void writeScriptHeaderNeu() throws IOException {
		Writer w = this.fileWriter;

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}
	}

	private void appendData(AggregatedValue[] values) throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "");
		this.appendEOF();
	}

	private void appendDataWithIndex(AggregatedValue[] values)
			throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "" + i);
		this.appendEOF();
	}

	private void appendData(AggregatedValue value, String timestamp)
			throws IOException {
		Writer w = this.fileWriter;
		String temp = "" + timestamp;

		double[] values;
		if (value == null)
			values = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		else
			values = value.getValues();
		for (int k = 0; k < values.length; k++) {
			if (temp.equals(""))
				temp += values[k];
			else
				temp += Config.get("PLOTDATA_DELIMITER") + values[k];
		}
		w.writeln(temp);
	}

	private void appendData(AggregatedValue value, double timestamp)
			throws IOException {
		this.appendData(value, "" + timestamp);
	}

	private void appendEOF() throws IOException {
		this.dataWriteCounter++;
		this.fileWriter.writeln("EOF");
	}

	/**
	 * Main method to add data to the plot. The data to be added will be
	 * identified by its name and domain.
	 * 
	 * Note: Convention of domains:
	 * 
	 * Statistical values : Config.get("PLOT_STATISTICS")
	 * 
	 * General Runtimes : Config.get("PLOT_GENERALRUNTIMES")
	 * 
	 * Metric Runtimes: Config.get("PLOT_METRICRUNTIMES")
	 * 
	 * If it can be either a general or metric runtime:
	 * Config.get("PLOT_CUSTOM_RUNTIME")
	 * 
	 * Metric Values, Distributions, NodeValueLists: name of the metric
	 * 
	 * @param name
	 *            Name of the value.
	 * @param domain
	 *            Domain of the value.
	 * @param batch
	 *            Batch the data will be taken from.
	 * @param addAsCDF
	 *            If its a distribution and shall be plotted as cdf
	 * @throws IOException
	 *             Thrown by the writer.
	 */
	private void addData(String name, String domain, AggregatedBatch batch,
			boolean addAsCDF) throws IOException {
		double timestamp = (double) batch.getTimestamp();
		// figure out where to get the data from
		if (domain.equals(Config.get("PLOT_STATISTICS"))) {
			this.appendData(batch.getValues().get(name), timestamp);
		} else if (domain.equals(Config.get("PLOT_METRICRUNTIMES"))) {
			this.appendData(batch.getMetricRuntimes().get(name), timestamp);
		} else if (domain.equals(Config.get("PLOT_GENERALRUNTIMES"))) {
			this.appendData(batch.getGeneralRuntimes().get(name), timestamp);
		} else if (domain.equals(Config.get("PLOT_CUSTOM_RUNTIME"))) {
			if (batch.getGeneralRuntimes().getNames().contains(name))
				this.appendData(batch.getGeneralRuntimes().get(name), timestamp);
			else if (batch.getMetricRuntimes().getNames().contains(name))
				this.appendData(batch.getMetricRuntimes().get(name), timestamp);
		} else {
			if (batch.getMetrics().getNames().contains(domain)) {
				AggregatedMetric m = batch.getMetrics().get(domain);
				if (m.getDistributions().getNames().contains(name)) {
					AggregatedValue[] values = m.getDistributions().get(name)
							.getValues();
					if (addAsCDF) {
						AggregatedValue[] tempValues = new AggregatedValue[values.length];
						for (int i = 0; i < tempValues.length; i++) {
							double[] tempV = new double[values[i].getValues().length];
							System.arraycopy(values[i].getValues(), 0, tempV,
									0, values[i].getValues().length);
							tempValues[i] = new AggregatedValue(
									values[i].getName(), tempV);
						}
						for (int j = 1; j < tempValues.length; j++) {
							for (int k = 1; k < tempValues[j].getValues().length; k++) {
								tempValues[j].getValues()[k] += tempValues[j - 1]
										.getValues()[k];
							}
						}
						this.appendData(tempValues);
					} else {
						this.appendData(values);
					}
				} else if (m.getNodeValues().getNames().contains(name)) {
					if (this.orderBy.equals(NodeValueListOrderBy.index)) {
						this.appendDataWithIndex(m.getNodeValues().get(name)
								.getValues());
					} else {
						AggregatedNodeValueList nvl = m.getNodeValues().get(
								name);
						nvl.setsortIndex(this.orderBy, this.sortOrder);
						AggregatedValue[] values = nvl.getValues();
						AggregatedValue[] tempValues = new AggregatedValue[values.length];
						int index = 0;
						for (int i : nvl.getSortIndex()) {
							double[] tempV = new double[values[i].getValues().length];
							System.arraycopy(values[i].getValues(), 0, tempV,
									0, values[i].getValues().length);
							tempValues[index] = new AggregatedValue(
									values[i].getName(), tempV);
							index++;
						}
						this.appendDataWithIndex(tempValues);
					}
				} else if (m.getValues().getNames().contains(name)) {
					this.appendData(m.getValues().get(name), timestamp);
				} else {
					Log.warn("problem when adding data to plot "
							+ this.scriptFilename + ". Value " + name
							+ " was not found in domain " + domain
							+ " of batch." + timestamp);
				}
			} else {
				Log.warn("problem when adding data to plot "
						+ this.scriptFilename + ". domain " + domain
						+ " not found in batch." + timestamp);
			}
		}
	}

	/**
	 * Adds data from one batch to the plot. Used for distribution and
	 * nodevaluelist plots, when batches will be read and handed over
	 * sequentially
	 **/
	public void addDataSequentially(AggregatedBatch batchData)
			throws IOException {
		String name = this.data[dataWriteCounter].getName();
		String domain = this.data[dataWriteCounter].getDomain();
		if (this.data[dataWriteCounter].isPlotAsCdf())
			this.addData(name, domain, batchData, true);
		else
			this.addData(name, domain, batchData, false);
	}

	/** Adds data to the plot **/
	public void addData(AggregatedBatch[] batchData) throws IOException {
		for (int i = 0; i < this.data.length; i++) {
			String name = this.data[i].getName();
			String domain = this.data[i].getDomain();
			for (int j = 0; j < batchData.length; j++) {
				this.addData(name, domain, batchData[j], false);
			}
			this.appendEOF();
		}
	}

	/** Adds data from runtimes as CDF's **/
	public void addDataFromRuntimesAsCDF(AggregatedBatch[] batchData)
			throws IOException {
		for (int i = 0; i < this.data.length; i++) {
			String name = this.data[i].getName();
			String domain = this.data[i].getDomain();
			AggregatedBatch prevBatch = batchData[0];
			AggregatedBatch tempBatch;
			for (int j = 0; j < batchData.length; j++) {
				if (j > 0) {
					tempBatch = Plot.sumRuntimes(batchData[j], prevBatch);
				} else {
					tempBatch = batchData[j];
				}
				this.addData(name, domain, tempBatch, false);
				prevBatch = tempBatch;
			}
			this.appendEOF();
		}
	}

	/**
	 * Returns a new AggregatedBatch, which equals b1, except that all runtime
	 * values equal the sum of the runtimes of b1 and b2.
	 * 
	 * @param b1
	 *            First runtime, will be cloned and returned with the sum of b2.
	 * @param b2
	 *            Will be added to b1.
	 * @return New AggregatedBatch, equalling b1, except for the runtime values.
	 */
	private static AggregatedBatch sumRuntimes(AggregatedBatch b1,
			AggregatedBatch b2) {
		AggregatedRunTimeList genRuntimes = new AggregatedRunTimeList(b1
				.getGeneralRuntimes().getName(), b1.getGeneralRuntimes().size());
		AggregatedRunTimeList metRuntimes = new AggregatedRunTimeList(b1
				.getMetricRuntimes().getName(), b1.getMetricRuntimes().size());
		for (String gen : b1.getGeneralRuntimes().getNames()) {
			AggregatedValue v1 = b1.getGeneralRuntimes().get(gen);
			AggregatedValue v2 = b2.getGeneralRuntimes().get(gen);

			double[] values3 = new double[v1.getValues().length];

			for (int i = 0; i < v1.getValues().length; i++) {
				double[] values2;
				if (v2 == null)
					values2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				else
					values2 = v2.getValues();
				values3[i] = 0;
				values3[i] += v1.getValues()[i] + values2[i];
			}

			genRuntimes.add(new AggregatedValue(v1.getName(), values3));
		}
		for (String met : b1.getMetricRuntimes().getNames()) {
			AggregatedValue v1 = b1.getMetricRuntimes().get(met);
			AggregatedValue v2 = b2.getMetricRuntimes().get(met);

			double[] values3 = new double[v1.getValues().length];

			for (int i = 0; i < v1.getValues().length; i++) {
				double[] values2;
				if (v2 == null)
					values2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				else
					values2 = v2.getValues();
				values3[i] = 0;
				values3[i] += v1.getValues()[i] + values2[i];
			}

			metRuntimes.add(new AggregatedValue(v1.getName(), values3));
		}

		// return new crafted batch
		return new AggregatedBatch(b1.getTimestamp(), b1.getValues(),
				genRuntimes, metRuntimes, b1.getMetrics());
	}

	/** Closes the fileWriter of the plot **/
	public void close() throws IOException {
		if (this.dataWriteCounter != this.data.length)
			Log.warn("Unexpected number of plotdata written to file "
					+ this.dir + this.scriptFilename + ". Expected: "
					+ this.data.length + "  Written: " + this.dataWriteCounter);
		this.fileWriter.close();
		this.fileWriter = null;
	}

	/**
	 * Method used to generate the header of a gnuplot script
	 * 
	 * @return the header part of the gnuplot script
	 */
	protected List<String> getScript() {
		List<String> script = new LinkedList<String>();

		script.add("set terminal " + Config.get("GNUPLOT_TERMINAL"));
		script.add("set output \"" + this.dir + this.plotFilename + "."
				+ Config.get("GNUPLOT_EXTENSION") + "\"");
		if (!Config.get("GNUPLOT_KEY").equals("null")) {
			script.add("set key " + Config.get("GNUPLOT_KEY"));
		}
		if (Config.getBoolean("GNUPLOT_GRID")) {
			script.add("set grid");
		}
		if (this.title != null) {
			script.add("set title \"" + this.title + "\"");
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
		if (this.plotDateTime) {
			script.add("set xdata time");
			script.add("set timefmt " + '"' + this.dateTime + '"');
		}

		script.add("set style " + Config.get("GNUPLOT_STYLE"));
		script.add("set boxwidth " + Config.get("GNUPLOT_BOXWIDTH"));

		for (int i = 0; i < this.data.length; i++) {
			String line = "";
			if (this.distPlotType == null)
				line = this.data[i].getEntry(i + 1,
						Config.getInt("GNUPLOT_LW"),
						Config.getDouble("GNUPLOT_XOFFSET") * i,
						Config.getDouble("GNUPLOT_YOFFSET") * i);
			else
				line = this.data[i].getEntry(i + 1,
						Config.getInt("GNUPLOT_LW"),
						Config.getDouble("GNUPLOT_XOFFSET") * i,
						Config.getDouble("GNUPLOT_YOFFSET") * i,
						this.distPlotType);
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

	/** Executes the gnuplot script **/
	public void execute() throws IOException, InterruptedException {
		Execute.exec(Config.get("GNUPLOT_PATH") + " " + this.dir
				+ this.scriptFilename, true);
	}

	// setters and getters
	public void setTitle(String title) {
		this.title = title;
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
		this.dateTime = dateTime;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setPlotDateTime(boolean plotDateTime) {
		this.plotDateTime = plotDateTime;
	}

	public boolean isPlotDateTime() {
		return plotDateTime;
	}

	public void setNodeValueListOrder(NodeValueListOrder order) {
		this.sortOrder = order;
	}

	public NodeValueListOrder getNodeValueListSortOrder() {
		return this.sortOrder;
	}

	public void setNodeValueListOrderBy(NodeValueListOrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public NodeValueListOrderBy getNodeValueListOrderBy() {
		return this.orderBy;
	}

	public int getDataQuantity() {
		return dataQuantity;
	}

	public void setDataQuantity(int dataQuantity) {
		this.dataQuantity = dataQuantity;
	}
}
