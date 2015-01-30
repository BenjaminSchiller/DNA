package dna.visualization.demo;

import java.io.File;

import dna.plot.Plotting;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.util.Execute;
import dna.util.Log;
import dna.visualization.MainDisplay;

public class DemoExecution {

	public static void generate(Demo demo) {
		log(demo, "GENERATE");

		try {
			if ((new File(demo.getDataDir())).exists()) {
				Execute.exec("rm -r " + demo.getDataDir());
			}

			Series s = new Series(demo.getGG(), demo.getBG(),
					demo.getMetrics(), demo.getDataDir(), demo.getName());
			s.generate(demo.getRuns(), demo.getBatches());

			Execute.exec("open " + demo.getDataDir());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void visualize(Demo demo) {
		log(demo, "VISUALIZE");
		try {
			// MainDisplay.main(new String[] { "-c", demo.getCfg(), "-d",
			// demo.getRunDir(), "-p" });
			MainDisplay.main(new String[] { "-c", demo.getCfg(), "-d",
					demo.getRunDir(), "-p" });
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void plot(Demo demo) {
		log(demo, "PLOT");

		try {
			if ((new File(demo.getPlotDir())).exists()) {
				Execute.exec("rm -r " + demo.getPlotDir());
			}
			SeriesData sd = SeriesData.read(demo.getDataDir(), demo.getName(),
					true, true);
			Plotting.plot(sd, demo.getPlotDir());
			Execute.exec("open " + demo.getPlotDir());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void tex(Demo demo) {
		log(demo, "TEX");
		try {
			Execute.exec("open /Users/benni/TUD/Students/rene.wilmes.hiwi/_readme/15-01-11.-.tex.sample.rene.wilmes.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void live(Demo demo) {
		log(demo, "LIVE");

		LiveGenerationThread gen = new LiveGenerationThread(demo);
		LiveVisualizationThread vis = new LiveVisualizationThread(demo);

		gen.start();
		vis.start();
	}

	private static void log(Demo demo, String action) {
		Log.infoSep();
		Log.info(action);
		Log.infoSep();
		Log.info("gg:      " + demo.gg);
		Log.info("bg:      " + demo.bg);
		Log.info("batches: " + demo.batches);
		Log.info("runs:    " + demo.runs);
		Log.infoSep();
	}
}
