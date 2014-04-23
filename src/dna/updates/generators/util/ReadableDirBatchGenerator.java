package dna.updates.generators.util;

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import dna.graph.Graph;
import dna.io.BatchReader;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.IOUtils;

/**
 * 
 * Batch generators that reads batches from a given directory. All files
 * (filtered by an optional FilenameFilter) are sorted by ascending
 * from-timestamp. Every time a new batch is generated, the next file is read.
 * When all batches are read, the process start again at the beginning. Please
 * note that no sanity check is performed if the batches (ordered by
 * from-timestamp) actually can be executed one after the other.
 * 
 * @author benni
 * 
 */
public class ReadableDirBatchGenerator extends BatchGenerator {

	protected String dir;

	protected String[] filenames;

	protected int index;

	public ReadableDirBatchGenerator(String name, String dir) {
		this(name, dir, null);
	}

	public ReadableDirBatchGenerator(String name, String dir,
			FilenameFilter filter) {
		super(name);
		this.dir = dir;
		this.filenames = IOUtils.getFilenames(dir, filter);
		HashMap<String, long[]> timestamps = new HashMap<String, long[]>();
		for (String filename : this.filenames) {
			try {
				timestamps.put(filename,
						BatchReader.readTimestamps(this.dir, filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		TimestampOrderComparator comp = new TimestampOrderComparator(timestamps);
		Arrays.sort(this.filenames, comp);
		this.index = 0;

		for (String filename : filenames) {
			long[] t = timestamps.get(filename);
			// System.out.println(filename + " - " + t[0] + " => " + t[1]);
		}
	}

	@Override
	public Batch generate(Graph g) {
		return BatchReader.read(dir, this.filenames[this.index++], g);
	}

	@Override
	public void reset() {
		this.index = 0;
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return index < this.filenames.length;
	}

	public class TimestampOrderComparator implements Comparator<String> {

		private HashMap<String, long[]> timestamps;

		public TimestampOrderComparator(HashMap<String, long[]> timestamps) {
			this.timestamps = timestamps;
		}

		@Override
		public int compare(String o1, String o2) {
			return (int) (this.timestamps.get(o1)[0] - this.timestamps.get(o2)[0]);
		}

	}

}
