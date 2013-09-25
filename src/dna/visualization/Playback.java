package dna.visualization;

import java.io.IOException;

import dna.series.data.BatchData;

public class Playback {

	// constructors
	public Playback(String dir, long intervall) throws IOException,
			InterruptedException {
		BatchHandler bh = new BatchHandler(dir);
		bh.updateBatches();

		while (bh.isNewBatchAvailable()) {
			BatchData tempBatch = bh.getNextBatch();
			System.out.println(tempBatch.getTimestamp() + "\t"
					+ tempBatch.getGeneralRuntimes().get("overhead") + "\t"
					+ tempBatch.getGeneralRuntimes().get("sum"));
			// wait timeout interval for playing in next batch
			Thread.sleep(intervall);
		}
	}
}
