package dna.plot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dna.io.Writer;
import dna.plot.PlottingConfig.ValueSortMode;
import dna.plot.data.ExpressionData;
import dna.plot.data.FunctionData;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotDataLocation;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.BatchData;
import dna.series.data.IBatch;
import dna.series.data.MetricData;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDistr;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.BinnedLongDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.Distr.DistrType;
import dna.series.data.distr.QualityDistr;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.Config;
import dna.util.Execute;
import dna.util.Log;
import dna.util.expr.Expr;
import dna.util.expr.Parser;
import dna.util.expr.SyntaxException;
import dna.util.expr.Variable;

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
	private int[] dataIndizes;

	private boolean sorted;

	// writer
	private String dir;
	private String plotFilename;
	private String scriptFilename;
	private Writer fileWriter;
	private int dataWriteCounter;
	private int dataQuantity;
	private int[] seriesDataQuantities;
	private int functionQuantity;
	private int skippedFunction;
	private boolean errorPrinted;

	// plot config
	PlotConfig config;

	// plot styles
	private String title;
	private String datetime;
	private String timeDataFormat;
	private boolean plotDateTime;
	private DistributionPlotType distPlotType;
	private NodeValueListOrderBy orderBy;
	private NodeValueListOrder sortOrder;
	private boolean cdfPlot;
	private String key;
	private HashMap<Long, Long> timestampMap;
	private ValueSortMode sortMode;
	private String[] valueSortList;

	/**
	 * Creates a plot object which will be written to a gnuplot script file.
	 * Data to be plotted can be added via addData methods.
	 * 
	 * @param dir
	 *            Destination directory of the plot and script file.
	 * @param plotFilename
	 *            Filename of the plotted file.
	 * @param scriptFilename
	 *            Filename of the scriptfile.
	 * @param title
	 *            The plots title.
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
		this.dataIndizes = new int[data.length];
		for (int i = 0; i < dataIndizes.length; i++)
			dataIndizes[i] = i;
		this.sorted = false;

		// load default values
		this.sortOrder = Config
				.getNodeValueListOrder(PlotConfig.gnuplotDefaultKeyNodeValueListOrder);
		this.orderBy = Config
				.getNodeValueListOrderBy(PlotConfig.gnuplotDefaultKeyNodeValueListOrderBy);
		this.datetime = Config.get(PlotConfig.gnuplotDefaultKeyDateTime);
		this.plotDateTime = Config
				.getBoolean(PlotConfig.gnuplotDefaultKeyPlotDateTime);
		this.timeDataFormat = Config
				.get(PlotConfig.gnuplotDefaultKeyTimeDataFormat);
		this.sortMode = Config
				.getValueSortMode(PlotConfig.gnuplotDefaultKeyValueSortMode);
		this.valueSortList = Config
				.keys(PlotConfig.gnuplotDefaultKeyValueSortList);
		this.cdfPlot = false;

		// init writer
		this.fileWriter = new Writer(dir, scriptFilename);
		this.dataWriteCounter = 0;
		this.functionQuantity = 0;
		this.skippedFunction = 0;
		this.dataQuantity = 1;
		this.errorPrinted = false;

		for (PlotData pd : this.data) {
			if (pd instanceof FunctionData) {
				this.functionQuantity++;
			} else {
				if (pd.getDataLocation().equals(PlotDataLocation.dataFile))
					this.functionQuantity++;
			}
		}
	}

	/**
	 * Creates a plot object which will be written to a gnuplot script file.
	 * Data to be plotted can be added via addData methods.
	 * 
	 * @param dir
	 *            Destination directory of the plot and script file.
	 * @param plotFilename
	 *            Filename of the plotted file.
	 * @param scriptFilename
	 *            Filename of the scriptfile.
	 * @param title
	 *            The plots title.
	 * @param config
	 *            PlotConfig that allows for detailed configuration of the plot.
	 * @param data
	 *            Array of PlotData objects, each representing a type of "data",
	 *            which will be plotted into the same plot.
	 * @throws IOException
	 *             Might be thrown by the writer.
	 */
	public Plot(String dir, String plotFilename, String scriptFilename,
			String title, PlotConfig config, PlotData[] data)
			throws IOException {
		this(dir, plotFilename, scriptFilename, title, data);
		this.config = config;

		// title
		if (config.getTitle() != null) {
			this.title = config.getTitle();
		}

		// datetime
		if (config.getDatetime() != null) {
			this.datetime = config.getDatetime();
			this.plotDateTime = true;
		}
		this.timeDataFormat = config.getTimeDataFormat();

		// sort settings
		if (config.getDistPlotType() != null) {
			this.distPlotType = config.getDistPlotType();
		}

		if (config.getOrder() != null) {
			this.sortOrder = config.getOrder();
		}

		if (config.getOrderBy() != null) {
			this.orderBy = config.getOrderBy();
		}
	}

	// new methods
	public void writeScriptHeader() throws IOException {
		Writer w = this.fileWriter;

		// write script header
		List<String> script = this.getScript();
		for (String line : script) {
			w.writeln(line);
		}
	}

	// append data methods
	public void appendData(AggregatedValue[] values) throws IOException {
		// if no values, add one line with zero's
		if (values.length == 0)
			this.appendData(new AggregatedValue("",
					PlotConfig.gnuplotZeroLineNoIndex), "0.0");
		// else append values
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "");
		this.appendEOF();
	}

	public void appendData(double[] values) throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "");
		this.appendEOF();
	}

	public void appendData(double[] values, double[] indizes)
			throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], indizes[i]);
		this.appendEOF();
	}

	public void appendDataWithIndex(AggregatedValue[] values)
			throws IOException {
		for (int i = 0; i < values.length; i++)
			this.appendData(values[i], "" + i);
		this.appendEOF();
	}

	public void appendDataWithIndex(double[] values) throws IOException {
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
			values = PlotConfig.gnuplotZeroLineWithIndex;
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

	private void appendData(double value, double timestamp) throws IOException {
		this.appendData(value, "" + timestamp);
	}

	private void appendData(double value, String timestamp) throws IOException {
		Writer w = this.fileWriter;
		String temp = "" + timestamp + Config.get("PLOTDATA_DELIMITER") + value;
		w.writeln(temp);
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

		// map timestamps
		if (this.getTimestampMap() != null) {
			if (this.getTimestampMap().containsKey(batch.getTimestamp())) {
				timestamp = (double) this.getTimestampMap().get(
						batch.getTimestamp());
			}
		}

		// figure out where to get the data from
		if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
			this.appendData(batch.getValues().get(name), timestamp);
		} else if (domain.equals(PlotConfig.customPlotDomainRuntimes)) {
			if (batch.getGeneralRuntimes().getNames().contains(name))
				this.appendData(batch.getGeneralRuntimes().get(name), timestamp);
			else if (batch.getMetricRuntimes().getNames().contains(name))
				this.appendData(batch.getMetricRuntimes().get(name), timestamp);
		} else if (domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
			this.appendData(batch.getMetricRuntimes().get(name), timestamp);
		} else if (domain.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
			this.appendData(batch.getGeneralRuntimes().get(name), timestamp);
		} else {
			if (batch.getMetrics().getNames().contains(domain)) {
				AggregatedMetric m = batch.getMetrics().get(domain);
				if (m.getDistributions().getNames().contains(name)) {
					AggregatedValue[] values = m.getDistributions().get(name)
							.getValues();
					if (values == null) {
						Log.warn("no values found in plot '"
								+ this.plotFilename + "' for '" + domain + "."
								+ name + "'");
					} else {
						if (addAsCDF) {
							AggregatedValue[] tempValues = new AggregatedValue[values.length];
							for (int i = 0; i < tempValues.length; i++) {
								double[] tempV = new double[values[i]
										.getValues().length];
								System.arraycopy(values[i].getValues(), 0,
										tempV, 0, values[i].getValues().length);
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
					}
				} else if (m.getNodeValues().getNames().contains(name)) {
					AggregatedNodeValueList nvl = m.getNodeValues().get(name);
					if (nvl.getValues() == null) {
						Log.warn("no values found in plot '"
								+ this.plotFilename + "' for '" + domain + "."
								+ name + "'");
					} else {
						if (this.orderBy.equals(NodeValueListOrderBy.index)) {
							this.appendDataWithIndex(nvl.getValues());
						} else {
							nvl.setsortIndex(this.orderBy, this.sortOrder);
							AggregatedValue[] values = nvl.getValues();
							AggregatedValue[] tempValues = new AggregatedValue[values.length];
							int index = 0;
							for (int i : nvl.getSortIndex()) {
								double[] tempV = new double[values[i]
										.getValues().length];
								System.arraycopy(values[i].getValues(), 0,
										tempV, 0, values[i].getValues().length);
								tempValues[index] = new AggregatedValue(
										values[i].getName(), tempV);
								index++;
							}
							this.appendDataWithIndex(tempValues);
						}
					}
				} else if (m.getValues().getNames().contains(name)) {
					this.appendData(m.getValues().get(name), timestamp);
				} else {
					if (!this.errorPrinted) {
						Log.warn("problem when adding data to plot '"
								+ this.scriptFilename + "'. Value '" + name
								+ "' was not found in domain '" + domain + "'!");
						this.errorPrinted = true;
					}
				}
			} else {
				if (!this.errorPrinted) {
					Log.warn("problem when adding data to plot '"
							+ this.scriptFilename + "', domain '" + domain
							+ "' not found!");
					this.errorPrinted = true;
				}
			}
		}
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
	private void addData(String name, String domain, BatchData batch,
			boolean addAsCDF) throws IOException {
		double timestamp = (double) batch.getTimestamp();

		// map timestamps
		if (this.getTimestampMap() != null) {
			if (this.getTimestampMap().containsKey(batch.getTimestamp())) {
				timestamp = (double) this.getTimestampMap().get(
						batch.getTimestamp());
			}
		}

		// figure out where to get the data from
		if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
			this.appendData(batch.getValues().get(name).getValue(), timestamp);
		} else if (domain.equals(PlotConfig.customPlotDomainRuntimes)) {
			if (batch.getGeneralRuntimes().getNames().contains(name))
				this.appendData(batch.getGeneralRuntimes().get(name)
						.getRuntime(), timestamp);
			else if (batch.getMetricRuntimes().getNames().contains(name))
				this.appendData(batch.getMetricRuntimes().get(name)
						.getRuntime(), timestamp);
		} else if (domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
			this.appendData(batch.getMetricRuntimes().get(name).getRuntime(),
					timestamp);
		} else if (domain.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
			this.appendData(batch.getGeneralRuntimes().get(name).getRuntime(),
					timestamp);
		} else {
			if (batch.getMetrics().getNames().contains(domain)) {
				MetricData m = batch.getMetrics().get(domain);
				if (m.getDistributions().getNames().contains(name)) {
					Distr<?, ?> d = m.getDistributions().get(name);
					DistrType type = d.getDistrType();
					double[] values = null;
					double[] indizes = null;

					if (type.equals(DistrType.BINNED_DOUBLE)
							|| type.equals(DistrType.BINNED_INT)
							|| type.equals(DistrType.BINNED_LONG)) {
						long[] tempValues = ((BinnedDistr<?>) d).getValues();
						long denominator = ((BinnedDistr<?>) d)
								.getDenominator();
						double binSize;

						switch (type) {
						case BINNED_DOUBLE:
							binSize = ((BinnedDoubleDistr) d).getBinSize();
							break;
						case BINNED_INT:
							binSize = ((BinnedIntDistr) d).getBinSize();
							break;
						case BINNED_LONG:
							binSize = ((BinnedLongDistr) d).getBinSize();
							break;
						default:
							binSize = 0;
							break;
						}
						values = new double[tempValues.length];
						indizes = new double[tempValues.length];

						for (int i = 0; i < tempValues.length; i++) {
							values[i] = tempValues[i] * 1.0 / denominator;
							indizes[i] = i * binSize;
						}
					} else if (type.equals(DistrType.QUALITY_DOUBLE)
							|| type.equals(DistrType.QUALITY_INT)
							|| type.equals(DistrType.QUALITY_LONG)) {
						double[] tempValues = ((QualityDistr<?>) d).getValues();
						values = new double[tempValues.length];
						indizes = new double[tempValues.length];
						for (int i = 0; i < tempValues.length; i++) {
							values[i] = tempValues[i];
							indizes[i] = i;
						}
					}

					if (values == null) {
						Log.warn("no values found in plot '"
								+ this.plotFilename + "' for '" + domain + "."
								+ name + "'");
					} else {
						if (addAsCDF) {
							double[] tempValues = new double[values.length];
							for (int i = 0; i < tempValues.length; i++) {
								if (i == 0)
									tempValues[i] = values[i];
								else
									tempValues[i] = values[i]
											+ tempValues[i - 1];
							}
							this.appendData(tempValues, indizes);
						} else {
							this.appendData(values, indizes);
						}
					}
				} else if (m.getNodeValues().getNames().contains(name)) {
					NodeValueList nvl = m.getNodeValues().get(name);
					if (nvl.getValues() == null) {
						Log.warn("no values found in plot '"
								+ this.plotFilename + "' for '" + domain + "."
								+ name + "'");
					} else {
						if (this.orderBy.equals(NodeValueListOrderBy.index)) {
							this.appendDataWithIndex(nvl.getValues());
						} else {
							// TODO: sorting by nvl sort_order
							Log.debug("adding nvl with sort order "
									+ this.sortOrder.toString()
									+ " but this is not implemented yet in run plotting");
							this.appendDataWithIndex(nvl.getValues());
						}
					}
				} else if (m.getValues().getNames().contains(name)) {
					this.appendData(m.getValues().get(name).getValue(),
							timestamp);
				} else {
					if (!this.errorPrinted) {
						Log.warn("problem when adding data to plot '"
								+ this.scriptFilename + "'. Value '" + name
								+ "' was not found in domain '" + domain + "'!");
						this.errorPrinted = true;
					}
				}
			} else {
				if (!this.errorPrinted) {
					Log.warn("problem when adding data to plot '"
							+ this.scriptFilename + "', domain '" + domain
							+ "' not found!");
					this.errorPrinted = true;
				}
			}
		}
	}

	/**
	 * Adds data from one batch to the plot. Used for distribution and
	 * nodevaluelist plots, when batches will be read and handed over
	 * sequentially
	 **/
	public void addDataSequentially(IBatch batchData) throws IOException {
		if (batchData instanceof AggregatedBatch)
			this.addDataSequentially((AggregatedBatch) batchData);
		if (batchData instanceof BatchData)
			this.addDataSequentially((BatchData) batchData);
	}

	/**
	 * Adds data from one batch to the plot. Used for distribution and
	 * nodevaluelist plots, when batches will be read and handed over
	 * sequentially
	 **/
	public void addDataSequentially(AggregatedBatch batchData)
			throws IOException {
		if (!(this.data[this.dataWriteCounter] instanceof FunctionData)) {
			// if not function, add data

			// if data location is in data file, dont add data to script file
			if (this.data[this.dataWriteCounter].getDataLocation().equals(
					PlotDataLocation.dataFile)) {
				this.dataWriteCounter++;
				this.skippedFunction++;
			} else {
				String name = this.data[this.dataWriteCounter].getName();
				String domain = this.data[this.dataWriteCounter].getDomain();

				// add data
				this.addData(name, domain, batchData, false);
			}
		}
	}

	/**
	 * Adds data from one batch to the plot. Used for distribution and
	 * nodevaluelist plots, when batches will be read and handed over
	 * sequentially
	 **/
	public void addDataSequentially(BatchData batchData) throws IOException {
		if (!(this.data[this.dataWriteCounter] instanceof FunctionData)) {
			// if not function, add data

			// if data location is in data file, dont add data to script file
			if (this.data[this.dataWriteCounter].getDataLocation().equals(
					PlotDataLocation.dataFile)) {
				this.dataWriteCounter++;
				this.skippedFunction++;
			} else {
				String name = this.data[this.dataWriteCounter].getName();
				String domain = this.data[this.dataWriteCounter].getDomain();

				// add data
				this.addData(name, domain, batchData, false);
			}
		}
	}

	/**
	 * Adds data from batches of a whole series to the plot. Used for plotting
	 * values of multiple series. Data can be read and added sequentially for
	 * each series.
	 */
	public void addDataSequentially(IBatch[] batchData) throws IOException {
		if (batchData instanceof AggregatedBatch[])
			this.addDataSequentially((AggregatedBatch[]) batchData);
		if (batchData instanceof BatchData[])
			this.addDataSequentially((BatchData[]) batchData);
	}

	/**
	 * Adds data from batches of a whole series to the plot. Used for plotting
	 * values of multiple series. Data can be read and added sequentially for
	 * each series.
	 */
	public void addDataSequentially(AggregatedBatch[] batchData)
			throws IOException {
		if (this.dataWriteCounter >= this.data.length) {
			// counter out of bounds, dont add more data
			Log.warn("attempt to write to much data to plot '"
					+ this.plotFilename + "'");
		} else {
			if (this.data[this.dataWriteCounter] == null) {
				Log.error("PlotData " + this.dataWriteCounter + " of plot '"
						+ this.plotFilename + "' is null!");
			}
			if (!(this.data[this.dataWriteCounter] instanceof FunctionData)) {
				// if not function, add data
				String name = this.data[this.dataWriteCounter].getName();
				String domain = this.data[this.dataWriteCounter].getDomain();

				for (int j = 0; j < batchData.length; j++) {
					// if batch is null, no data -> just add EOF
					if (batchData[j] != null) {
						// check if expression
						if (this.data[this.dataWriteCounter] instanceof ExpressionData)
							this.addDataFromExpression(
									batchData[j],
									(ExpressionData) this.data[this.dataWriteCounter]);
						else
							this.addData(name, domain, batchData[j], false);
					}
				}
				this.appendEOF();

			} else {
				// if function, increment write counter and call method again
				this.dataWriteCounter++;
				this.skippedFunction++;
				this.addDataSequentially(batchData);
			}
		}
	}

	/**
	 * Adds data from batches of a whole series to the plot. Used for plotting
	 * values of multiple series. Data can be read and added sequentially for
	 * each series.
	 */
	public void addDataSequentially(BatchData[] batchData) throws IOException {
		if (this.dataWriteCounter >= this.data.length) {
			// counter out of bounds, dont add more data
			Log.warn("attempt to write to much data to plot '"
					+ this.plotFilename + "'");
		} else {
			if (this.data[this.dataWriteCounter] == null) {
				Log.error("PlotData " + this.dataWriteCounter + " of plot '"
						+ this.plotFilename + "' is null!");
			}
			if (!(this.data[this.dataWriteCounter] instanceof FunctionData)) {
				// if not function, add data
				String name = this.data[this.dataWriteCounter].getName();
				String domain = this.data[this.dataWriteCounter].getDomain();

				for (int j = 0; j < batchData.length; j++) {
					// if batch is null, no data -> just add EOF
					if (batchData[j] != null) {
						// check if expression
						if (this.data[this.dataWriteCounter] instanceof ExpressionData)
							this.addDataFromExpression(
									batchData[j],
									(ExpressionData) this.data[this.dataWriteCounter]);
						else
							this.addData(name, domain, batchData[j], false);
					}
				}
				this.appendEOF();

			} else {
				// if function, increment write counter and call method again
				this.dataWriteCounter++;
				this.skippedFunction++;
				this.addDataSequentially(batchData);
			}
		}
	}

	/** Adds data to the plot **/
	public void addData(IBatch[] batchData) throws IOException {
		if (batchData[0] instanceof BatchData)
			this.addData((BatchData[]) batchData);
		else
			this.addData((AggregatedBatch[]) batchData);
	}

	/** Adds data to the plot **/
	public void addData(AggregatedBatch[] batchData) throws IOException {
		// iterate over plotdata
		for (int i = 0; i < this.data.length; i++) {
			// check what type of data
			if (this.data[i] instanceof FunctionData) {
				// if function, skip
				continue;
			}

			// default case
			for (int j = 0; j < batchData.length; j++) {
				// check if expression
				if (this.data[i] instanceof ExpressionData)
					this.addDataFromExpression(batchData[j],
							(ExpressionData) this.data[i]);
				else
					this.addData(this.data[i].getName(),
							this.data[i].getDomain(), batchData[j], false);
			}
			this.appendEOF();
		}
	}

	/** Adds data to the plot **/
	public void addData(BatchData[] batchData) throws IOException {
		// iterate over plotdata
		for (int i = 0; i < this.data.length; i++) {
			// check what type of data
			if (this.data[i] instanceof FunctionData) {
				// if function, skip
				continue;
			}

			// default case
			for (int j = 0; j < batchData.length; j++) {
				// check if expression
				if (this.data[i] instanceof ExpressionData)
					this.addDataFromExpression(batchData[j],
							(ExpressionData) this.data[i]);
				else
					this.addData(this.data[i].getName(),
							this.data[i].getDomain(), batchData[j], false);
			}
			this.appendEOF();
		}
	}

	/** Adds data according to the expression **/
	public void addDataFromExpression(AggregatedBatch b, ExpressionData d)
			throws IOException {
		long timestamp = b.getTimestamp();

		// map timestamps
		if (this.getTimestampMap() != null) {
			if (this.getTimestampMap().containsKey(b.getTimestamp())) {
				timestamp = this.getTimestampMap().get(b.getTimestamp());
			}
		}

		String expression = d.getExpressionWithoutMarks();

		String[] vars = d.getVariables();
		String[] domains = d.getDomains();
		AggregatedValue[] values = new AggregatedValue[vars.length];

		for (int i = 0; i < vars.length; i++) {
			String value = vars[i];
			String domain = domains[i];
			if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
				values[i] = b.getValues().get(value);
			} else if (domain.equals(PlotConfig.customPlotDomainRuntimes)) {
				if (b.getGeneralRuntimes().getNames().contains(value))
					values[i] = b.getGeneralRuntimes().get(value);
				else if (b.getMetricRuntimes().getNames().contains(value))
					values[i] = b.getMetricRuntimes().get(value);
			} else if (domain
					.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
				values[i] = b.getGeneralRuntimes().get(value);
			} else if (domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
				values[i] = b.getMetricRuntimes().get(value);
			} else if (b.getMetrics().getNames().contains(domain)) {
				AggregatedMetric m = b.getMetrics().get(domain);
				if (m.getValues().getNames().contains(value)) {
					values[i] = m.getValues().get(value);
				} else {
					if (!this.errorPrinted) {
						Log.warn("problem when adding data to plot '"
								+ this.scriptFilename + "'. Value '" + value
								+ "' was not found in domain '" + domain + "'!");
						this.errorPrinted = true;
					}
				}
			} else {
				if (!this.errorPrinted) {
					Log.warn("problem when adding expression data to plot '"
							+ this.scriptFilename + "', domain '" + domain
							+ "' not found!");
					this.errorPrinted = true;
				}
			}
		}

		// replace normal variables with unique dummy variables, in case normal
		// variables contain mathematical symbols like '-'
		String[] varsTemp = new String[vars.length];
		System.arraycopy(vars, 0, varsTemp, 0, vars.length);
		for (int i = 0; i < vars.length; i++) {
			varsTemp[i] = PlotConfig.dummyVariable + i;
			expression = expression.replace(vars[i], varsTemp[i]);
		}

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

		// define variables
		Variable[] variables = new Variable[vars.length];
		for (int i = 0; i < variables.length; i++) {
			variables[i] = Variable.make(varsTemp[i]);
		}

		// only print warning message once
		boolean[] warningsPrinted = new boolean[vars.length];

		// calculate values
		int valuesCount = 9;
		double[] valuesNew = new double[valuesCount];
		for (int i = 0; i < valuesCount; i++) {
			// set variables
			for (int j = 0; j < variables.length; j++) {
				if (values[j] == null) {
					// if null, print warning and set 0 as value
					if (!warningsPrinted[j]) {
						// Log.warn("no values found for '" + domains[j] + "."
						// + vars[j] + "'. Values assumed to be zero.");
						warningsPrinted[j] = true;
					}
					variables[j].setValue(0.0);
				} else {
					// set variable
					variables[j].setValue(values[j].getValues()[i]);
				}
			}
			valuesNew[i] = expr.value();
		}

		// append data
		this.appendData(new AggregatedValue(null, valuesNew), "" + timestamp);
	}

	/** Adds data according to the expression **/
	public void addDataFromExpression(BatchData b, ExpressionData d)
			throws IOException {
		long timestamp = b.getTimestamp();
		// map timestamps
		if (this.getTimestampMap() != null) {
			if (this.getTimestampMap().containsKey(b.getTimestamp())) {
				timestamp = this.getTimestampMap().get(b.getTimestamp());
			}
		}

		String expression = d.getExpressionWithoutMarks();

		String[] vars = d.getVariables();
		String[] domains = d.getDomains();
		Value[] values = new Value[vars.length];

		for (int i = 0; i < vars.length; i++) {
			String value = vars[i];
			String domain = domains[i];
			if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
				values[i] = b.getValues().get(value);
			} else if (domain.equals(PlotConfig.customPlotDomainRuntimes)) {
				if (b.getGeneralRuntimes().getNames().contains(value))
					values[i] = new Value(value, b.getGeneralRuntimes()
							.get(value).getRuntime());
				else if (b.getMetricRuntimes().getNames().contains(value))
					values[i] = new Value(value, b.getMetricRuntimes()
							.get(value).getRuntime());
			} else if (domain
					.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
				values[i] = new Value(value, b.getGeneralRuntimes().get(value)
						.getRuntime());
			} else if (domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
				values[i] = new Value(value, b.getMetricRuntimes().get(value)
						.getRuntime());
			} else if (b.getMetrics().getNames().contains(domain)) {
				MetricData m = b.getMetrics().get(domain);
				if (m.getValues().getNames().contains(value)) {
					values[i] = m.getValues().get(value);
				} else {
					if (!this.errorPrinted) {
						Log.warn("problem when adding data to plot '"
								+ this.scriptFilename + "'. Value '" + value
								+ "' was not found in domain '" + domain + "'!");
						this.errorPrinted = true;
					}
				}
			} else {
				if (!this.errorPrinted) {
					Log.warn("problem when adding expression data to plot '"
							+ this.scriptFilename + "', domain '" + domain
							+ "' not found!");
					this.errorPrinted = true;
				}
			}
		}

		// replace normal variables with unique dummy variables, in case normal
		// variables contain mathematical symbols like '-'
		String[] varsTemp = new String[vars.length];
		System.arraycopy(vars, 0, varsTemp, 0, vars.length);
		for (int i = 0; i < vars.length; i++) {
			varsTemp[i] = PlotConfig.dummyVariable + i;
			expression = expression.replace(vars[i], varsTemp[i]);
		}

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

		// define variables
		Variable[] variables = new Variable[vars.length];
		for (int i = 0; i < variables.length; i++) {
			variables[i] = Variable.make(varsTemp[i]);
		}

		// only print warning message once
		boolean[] warningsPrinted = new boolean[vars.length];

		// calculate values
		int valuesCount = 1;
		double[] valuesNew = new double[valuesCount];
		for (int i = 0; i < valuesCount; i++) {
			// set variables
			for (int j = 0; j < variables.length; j++) {
				if (values[j] == null) {
					// if null, print warning and set 0 as value
					if (!warningsPrinted[j]) {
						// Log.warn("no values found for '" + domains[j] + "."
						// + vars[j] + "'. Values assumed to be zero.");
						warningsPrinted[j] = true;
					}
					variables[j].setValue(0.0);
				} else {
					// set variable
					variables[j].setValue(values[j].getValue());
				}
			}
			valuesNew[i] = expr.value();
		}

		// append data
		this.appendData(new AggregatedValue(null, valuesNew), "" + timestamp);
	}

	/** Adds data from runtimes as CDF's **/
	@Deprecated
	public void addDataFromRuntimesAsCDF(AggregatedBatch[] batchData)
			throws IOException {
		// iterate over plotdata
		for (int i = 0; i < this.data.length; i++) {
			// check if function
			if (this.data[i] instanceof FunctionData) {
				// if function, only increment data write counter
				this.dataWriteCounter++;
			} else if (this.data[i] instanceof ExpressionData) {
				// if expression
				AggregatedBatch prevBatch = batchData[0];
				AggregatedBatch tempBatch;
				for (int j = 0; j < batchData.length; j++) {
					if (j > 0) {
						tempBatch = AggregatedBatch.sumRuntimes(batchData[j],
								prevBatch);
					} else {
						tempBatch = batchData[j];
					}
					this.addDataFromExpression(tempBatch,
							(ExpressionData) this.data[i]);
					prevBatch = tempBatch;
				}
				this.appendEOF();
			} else {
				// if not a function or expression, add data
				String name = this.data[i].getName();
				String domain = this.data[i].getDomain();
				AggregatedBatch prevBatch = batchData[0];
				AggregatedBatch tempBatch;
				for (int j = 0; j < batchData.length; j++) {
					if (j > 0) {
						tempBatch = AggregatedBatch.sumRuntimes(batchData[j],
								prevBatch);
					} else {
						tempBatch = batchData[j];
					}
					this.addData(name, domain, tempBatch, false);
					prevBatch = tempBatch;
				}
				this.appendEOF();
			}
		}
	}

	/** Adds data from values as CDF's **/
	@Deprecated
	public void addDataFromValuesAsCDF(AggregatedBatch[] batchData)
			throws IOException {
		// iterate over plotdata
		for (int i = 0; i < this.data.length; i++) {
			// check if function
			if (!(this.data[i] instanceof FunctionData)) {
				// if not a function, add data
				String name = this.data[i].getName();
				String domain = this.data[i].getDomain();
				AggregatedBatch prevBatch = batchData[0];
				AggregatedBatch tempBatch;
				for (int j = 0; j < batchData.length; j++) {
					if (j > 0) {
						tempBatch = AggregatedBatch.sumValues(batchData[j],
								prevBatch);
					} else {
						tempBatch = batchData[j];
					}
					this.addData(name, domain, tempBatch, false);
					prevBatch = tempBatch;
				}
				this.appendEOF();
			} else {
				// if function, only increment data write counter
				this.dataWriteCounter++;
			}
		}
	}

	/** Closes the fileWriter of the plot **/
	public void close() throws IOException {
		if (this.dataWriteCounter + this.functionQuantity
				- this.skippedFunction != this.data.length)
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
		// init script list
		List<String> script = new LinkedList<String>();

		// add script lines
		script.add("set terminal " + Config.get("GNUPLOT_TERMINAL"));
		script.add("set output \"" + this.dir + this.plotFilename + "."
				+ Config.get("GNUPLOT_EXTENSION") + "\"");

		if (Config.getBoolean("GNUPLOT_GRID")) {
			script.add("set grid");
		}
		if (this.title != null) {
			script.add("set title \"" + this.title + "\"");
		}

		if (this.plotDateTime) {
			script.add("set xdata time");
			script.add("set timefmt " + '"' + this.timeDataFormat + '"');
			script.add("set format x " + '"' + this.datetime + '"');
		}

		for (int i = 0; i < this.data.length; i++) {
			if (data[i] instanceof FunctionData) {
				script.add(((FunctionData) data[i]).getLine());
			}
		}
		script.add("set style " + Config.get("GNUPLOT_STYLE"));
		script.add("set boxwidth " + Config.get("GNUPLOT_BOXWIDTH"));

		// if no config is present
		if (this.config == null) {
			if (!Config.get("GNUPLOT_XLABEL").equals("null")) {
				script.add("set xlabel \"" + Config.get("GNUPLOT_XLABEL")
						+ "\"");
			}
			if (!Config.get("GNUPLOT_YLABEL").equals("null")) {
				script.add("set ylabel \"" + Config.get("GNUPLOT_YLABEL")
						+ "\"");
			}
			if (this.cdfPlot)
				script.add("set key "
						+ Config.get(PlotConfig.gnuplotDefaultKeyCdfKey));
			else
				script.add("set key "
						+ Config.get(PlotConfig.gnuplotDefaultKeyKey));
			if (Config.getBoolean("GNUPLOT_XLOGSCALE")
					&& Config.getBoolean("GNUPLOT_YLOGSCALE")) {
				script.add("set logscale xy");
			} else if (Config.getBoolean("GNUPLOT_XLOGSCALE")) {
				script.add("set logscale x");
			} else if (Config.getBoolean("GNUPLOT_YLOGSCALE")) {
				script.add("set logscale y");
			}
			if (!Config.get("GNUPLOT_XRANGE").equals("null")) {
				script.add("set xrange " + Config.get("GNUPLOT_XRANGE"));
			}
			if (!Config.get("GNUPLOT_YRANGE").equals("null")) {
				script.add("set yrange " + Config.get("GNUPLOT_YRANGE"));
			}
			for (int i = 0; i < this.data.length; i++) {
				String line = "";

				// determine plot type
				DistributionPlotType type = this.distPlotType;
				if (type != null) {
					if (type.equals(DistributionPlotType.distANDcdf)) {
						if (this.cdfPlot)
							type = DistributionPlotType.cdfOnly;
						else
							type = DistributionPlotType.distOnly;
					}
				}

				// get line
				line = this.data[i].getEntry(i + 1,
						Config.getInt("GNUPLOT_LW"),
						Config.getDouble("GNUPLOT_XOFFSET") * i,
						Config.getDouble("GNUPLOT_YOFFSET") * i,
						Config.get("GNUPLOT_XSCALING"),
						Config.get("GNUPLOT_YSCALING"), this.distPlotType,
						false);
				line = line.replace("filledcurves", "filledcurves y1=0");
				if (i == 0) {
					line = "plot " + line;
				}
				if (i < this.data.length - 1) {
					line = line + " , \\";
				}
				script.add(line);
			}
		} else {
			if (!this.config.getxLabel().equals("null"))
				script.add("set xlabel \"" + this.config.getxLabel() + "\"");
			if (!this.config.getyLabel().equals("null"))
				script.add("set ylabel \"" + this.config.getyLabel() + "\"");
			if (!this.config.getxRange().equals("null"))
				script.add("set xrange " + this.config.getxRange());
			if (!this.config.getyRange().equals("null"))
				script.add("set yrange " + this.config.getyRange());
			if (this.config.getxTics() != null)
				script.add("set xtics " + this.config.getxTics());
			if (this.config.getyTics() != null)
				script.add("set ytics " + this.config.getyTics());
			if (this.config.getLogscale() != null)
				script.add("set logscale " + this.config.getLogscale());
			if (this.config.getKey() != null) {
				script.add("set key " + this.config.getKey());
			} else {
				if (this.cdfPlot)
					script.add("set key "
							+ Config.get(PlotConfig.gnuplotDefaultKeyCdfKey));
				else
					script.add("set key "
							+ Config.get(PlotConfig.gnuplotDefaultKeyKey));
			}
			for (int i = 0; i < this.data.length; i++) {
				String line = "";

				// determine plot type
				DistributionPlotType type = this.config.getDistPlotType();
				if (type.equals(DistributionPlotType.distANDcdf)) {
					if (this.cdfPlot)
						type = DistributionPlotType.cdfOnly;
					else
						type = DistributionPlotType.distOnly;
				}

				// get line
				line = this.data[i].getEntry(i + 1,
						Config.getInt("GNUPLOT_LW"), this.config.getxOffset()
								* i, this.config.getyOffset() * i,
						this.config.getxScaling(), this.config.getyScaling(),
						type, this.config.getStyle(), false);
				line = line.replace("filledcurves", "filledcurves y1=0");
				if (i == 0) {
					line = "plot " + line;
				}
				if (i < this.data.length - 1) {
					line = line + " , \\";
				}
				script.add(line);
			}
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
		this.datetime = dateTime;
	}

	public String getDateTime() {
		return this.datetime;
	}

	public void setPlotDateTime(boolean plotDateTime) {
		this.plotDateTime = plotDateTime;
	}

	public boolean isPlotDateTime() {
		return this.plotDateTime;
	}

	public void setTimeDataFormat(String timeDataFormat) {
		this.timeDataFormat = timeDataFormat;
	}

	public String getTimeDataFormat() {
		return this.timeDataFormat;
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
		return this.dataQuantity;
	}

	public void setDataQuantity(int dataQuantity) {
		this.dataQuantity = dataQuantity;
	}

	public int[] getSeriesDataQuantities() {
		return this.seriesDataQuantities;
	}

	public int getSeriesDataQuantity(int index) {
		return this.seriesDataQuantities[index];
	}

	public void setSeriesDataQuantities(int[] seriesDataQuantities) {
		this.seriesDataQuantities = seriesDataQuantities;
	}

	public void setErrorPrinted(boolean errorPrinted) {
		this.errorPrinted = errorPrinted;
	}

	public boolean isCdfPlot() {
		return this.cdfPlot;
	}

	public void setCdfPlot(boolean cdfPlot) {
		this.cdfPlot = cdfPlot;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public HashMap<Long, Long> getTimestampMap() {
		return timestampMap;
	}

	public void setTimestampMap(HashMap<Long, Long> timestampMap) {
		this.timestampMap = timestampMap;
	}

	public void setValueSortMode(ValueSortMode sortMode) {
		this.sortMode = sortMode;
	}

	public ValueSortMode getValueSortMode() {
		return this.sortMode;
	}

	public void setValueSortList(String[] valueSortList) {
		this.valueSortList = valueSortList;
	}

	public String[] getValueSortList() {
		return this.valueSortList;
	}

	/** Sorts the data according to the plot-config. **/
	public void sortData(PlotConfig config) {
		this.setValueSortMode(config.getValueSortMode());
		this.setValueSortList(config.getValueSortList());
		sortData();
	}

	/** Sorts the data according to the parameters. **/
	public void sortData(ValueSortMode valueSortMode, String[] valueSortList) {
		this.setValueSortMode(valueSortMode);
		this.setValueSortList(valueSortList);
		sortData();
	}

	/**
	 * Sorts the data according to the internal ValueSortMode and ValueSortList.
	 **/
	public void sortData() {
		// if null or sortmode = NONE or data empty -> return
		if (this.sortMode == null || this.sortMode.equals(ValueSortMode.NONE)
				|| this.data.length == 0)
			return;

		ValueSortMode mode = this.sortMode;
		String unqiue_delimiter = "§§§";

		// holds the indizes of all values
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++) {
			indexList.add(i);
		}

		// will be filled with sorted indizes
		ArrayList<Integer> sortedIndizesList = new ArrayList<Integer>(
				data.length);

		// different cases
		if (mode.equals(ValueSortMode.LIST_FIRST)
				|| mode.equals(ValueSortMode.ALPHABETICAL_LIST_LAST)) {
			// search for entries in plotdata list
			for (String entry : valueSortList) {
				for (int i = 0; i < data.length; i++) {
					PlotData d = data[i];
					String name = d.getName();
					if (d instanceof ExpressionData) {
						name = ((ExpressionData) d).getName().replace("$", "");
					} else if (d instanceof FunctionData) {
						name = ((FunctionData) d).getName();
					}

					// if found, add to list
					if (name.equals(entry)) {
						int index = indexList.indexOf(i);
						sortedIndizesList.add(index);
						indexList.remove(index);
					}
				}
			}

			// add normal objects last
			if (mode.equals(ValueSortMode.ALPHABETICAL_LIST_FIRST)) {
				// sort alphabetical
				Map<String, Integer> map = new HashMap<String, Integer>();
				ArrayList<String> namesList = new ArrayList<String>();
				for (int i = 0; i < indexList.size(); i++) {
					int index = indexList.get(i);
					PlotData d = data[index];
					String name = d.getName();
					if (d instanceof ExpressionData) {
						name = ((ExpressionData) d).getName().replace("$", "");
					} else if (d instanceof FunctionData) {
						name = ((FunctionData) d).getName();
					}

					map.put(name + unqiue_delimiter + d.getTitle(), index);
					namesList.add(name + unqiue_delimiter + d.getTitle());
				}

				// sort
				Collections.sort(namesList);

				// get sorted indizes from map and add data to sorted list
				for (String name : namesList) {
					sortedIndizesList.add(map.get(name));
				}
			} else {
				for (int i = 0; i < indexList.size(); i++)
					sortedIndizesList.add(indexList.get(i));
			}
		} else if (mode.equals(ValueSortMode.LIST_LAST)
				|| mode.equals(ValueSortMode.ALPHABETICAL_LIST_LAST)) {
			ArrayList<Integer> tempList = new ArrayList<Integer>();

			// search for entries in plotdata list
			for (String entry : valueSortList) {
				for (int i = 0; i < data.length; i++) {
					PlotData d = data[i];
					String name = d.getName();
					if (d instanceof ExpressionData) {
						name = ((ExpressionData) d).getName().replace("$", "");
					} else if (d instanceof FunctionData) {
						name = ((FunctionData) d).getName();
					}

					if (name.equals(entry)) {
						int index = indexList.indexOf(i);
						tempList.add(index);
						indexList.remove(index);
					}
				}
			}

			if (mode.equals(ValueSortMode.ALPHABETICAL_LIST_LAST)) {
				// sort alphabetical
				Map<String, Integer> map = new HashMap<String, Integer>();
				ArrayList<String> namesList = new ArrayList<String>();
				for (int i = 0; i < indexList.size(); i++) {
					int index = indexList.get(i);
					PlotData d = data[index];
					String name = d.getName();
					if (d instanceof ExpressionData) {
						name = ((ExpressionData) d).getName().replace("$", "");
					} else if (d instanceof FunctionData) {
						name = ((FunctionData) d).getName();
					}

					map.put(name + unqiue_delimiter + d.getTitle(), index);
					namesList.add(name + unqiue_delimiter + d.getTitle());
				}

				// sort
				Collections.sort(namesList);

				// get sorted indizes from map and add data to sorted list
				for (String name : namesList) {
					sortedIndizesList.add(map.get(name));
				}
			} else {
				// add normal objects first
				for (int i = 0; i < indexList.size(); i++) {
					sortedIndizesList.add(indexList.get(i));
				}
			}

			// add list objects last
			for (int i = 0; i < tempList.size(); i++)
				sortedIndizesList.add(tempList.get(i));

		} else if (mode.equals(ValueSortMode.ALPHABETICAL)) {
			// sort alphabetical
			Map<String, Integer> map = new HashMap<String, Integer>();
			ArrayList<String> namesList = new ArrayList<String>();
			for (int i = 0; i < data.length; i++) {
				PlotData d = data[i];
				String name = d.getName();
				if (d instanceof ExpressionData) {
					name = ((ExpressionData) d).getName().replace("$", "");
				} else if (d instanceof FunctionData) {
					name = ((FunctionData) d).getName();
				}

				map.put(name + unqiue_delimiter + d.getTitle(), i);
				namesList.add(name + unqiue_delimiter + d.getTitle());
			}

			// sort
			Collections.sort(namesList);

			// get sorted indizes from map and add data to sorted list
			for (String name : namesList)
				sortedIndizesList.add(map.get(name));
		}

		// copy sorted indizes
		for (int i = 0; i < sortedIndizesList.size(); i++) {
			this.dataIndizes[i] = sortedIndizesList.get(i);

			// updated sorted flag
			if (!this.sorted && this.dataIndizes[i] != i)
				this.sorted = true;
		}
	}
}
