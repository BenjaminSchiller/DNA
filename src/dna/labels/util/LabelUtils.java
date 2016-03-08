package dna.labels.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dna.io.Reader;
import dna.io.filesystem.Dir;
import dna.labels.Label;
import dna.labels.LabelList;
import dna.series.data.BatchData;
import dna.util.Config;

/**
 * Utility class for DNA labeling.
 * 
 * @author Rwilmes
 * 
 */
public class LabelUtils {

	/**
	 * Analyzes a label-list and compares all labels with the key-label. Then
	 * returns a HashMap mapping label-identifiers -> LabelStat objects.<br>
	 * <br>
	 * 
	 * <b>Note:</b> KeyLabel of format: $label_name$:$label_type$
	 */
	public static HashMap<String, LabelStat> analyzeLabelList(String dir,
			String filename, int conditionLifeTime, Label keyLabel)
			throws IOException {
		ArrayList<BatchData> list = readBatchLabelsFromList(dir, filename);

		// mapping label-identifiers to labelstat objects
		HashMap<String, LabelStat> map = new HashMap<String, LabelStat>();

		// iterate over all batches and gather labels
		for (BatchData batch : list) {
			for (Label l : batch.getLabels().getList()) {
				String identifier = l.getName() + ":" + l.getType();
				if (!map.containsKey(identifier))
					map.put(identifier, new LabelStat(identifier));
			}
		}

		long lastTimeKeyOccured = -1;

		// iterate over batches
		for (BatchData batch : list) {
			long timestamp = batch.getTimestamp();
			LabelList ll = batch.getLabels();
			boolean keyLabelPresent = false;
			boolean timeConditionMet = false;
			for (Label l : ll.getList()) {
				if (l.getName().equals(keyLabel.getName())
						&& l.getType().equals(keyLabel.getType()))
					keyLabelPresent = true;
			}

			if (keyLabelPresent) {
				lastTimeKeyOccured = timestamp;
			} else {
				if (timestamp <= (lastTimeKeyOccured + conditionLifeTime))
					timeConditionMet = true;
			}

			// iterate over labels
			for (String identifier : map.keySet()) {
				LabelStat ls = map.get(identifier);

				boolean labelContained = false;
				for (Label l : ll.getList()) {
					if (identifier.equals(l.getName() + ":" + l.getType())) {
						labelContained = true;
					}
				}

				if (labelContained) {
					if (keyLabelPresent) {
						ls.incrTruePositives();
					} else {
						if (timeConditionMet) {
							ls.incrCondPositives();
						} else {
							ls.incrFalsePositives();
						}
					}
				} else {
					if (keyLabelPresent) {
						ls.incrFalseNegatives();
					} else {
						ls.incrTrueNegatives();
					}
				}

				map.put(identifier, ls);
			}

		}

		return map;
	}

	/**
	 * Reads a batch-list and returns a list of BatchData objects containing
	 * labels.
	 **/
	public static ArrayList<BatchData> readBatchLabelsFromList(String dir,
			String filename) throws IOException {
		String delimiter = Config.get("DATA_DELIMITER");
		String valueSeparator = Config.get("LABEL_VALUE_SEPARATOR");
		String nameTypeSeparator = Config.get("LABEL_NAME_TYPE_SEPARATOR");

		ArrayList<BatchData> batchList = new ArrayList<BatchData>();
		ArrayList<Long> timestampList = new ArrayList<Long>();

		// read all lines
		Reader r = new Reader(dir, filename);
		String line;
		while ((line = r.readString()) != null) {
			String[] splits = line.split(delimiter);
			long timestamp = Dir.getTimestamp(splits[0]);

			BatchData batch;

			if (!timestampList.contains(timestamp)) {
				batch = new BatchData(timestamp);
				batchList.add(batch);
				timestampList.add(timestamp);
			} else {
				batch = batchList.get(timestampList.indexOf(timestamp));
			}

			String[] valueSplit = splits[1].split("\\" + valueSeparator);
			String[] nameTypeSplit = valueSplit[0].split("\\"
					+ nameTypeSeparator);

			batch.getLabels()
					.add(new Label(nameTypeSplit[0], nameTypeSplit[1],
							valueSplit[1]));
		}

		r.close();
		return batchList;
	}

}
