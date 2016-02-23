package dna.labels;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.BatchData;
import dna.series.data.SeriesData;

public class LabelUtils {

	/**
	 * Collects all labels from the series in the specified run and returns all
	 * batches which contain one of the specified labels.
	 **/
	public static ArrayList<BatchData> collectLabels(SeriesData sd, int runId,
			Label... labelFilter) throws IOException {
		String seriesDir = sd.getDir();
		ArrayList<BatchData> batchList = new ArrayList<BatchData>();
		ArrayList<BatchData> batches = sd.getRun(runId).getBatches().getList();

		for (int i = 0; i < batches.size(); i++) {
			BatchData batch = batches.get(i);
			long timestamp = batch.getTimestamp();

			// read data
			BatchData dataBatch = BatchData.readIntelligent(
					Dir.getBatchDataDir(seriesDir, runId, timestamp),
					timestamp, BatchReadMode.readOnlySingleValues);

			LabelList tempList = dataBatch.getLabels();
			if (dataBatch.getLabels().size() > 0) {
				LabelList list = new LabelList();

				// check for matching labels
				for (Label label1 : tempList.getList()) {
					for (Label label2 : labelFilter) {
						if (label1.getName().equals(label2.getName())) {
							list.add(label1);
						}
					}
				}

				// if labels are found -> craft batch and add to batchlist
				if (list.size() > 0) {
					BatchData b = new BatchData(timestamp, null, null, null,
							null, list);
					batchList.add(b);
				}
			}
		}

		return batchList;
	}

	/**
	 * Writes all specified labels which occur in the batches to the specified
	 * list-file.
	 **/
	public static void writeLabelsToList(String dir, String filename,
			ArrayList<BatchData> batchList, Label... labels) throws IOException {
		Writer w = new Writer(dir, filename);

		int id = 0;
		for (BatchData bd : batchList) {
			String line = id + "\t" + bd.getTimestamp();

			for (Label l : labels) {
				if (bd.getLabels().get(l.getName()) != null) {
					line += "\t" + bd.getLabels().get(l.getName()).toString();
				} else {
					line += "\t" + "-";
				}
			}

			line += "\n";

			w.writeln(line);

			id++;
		}

		w.close();
	}
}
