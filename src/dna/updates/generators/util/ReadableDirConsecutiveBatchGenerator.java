package dna.updates.generators.util;

import java.io.File;

import dna.graph.IGraph;
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
	public Batch generate(IGraph g) {
		return BatchReader.read(this.dir, (g.getTimestamp() + 1) + suffix, g);
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return ((new File(dir + (g.getTimestamp() + 1) + this.suffix)))
				.exists();
	}
}
