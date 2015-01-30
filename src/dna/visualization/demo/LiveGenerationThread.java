package dna.visualization.demo;

import java.io.File;
import java.io.IOException;

import dna.metrics.MetricNotApplicableException;
import dna.series.AggregationException;
import dna.series.Series;
import dna.util.Execute;
import dna.util.Log;

public class LiveGenerationThread extends Thread {

	private Demo demo;

	public LiveGenerationThread(Demo demo) {
		this.demo = demo;
	}

	public void run() {
		try {
			Log.info("starting GENERATION thread");
			if ((new File(demo.getDataDir())).exists()) {
				Execute.exec("rm -r " + demo.getDataDir());
			}
			Log.info("output: " + demo.getRunDir());
			Series s = new Series(demo.getGG(), demo.getBG(),
					demo.getMetrics(), demo.getDataDir(), demo.getName());
			s.generate(1, demo.getBatches(), demo.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
