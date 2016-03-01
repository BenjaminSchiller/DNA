package dna.parallel.util;

import java.io.File;

import dna.graph.Graph;
import dna.io.BatchReader;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class ReadableDirConsecutiveWaitingBatchGenerator extends BatchGenerator {

	protected String dir;
	protected String suffix;

	protected Sleeper sleeper;

	public ReadableDirConsecutiveWaitingBatchGenerator(String name, String dir,
			String suffix, Sleeper sleeper) {
		super(name);
		this.dir = dir;
		this.suffix = suffix;
		this.sleeper = sleeper;
	}

	@Override
	public Batch generate(Graph g) {
		this.sleeper.reset();
		String filename = (g.getTimestamp() + 1) + this.suffix;
		while (!this.sleeper.isTimedOut()) {
			if (((new File(dir + filename))).exists()) {
				this.sleeper.reset();
				return BatchReader.read(this.dir, filename, g);
			}
			this.sleeper.sleep();
		}
		throw new IllegalStateException("could not read batch from " + this.dir
				+ filename);
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return true;
	}
}
