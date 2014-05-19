package dna.plot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dna.io.Writer;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedValue;
import dna.util.Config;
import dna.util.Execute;
import dna.util.Log;

public class Plot {

	// default values
	private String dir = Config.get("GNUPLOT_DIR");

	private String filename = Config.get("GNUPLOT_FILENAME");
	private String plotFilename;
	private String scriptFilename = Config.get("GNUPLOT_SCRIPTFILENAME");
	private String title;

	private Writer fileWriter;

	private int dataWriteCounter;
	private int dataQuantity;

	// private variables
	private PlotData[] data;

	private ArrayList<String> names;

	private DistributionPlotType distPlotType;

	private NodeValueListOrderBy orderBy;

	private NodeValueListOrder sortOrder;

	// new constructors

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
		this.filename = plotFilename;
		this.scriptFilename = scriptFilename;
		this.title = title;
		this.data = data;
		this.sortOrder = Config
				.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER");
		this.orderBy = Config
				.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY");

		// init writer
		this.fileWriter = new Writer(dir, scriptFilename);
		this.dataWriteCounter = 0;
		this.dataQuantity = 1;
	}

	public Plot(String dir, String plotFilename, String scriptFilename,
			String title, ArrayList<String> names, PlotData[] data)
			throws IOException {
		this(dir, plotFilename, scriptFilename, title, data);
		this.names = names;
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

	public void appendData(AggregatedValue[] values) throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "");
		this.appendEOF();
	}

	public void appendDataWithIndex(AggregatedValue[] values)
			throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "" + i);
		this.appendEOF();
	}

	public void appendData(AggregatedValue[] values, double[] timestamps)
			throws IOException {
		for (int i = 0; i < values.length; i++) {
			this.appendData(values[i], timestamps[i]);
		}
		this.appendEOF();
	}

	public void appendData(AggregatedValue value, String timestamp)
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

	public void appendData(AggregatedValue value, double timestamp)
			throws IOException {
		this.appendData(value, "" + timestamp);
	}

	public void appendEOF() throws IOException {
		this.dataWriteCounter++;
		this.fileWriter.writeln("EOF");
	}

	public ArrayList<String> getNames() {
		return this.names;
	}

	public void addData(String name, String domain, AggregatedBatch batch,
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

	public void addDataWithEOF(AggregatedBatch batchData) throws IOException {
		for (int i = 0; i < this.data.length; i++) {
			String name = this.data[i].getName();
			String domain = this.data[i].getDomain();
			this.addData(name, domain, batchData, false);
			this.appendEOF();
		}
	}

	public void addDataSequentially(AggregatedBatch batchData)
			throws IOException {
		// Log.info("add data sequentially debug: batchtimestamp: "
		// + batchData.getTimestamp() + "  datacounter: "
		// + this.dataWriteCounter);
		String name = this.data[dataWriteCounter].getName();
		String domain = this.data[dataWriteCounter].getDomain();
		if (this.data[dataWriteCounter].isPlotAsCdf())
			this.addData(name, domain, batchData, true);
		else
			this.addData(name, domain, batchData, false);
	}

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

	public void close() throws IOException {
		// for (int i = 0; i < this.data.length; i++) {
		// System.out.println(this.filename + "\t" + this.data[i].getDomain()
		// + "\t" + this.data[i].getName());
		// }

		if (this.dataWriteCounter != this.data.length)
			Log.warn("Unexpected number of plotdata written to file "
					+ this.dir + this.scriptFilename + ". Expected: "
					+ this.data.length + "  Written: " + this.dataWriteCounter);
		this.fileWriter.close();
		this.fileWriter = null;
	}

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
	 * Writes the gnuplot script header and returns the writer.
	 * 
	 * @param dir
	 *            destination directory
	 * @param filename
	 *            script filename
	 * @throws IOException
	 *             thrown by the writer
	 */
	public Writer writeScriptHeader(String dir, String filename)
			throws IOException {
		Writer w = new Writer(dir, filename);

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}
		return w;
	}

	public void appendData(Writer w, AggregatedValue[] values,
			double[] timestamps) throws IOException {
		for (int i = 0; i < values.length; i++) {
			this.appendData(w, values[i], timestamps[i]);
		}
	}

	public void appendData(Writer w, AggregatedValue value, double timestamp)
			throws IOException {
		String temp = "" + timestamp;
		for (int k = 0; k < value.getValues().length; k++) {
			temp += Config.get("PLOTDATA_DELIMITER") + value.getValues()[k];
		}
		w.writeln(temp);

		// end-of-file indicates the end of the data
		w.writeln("EOF");
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
			AggregatedValue[][] data, long[][] timestamps) throws IOException {
		Writer w = new Writer(dir, filename);

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}

		// write script data
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				String temp = "" + timestamps[i][j];
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
		if (Config.getBoolean("GNUPLOT_PLOTDATETIME")) {
			script.add("set xdata time");
			script.add("set timeft " + Config.get("GNUPLOT_DATETIME"));
		}

		script.add("set style " + Config.get("GNUPLOT_STYLE"));
		script.add("set boxwidth " + Config.get("GNUPLOT_BOXWIDTH"));

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
	public void generate(AggregatedValue[][] inputData, long[][] timestamps)
			throws IOException, InterruptedException {
		Log.info("  => \"" + this.filename + "\" in " + this.dir);
		this.writeScript(this.dir, this.scriptFilename, inputData, timestamps);
		Execute.exec(Config.get("GNUPLOT_PATH") + " " + this.dir
				+ this.scriptFilename, true);
	}

	public void execute() throws IOException, InterruptedException {
		Execute.exec(Config.get("GNUPLOT_PATH") + " " + this.dir
				+ this.scriptFilename, true);
	}

	public void setTitle(String title) {
		Config.overwrite("GNUPLOT_TITLE", title);
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
		Config.overwrite("GNUPLOT_DATETIME", dateTime);
	}

	public void setPlotDateTime(boolean plotDateTime) {
		Config.overwrite("GNUPLOT_PLOTDATETIME", Boolean.toString(plotDateTime));
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
