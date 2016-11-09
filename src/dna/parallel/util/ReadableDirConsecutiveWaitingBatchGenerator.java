package dna.parallel.util;

import java.io.File;

import dna.graph.Graph;
import dna.io.BatchReader;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.Timer;

public class ReadableDirConsecutiveWaitingBatchGenerator extends BatchGenerator {

	protected String dir;
	protected String suffix;

	protected Sleeper sleeper;

	public long idleTime = 0;
	public long readTime = 0;

	public static final String idleTimeName = "BatchGeneratorIdleTime";
	public static final String readTimeName = "BatchGeneratorReadTime";

	public ReadableDirConsecutiveWaitingBatchGenerator(String name, String dir,
			String suffix, Sleeper sleeper) {
		super(name);
		this.dir = dir;
		this.suffix = suffix;
		this.sleeper = sleeper;
	}

	@Override
	public Batch generate(Graph g) {
		Timer t1 = new Timer();
		this.sleeper.reset();
		String filename = (g.getTimestamp() + 1) + this.suffix;
		while (!this.sleeper.isTimedOut()) {
			if (((new File(dir + filename))).exists()) {
				this.sleeper.reset();
				t1.end();
				this.idleTime = t1.getDutation();
				Timer t2 = new Timer();
				Batch b = BatchReader.read(this.dir, filename, g);
				t2.end();
				this.readTime = t2.getDutation();
				return b;
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
