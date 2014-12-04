package dna.latex;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sun.util.calendar.LocalGregorianCalendar.Date;
import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedValue;
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
	public void writeMetric(AggregatedMetric m) throws IOException {
		String name = m.getName();
		this.writeLine(TexUtils.subsection(name));
		this.writeLine();

		if (m.getValues().size() > 0) {
			this.writeLine(TexUtils.subsection("Values"));
			for (AggregatedValue v : m.getValues().getList()) {
				this.writeValue(v);
			}
			this.writeLine();
		}

		if (m.getDistributions().size() > 0) {
			this.writeLine(TexUtils.subsection("Distributions"));
			for (AggregatedDistribution d : m.getDistributions().getList()) {
				this.writeDistribution(d);
			}
			this.writeLine();
		}

		if (m.getNodeValues().size() > 0) {
			this.writeLine(TexUtils.subsection("Distributions"));
			for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
				this.writeNodeValueList(n);
			}
			this.writeLine();
		}
	}

	/** Writes a value to the TexFile. **/
	private void writeValue(AggregatedValue v) throws IOException {
		this.writer.writeln(TexUtils.textBf(v.getName()) + TexUtils.newline);
	}

	/** Writes a distribution to the TexFile. **/
	private void writeDistribution(AggregatedDistribution d) throws IOException {
		this.writer.writeln(TexUtils.textBf(d.getName()) + TexUtils.newline);
	}

	/** Writes a nodevaluelist to the TexFile. **/
	private void writeNodeValueList(AggregatedNodeValueList n)
			throws IOException {
		this.writer.writeln(TexUtils.textBf(n.getName()) + TexUtils.newline);
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
				+ chapter.getFilename());
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
