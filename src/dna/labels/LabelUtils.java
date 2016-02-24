package dna.labels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
		return LabelUtils.collectLabels(sd, runId, new Label[0], new int[0],
				new int[0], labelFilter);
	}

	/**
	 * Collects all labels from the series in the specified run and returns all
	 * batches which contain one of the specified labels.<br>
	 * <br>
	 * 
	 * Also adds the given label to all batches in the interval (from;to).
	 **/
	public static ArrayList<BatchData> collectLabels(SeriesData sd, int runId,
			Label labelToAdd, int from, int to, Label... labelFilter)
			throws IOException {
		return LabelUtils.collectLabels(sd, runId, new Label[] { labelToAdd },
				new int[] { from }, new int[] { to }, labelFilter);
	}

	/**
	 * Collects all labels from the series in the specified run and returns all
	 * batches which contain one of the specified labels. <br>
	 * <br>
	 * 
	 * Via the Label[] labelsToAdd array it is possible to add desired labels in
	 * the given timeframes int[] from - int[] to.<br>
	 * <br>
	 * 
	 * Label <b>i</b> will be added to all batches between from[i] and to[i].
	 **/
	public static ArrayList<BatchData> collectLabels(SeriesData sd, int runId,
			Label[] labelsToAdd, int[] from, int[] to, Label... labelFilter)
			throws IOException {
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

			// add new labels
			for (int j = 0; j < labelsToAdd.length; j++) {
				if (from[j] <= timestamp && timestamp <= to[j]) {
					if (!dataBatch.getLabels().getList()
							.contains(labelsToAdd[j])) {
						dataBatch.getLabels().add(labelsToAdd[j]);
					}
				}
			}

			// check if labels exist
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
		LabelUtils.writeLabelsToList(dir, filename, batchList, null, 0, false,
				labels);
	}

	/**
	 * Writes all specified labels which occur in the batches to the specified
	 * list-file.
	 * 
	 * @param dir
	 *            Destination directory.
	 * @param filename
	 *            Destination file.
	 * @param batchList
	 *            List of batches containing the labels.
	 * @param dateTimeFormat
	 *            Format the timestamp will be written in. (null for no
	 *            transformation)
	 * @param writeLabel
	 *            If true the actual Label name, value and type will be written.
	 *            Else just a 1 when it occurs.
	 * @param labels
	 *            Specifies which labels will be written and in which order.
	 * @throws IOException
	 */
	public static void writeLabelsToList(String dir, String filename,
			ArrayList<BatchData> batchList, String dateTimeFormat,
			int timestampOffsetSeconds, boolean writeLabel, Label... labels)
			throws IOException {
		Writer w = new Writer(dir, filename);

		int id = 0;

		SimpleDateFormat format = (dateTimeFormat == null) ? null
				: new SimpleDateFormat(dateTimeFormat);

		for (BatchData bd : batchList) {
			String line = "" + id;

			if (dateTimeFormat == null) {
				line += "\t" + (bd.getTimestamp() + timestampOffsetSeconds);
			} else {
				Date date = new Date(
						(bd.getTimestamp() + timestampOffsetSeconds) * 1000);
				line += "\t" + format.format(date);
			}

			for (Label l : labels) {
				if (bd.getLabels().get(l.getName()) != null) {
					if (writeLabel)
						line += "\t"
								+ bd.getLabels().get(l.getName()).toString();
					else
						line += "\t" + "1";
				} else {
					line += "\t" + "0";
				}
			}

			w.writeln(line);

			id++;
		}

		w.close();
	}

	/** Adds labels to the batches in the given timewindow. **/
	public static ArrayList<BatchData> addLabelsToBatches(
			ArrayList<BatchData> batches, Label label, int from, int to) {
		return LabelUtils.addLabelsToBatches(batches, new Label[] { label },
				new int[] { from }, new int[] { to });
	}

	/** Adds labels to the batches in the given timewindow. **/
	public static ArrayList<BatchData> addLabelsToBatches(
			ArrayList<BatchData> batches, Label[] labels, int[] from, int[] to) {
		for (BatchData bd : batches) {
			long timestamp = bd.getTimestamp();
			LabelList list = bd.getLabels();
			for (int i = 0; i < labels.length; i++) {
				if (from[i] <= timestamp && timestamp <= to[i]) {
					if (!list.getList().contains(labels[i])) {
						list.add(labels[i]);
					}
				}
			}
		}

		return batches;
	}
}
