package dna.labels.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dna.io.Reader;
import dna.io.filesystem.Dir;
import dna.util.Config;
import dna.util.Log;

/**
 * Utility class for DNA labeling.
 * 
 * @author Rwilmes
 * 
 */
public class LabelUtils {

	/** Note: keyLabel of format: $label_name$:$label_type$ **/
	public static HashMap<String, LabelStat> analyzeLabelList(String dir,
			String filename, int conditionLifeTime, String keyLabel)
			throws IOException {

		ArrayList<ArrayList<String>> list = readBatchLabelsFromList(dir,
				filename);

		// mapping label-identifiers to labelstat objects
		HashMap<String, LabelStat> map = new HashMap<String, LabelStat>();

		Log.infoSep();
		// iterate over batches
		for (ArrayList<String> ll : list) {
			boolean keyLabelPresent = ll.contains(keyLabel);

			// make sure all labels have a labelstat in the map
			for (String l : ll) {
				if (!map.containsKey(l))
					map.put(l, new LabelStat(l));
			}
		}

		// iterate over batches
		for (ArrayList<String> ll : list) {
			boolean keyLabelPresent = ll.contains(keyLabel);

			// iterate over labels
			for (String l : map.keySet()) {
				LabelStat ls = map.get(l);

				if (ll.contains(l)) {
					if (keyLabelPresent) {
						ls.incrTruePositives();
					} else {
						ls.incrFalsePositives();
					}
				} else {
					if (keyLabelPresent) {
						ls.incrFalseNegatives();
					} else {
						ls.incrTrueNegatives();
					}
				}

				map.put(l, ls);
			}
		}

		return map;
	}

	/** Reads a batch-list and returns a list of a list of label-identifiers. **/
	public static ArrayList<ArrayList<String>> readBatchLabelsFromList(
			String dir, String filename) throws IOException {
		Reader r = new Reader(dir, filename);
		String line;

		String delimiter = Config.get("DATA_DELIMITER");

		ArrayList<String> labels = null;
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

		long currentTimestamp = -1;
		while ((line = r.readString()) != null) {
			String[] splits = line.split(delimiter);
			long timestamp = Dir.getTimestamp(splits[0]);
			String labelIdentifier = splits[1].split("\\"
					+ Config.get("LABEL_VALUE_SEPARATOR"))[0];

			if (timestamp != currentTimestamp) {
				if (labels != null) {
					list.add(new ArrayList<String>(labels));
				}
				currentTimestamp = timestamp;
				labels = new ArrayList<String>();
			}

			labels.add(labelIdentifier);
		}

		if (labels.size() > 0)
			list.add(labels);

		return list;
	}

}
