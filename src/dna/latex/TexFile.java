package dna.latex;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.latex.TexTable.TableFlag;
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
			AggregatedBatch[] batchData, TexConfig config) throws IOException {
		String name = m.getName();
		this.writeLine(TexUtils.section(name));
		this.writeLine();

		if (m.getValues().size() > 0) {
			this.writeLine(TexUtils.subsection("Values"));
			for (AggregatedValue v : m.getValues().getList()) {
				this.writeMetricValue(v, m, batchData, config);
			}
			this.writeLine();
		}

		if (m.getDistributions().size() > 0) {
			this.writeLine(TexUtils.subsection("Distributions"));
			for (AggregatedDistribution d : m.getDistributions().getList()) {
				this.writeDistribution(d, m, s, batchData, config);
			}
			this.writeLine();
		}

		if (m.getNodeValues().size() > 0) {
			this.writeLine(TexUtils.subsection("NodeValueLists"));
			for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
				this.writeNodeValueList(n, m, s, batchData, config);
			}
			this.writeLine();
		}
	}

	/** Writes a value to the TexFile. **/
	private void writeMetricValue(AggregatedValue v, AggregatedMetric m,
			AggregatedBatch[] batchData, TexConfig config) throws IOException {
		this.writeLine(TexUtils.subsubsection(v.getName()));
		this.writeLine(v.getName() + " is a metric value.");
		this.writeLine();
		this.writeCommentBlock("value table of " + v.getName());

		// select description
		String[] tableDescrArray = TexUtils.selectDescription(config);

		// init table
		TexTable table = new TexTable(this, tableDescrArray);

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
			TexConfig config) throws IOException {
		this.writeLine(TexUtils.subsubsection(d.getName()));
		this.writeLine(d.getName() + " is a distribution.");
		this.writeLine();
		this.writeCommentBlock("value tables of " + d.getName());
		this.writeLine();

		// select description
		String[] tableDescrArray = TexUtils.selectDescription(config);
		tableDescrArray[0] = "x";

		// add values
		for (AggregatedBatch b : batchData) {
			long timestamp = b.getTimestamp();
			this.writeCommentLine("value table of timestamp " + timestamp);

			// init table
			TexTable table = new TexTable(this, tableDescrArray, timestamp);

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
					table.addRow(selectedValues, timestamp);
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
			TexConfig config) throws IOException {
		this.writeLine(TexUtils.subsubsection(n.getName()));
		this.writeLine(n.getName() + " is a nodevaluelist.");
		this.writeLine();
		this.writeCommentBlock("value tables of " + n.getName());
		this.writeLine();

		// check what data to add in table
		String[] tableDescrArray = TexUtils.selectDescription(config);
		tableDescrArray[0] = "Node";

		// add values
		for (AggregatedBatch b : batchData) {
			long timestamp = b.getTimestamp();
			this.writeCommentLine("value table of timestamp " + timestamp);

			// init table
			TexTable table = new TexTable(this, tableDescrArray, timestamp);

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
			this.writer.writeln(TexUtils.begin("document"));
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
