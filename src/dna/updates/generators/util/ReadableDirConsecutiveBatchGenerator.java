package dna.updates.generators.util;

import java.io.File;

import dna.graph.Graph;
import dna.io.BatchReader;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class ReadableDirConsecutiveBatchGenerator extends BatchGenerator {

	protected String dir;
	protected String suffix;

	public ReadableDirConsecutiveBatchGenerator(String name, String dir,
			String suffix) {
		super(name);
		this.dir = dir;
		this.suffix = suffix;
	}

	@Override
	public Batch generate(Graph g) {
		return BatchReader.read(this.dir, (g.getTimestamp() + 1) + suffix, g);
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return ((new File(dir + (g.getTimestamp() + 1) + this.suffix)))
				.exists();
	}
}
