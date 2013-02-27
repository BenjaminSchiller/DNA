package dna.plot;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dna.io.Settings;
import dna.io.Writer;
import dna.plot.data.PlotData;
import dna.util.Execute;
import dna.util.Log;

public class Plot {

	private String terminal = "png";

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

	public Plot(PlotData[] data, String dir, String filename,
			String scriptFilename) {
		this.data = data;
		this.dir = dir;
		this.filename = filename;
		this.scriptFilename = scriptFilename;
	}

	public void write(String filename) throws IOException {
		Writer writer = new Writer(filename);
		List<String> script = this.getScript();
		for (String line : script) {
			writer.writeln(line);
		}
		writer.close();
	}

	protected List<String> getScript() {
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

		script.add("set style fill empty");
		script.add("set boxwidth 0.2");

		for (int i = 0; i < this.data.length; i++) {
			String line = this.data[i].getEntry(i + 1, this.lw, this.xOffset
					* i, this.yOffset * i);
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

	public void generate() throws IOException, InterruptedException {
		Log.info("  => \"" + this.filename + "\" in " + this.dir);
		this.write(this.dir + this.scriptFilename);
		Execute.exec(Settings.gnuplotPath + " " + this.dir
				+ this.scriptFilename, true);
	}
}
