package dna.latex;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.plot.PlotConfig;
import dna.plot.PlottingConfig;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.SeriesData;
import dna.util.Log;

/**
 * A TexFile object represents a tex file on the local filesystem.
 * 
 * @author Rwilmes
 * @date 24.11.2014
 */
public class TexFile {
	// variables
	private String dir;
	private String filename;
	private Writer writer;

	// open-flag
	private boolean open;

	// constructor
	public TexFile(String dir, String filename) {
		this.dir = dir;
		this.filename = filename;
		this.open = false;
		try {
			this.writer = Writer.getWriter(dir, filename);
			this.open = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// getters
	public String getDir() {
		return this.dir;
	}

	public String getFilename() {
		return this.filename;
	}

	public String getPath() {
		return this.dir + this.filename;
	}

	/** Writes a metric to the texfile. **/
	public void writeMetric(SeriesData s, AggregatedMetric m,
			AggregatedBatch[] batchData, String plotDir, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		String name = m.getName();
		this.writeLine(TexUtils.section(name));
		this.writeLine();

		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// add metric values
		if (config.isIncludeMetricValues()) {
			if (m.getValues().size() > 0) {
				this.writeLine(TexUtils.subsection("Values"));
				for (AggregatedValue v : m.getValues().getList()) {
					this.writeMetricValue(v, m, batchData, addedPlots, config,
							pconfig);
				}
				this.writeLine();
			}
		}

		// add distribution
		if (config.isIncludeDistributions()) {
			if (m.getDistributions().size() > 0) {
				this.writeLine(TexUtils.subsection("Distributions"));
				for (AggregatedDistribution d : m.getDistributions().getList()) {
					this.writeDistribution(d, m, s, batchData, addedPlots,
							config, pconfig);
				}
				this.writeLine();
			}
		}

		// add nodevaluelists
		if (config.isIncludeNodeValueLists()) {
			if (m.getNodeValues().size() > 0) {
				this.writeLine(TexUtils.subsection("NodeValueLists"));
				for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
					this.writeNodeValueList(n, m, s, batchData, addedPlots,
							config, pconfig);
				}
				this.writeLine();
			}
		}

		// add plots subsection
		if (addedPlots.size() > 0)
			TexUtils.addPlotsSubsection(this, plotDir, addedPlots);
	}

	/** Writes a value to the TexFile. **/
	private void writeMetricValue(AggregatedValue v, AggregatedMetric m,
			AggregatedBatch[] batchData, ArrayList<PlotConfig> addedPlots,
			TexConfig config, PlottingConfig pconfig) throws IOException {
		this.writeLine(TexUtils.subsubsection(v.getName()));
		this.writeLine(v.getName() + " is a metric value.");
		this.writeLine();

		// gather fitting plots
		ArrayList<PlotConfig> fits = TexUtils.getCustomMetricValuePlotFits(
				m.getName(), v.getName(), pconfig.getCustomMetricValuePlots());
		TexUtils.addDefaultMetricValuePlotFits(fits, m.getName(), v.getName());

		// add ref line
		if (fits.size() > 0) {
			String refs = TexUtils.getReferenceString(fits);
			this.writeLine(refs);
			this.writeLine();

			// add plots that contain the value
			for (PlotConfig pc : fits) {
				if (!addedPlots.contains(pc)) {
					addedPlots.add(pc);
				}
			}
		}

		// values
		this.writeCommentBlock("value table of " + v.getName());

		// select description
		String[] tableDescrArray = TexUtils.selectDescription(config);

		// init table
		SingleScalarTexTable table = new SingleScalarTexTable(this,
				tableDescrArray, config.getDateFormat(), config.getScaling(),
				config.getMapping());

		// add values
		for (AggregatedBatch b : batchData) {
			if (!b.getMetrics().getNames().contains(m.getName())
					&& !b.getMetrics().get(m.getName()).getValues().getNames()
							.contains(v.getName())) {
				table.addBlankRow(tableDescrArray.length - 1, b.getTimestamp());
			} else {
				// select values
				double[] selectedValues = TexUtils.selectValues(b.getMetrics()
						.get(m.getName()).getValues().get(v.getName()), config,
						false);

				// add row
				table.addRow(selectedValues, b.getTimestamp());
			}
		}

		// close table
		table.close();
		this.writeLine();
	}

	/** Writes a distribution to the TexFile. **/
	private void writeDistribution(AggregatedDistribution d,
			AggregatedMetric m, SeriesData s, AggregatedBatch[] batchData,
			ArrayList<PlotConfig> addedPlots, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		this.writeLine(TexUtils.subsubsection(d.getName()));
		this.writeLine(d.getName() + " is a distribution.");
		this.writeLine();

		// gather fitting plots
		ArrayList<PlotConfig> fits = TexUtils.getCustomDistributionPlotFits(
				m.getName(), d.getName(), pconfig.getCustomDistributionPlots());
		TexUtils.addDefaultDistributionPlotFits(fits, m.getName(), d.getName(),
				pconfig.getDistPlotType());

		// add ref line
		if (fits.size() > 0) {
			String refs = TexUtils.getReferenceString(fits);
			this.writeLine(refs);
			this.writeLine();

			// add plots that contain the value
			for (PlotConfig pc : fits) {
				if (!addedPlots.contains(pc)) {
					addedPlots.add(pc);
				}
			}
		}

		// values
		this.writeCommentBlock("value tables of " + d.getName());
		this.writeLine();

		// select description
		String[] tableDescrArray = TexUtils.selectDescription(config);
		tableDescrArray[0] = "x";

		// add values
		for (AggregatedBatch b : batchData) {
			long timestamp = b.getTimestamp();
			this.writeCommentLine("value table of timestamp " + timestamp);

			// map & scale timestamp
			long tempTimestamp = timestamp;

			// if mapping, map
			if (config.getMapping() != null) {
				if (config.getMapping().containsKey(tempTimestamp))
					tempTimestamp = config.getMapping().get(tempTimestamp);
			}

			// if scaling, scale
			if (config.getScaling() != null)
				tempTimestamp = TexTable.scaleTimestamp(tempTimestamp,
						config.getScaling());

			// init table
			MultiScalarTexTable table = new MultiScalarTexTable(this,
					tableDescrArray, tempTimestamp, config.getDateFormat());

			// read batch
			String readDir = Dir.getAggregationBatchDir(s.getDir(), timestamp);
			AggregatedBatch tempBatch = AggregatedBatch.read(readDir,
					timestamp, BatchReadMode.readOnlyDistAndNvl);

			// add lines
			if (!b.getMetrics().getNames().contains(m.getName())
					&& !b.getMetrics().get(m.getName()).getDistributions()
							.getNames().contains(d.getName())) {
				table.addBlankRow(tableDescrArray.length - 1, b.getTimestamp());
			} else {
				AggregatedValue[] values = tempBatch.getMetrics()
						.get(m.getName()).getDistributions().get(d.getName())
						.getValues();

				for (int i = 0; i < values.length; i++) {
					// select values
					double[] selectedValues = TexUtils
							.selectValuesFromDistribution(values[i], config);

					// add row to table
					table.addRow(selectedValues, i);
				}

			}

			// close table
			table.close();
			this.writeLine();
		}
	}

	/** Writes a nodevaluelist to the TexFile. **/
	private void writeNodeValueList(AggregatedNodeValueList n,
			AggregatedMetric m, SeriesData s, AggregatedBatch[] batchData,
			ArrayList<PlotConfig> addedPlots, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		this.writeLine(TexUtils.subsubsection(n.getName()));
		this.writeLine(n.getName() + " is a nodevaluelist.");
		this.writeLine();

		// gather fitting plots
		ArrayList<PlotConfig> fits = TexUtils
				.getCustomNodeValueListPlotFits(m.getName(), n.getName(),
						pconfig.getCustomNodeValueListPlots());
		TexUtils.addDefaultNodeValueListPlotFits(fits, m.getName(), n.getName());

		// add ref line
		if (fits.size() > 0) {
			String refs = TexUtils.getReferenceString(fits);
			this.writeLine(refs);
			this.writeLine();

			// add plots that contain the value
			for (PlotConfig pc : fits) {
				if (!addedPlots.contains(pc)) {
					addedPlots.add(pc);
				}
			}
		}

		// values
		this.writeCommentBlock("value tables of " + n.getName());
		this.writeLine();

		// check what data to add in table
		String[] tableDescrArray = TexUtils.selectDescription(config);
		tableDescrArray[0] = "Node";

		// add values
		for (AggregatedBatch b : batchData) {
			long timestamp = b.getTimestamp();
			this.writeCommentLine("value table of timestamp " + timestamp);

			// map & scale timestamp
			long tempTimestamp = timestamp;

			// if mapping, map
			if (config.getMapping() != null) {
				if (config.getMapping().containsKey(tempTimestamp))
					tempTimestamp = config.getMapping().get(tempTimestamp);
			}

			// if scaling, scale
			if (config.getScaling() != null)
				tempTimestamp = TexTable.scaleTimestamp(tempTimestamp,
						config.getScaling());

			// init table
			MultiScalarTexTable table = new MultiScalarTexTable(this,
					tableDescrArray, tempTimestamp, config.getDateFormat());

			// read batch
			String readDir = Dir.getAggregationBatchDir(s.getDir(), timestamp);
			AggregatedBatch tempBatch = AggregatedBatch.read(readDir,
					timestamp, BatchReadMode.readOnlyDistAndNvl);

			// add lines
			if (!b.getMetrics().getNames().contains(m.getName())
					&& !b.getMetrics().get(m.getName()).getNodeValues()
							.getNames().contains(n.getName())) {
				table.addBlankRow(tableDescrArray.length - 1, b.getTimestamp());
			} else {
				AggregatedValue[] values = tempBatch.getMetrics()
						.get(m.getName()).getNodeValues().get(n.getName())
						.getValues();

				for (int i = 0; i < values.length; i++) {
					// select values
					double[] selectedValuesArray = TexUtils
							.selectValuesFromNodeValueList(values[i], config);

					// add row to table
					table.addRow(selectedValuesArray, i);
				}
			}

			// close table
			table.close();
			this.writeLine();
		}
	}

	// tex methods
	/** Writes the default latex header to the texfile. **/
	public void writeHeader() throws IOException {
		if (open) {
			// auto gen
			this.writeCommentBlock("This is an auto-generated tex-file from DNA - dynammic network analyzer.");
			this.writeLine();

			// commands for frontpage
			this.writeCommentLine("commands for frontpage");
			this.writeCommand("Project", "Auto-generated");
			this.writeCommand("DocumentType", "LaTeX output");
			this.writeCommand("AuthorName", System.getProperty("user.name"));
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Calendar cal = Calendar.getInstance();
			this.writeCommand("Date", dateFormat.format(cal.getTime()));
			this.writeCommand("Institute", TexUtils.institute);
			this.writeLine();

			// begin document
			this.writeCommentLine(TexUtils.beginOfDocument);

			// include header, titlepage, adjust pagenumbering
			this.include(TexUtils.chapterDirectory + Dir.delimiter
					+ TexUtils.headerFilename);
			this.writeLine(TexUtils.pagenumbering("roman"));
			this.writeLine(TexUtils.setcounter("" + 0));
			this.include(TexUtils.generateTitlepage(this.getDir()
					+ TexUtils.chapterDirectory + Dir.delimiter,
					TexUtils.titlePageFilename + TexUtils.texSuffix));
			this.writeLine(TexUtils.pagenumbering("arabic"));
			this.writeLine();
		} else {
			Log.warn("Attempt to write to closed TexFile " + this.getPath()
					+ "!");
		}
	}

	public void include(TexFile chapter) throws IOException {
		this.include(TexUtils.chapterDirectory + Dir.delimiter
				+ chapter.getFilename().replaceAll(TexUtils.texSuffix, ""));
	}

	public void include(String chapter) throws IOException {
		this.writeLine(TexUtils.include(chapter));
	}

	public void includeFigure(String dir, String filename) throws IOException {
		this.includeFigure(null, dir, filename, "", 0.8, "h");
	}

	public void includeFigure(String name, String dir, String filename)
			throws IOException {
		this.includeFigure(name, dir, filename, "", 0.8, "h");
	}

	public void includeFigure(String name, String dir, String filename,
			String extension, double scale, String option) throws IOException {
		if (name != null)
			this.writeCommentLine("plot " + filename + " containing " + name);
		else
			this.writeCommentLine("plot " + filename);
		this.writeLine(TexUtils.beginFigure(option));
		this.writeLine(TexUtils.tab + TexUtils.centering());
		this.writeLine(TexUtils.tab
				+ TexUtils.includeGraphics(dir + filename + extension, scale));
		this.writeLine(TexUtils.tab + TexUtils.caption(filename));
		this.writeLine(TexUtils.tab
				+ TexUtils.label(TexUtils.getPlotLabel(filename)));
		this.writeLine(TexUtils.endFigure());
		this.writeLine();
	}

	public void writeCommentLine(String comment) throws IOException {
		if (open) {
			String line = TexUtils.commentIdentifier;
			if (comment != null || comment.length() > 0)
				line += " " + comment;
			this.writer.writeln(line);
		} else {
			Log.warn("Attempt to write to closed TexFile " + this.getPath()
					+ "!");
		}
	}

	public void writeCommentBlock(String comment) throws IOException {
		if (open) {
			this.writeCommentLine("");
			this.writeCommentLine(comment);
			this.writeCommentLine("");
		} else {
			Log.warn("Attempt to write to closed TexFile " + this.getPath()
					+ "!");
		}
	}

	public void writeCommand(String name, String content) throws IOException {
		if (open) {
			this.writer.writeln("\\newcommand {\\" + name + "}\t\t\t\t{"
					+ content + "}");
		} else {
			Log.warn("Attempt to write to closed TexFile " + this.getPath()
					+ "!");
		}
	}

	public void writeLine(String line) throws IOException {
		if (open) {
			this.writer.writeln(line);
		} else {
			Log.warn("Attempt to write to closed TexFile " + this.getPath()
					+ "!");
		}
	}

	public void writeLine() throws IOException {
		if (open) {
			this.writer.writeln("");
		} else {
			Log.warn("Attempt to write to closed TexFile " + this.getPath()
					+ "!");
		}
	}

	/**
	 * Closes the tex file and ends the tex document. Has to be called during
	 * runtime to close the filewriter.
	 **/
	public void closeAndEnd() {
		if (open) {
			try {
				this.writeCommentLine(TexUtils.endOfDocument);
				this.writer.writeln(TexUtils.end("document"));
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.warn("Attempt to close TexFile '" + this.getPath()
					+ "' but its already closed!");
		}
	}

	/**
	 * Closes the tex file. Has to be called during runtime to close the
	 * filewriter.
	 **/
	public void close() {
		if (open) {
			try {
				this.writeCommentLine("end of document");
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.warn("Attempt to close TexFile '" + this.getPath()
					+ "' but its already closed!");
		}
	}
}
