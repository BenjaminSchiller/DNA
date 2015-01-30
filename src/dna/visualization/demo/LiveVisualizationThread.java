package dna.visualization.demo;

import dna.util.Log;
import dna.visualization.MainDisplay;

public class LiveVisualizationThread extends Thread {

	private Demo demo;

	public LiveVisualizationThread(Demo demo) {
		this.demo = demo;
	}

	public void run() {
		try {
			Log.info("starting VISUALIZATION thread");
			Log.info("input: " + demo.getRunDir());
			MainDisplay.main(new String[] { "-c", demo.getCfg(), "-d",
					demo.getRunDir(), "-l" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
