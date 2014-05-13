package dna.updates.generators.util;

import java.io.FilenameFilter;

import dna.graph.Graph;
import dna.io.LegacyBatchReader;
import dna.updates.batch.Batch;

public class LegacyReadableDirBatchGenerator extends ReadableDirBatchGenerator {

	public LegacyReadableDirBatchGenerator(String name, String dir) {
		super(name, dir);
	}

	public LegacyReadableDirBatchGenerator(String name, String dir,
			FilenameFilter filter) {
		super(name, dir, filter);
	}

	@Override
	public Batch generate(Graph g) {
		return LegacyBatchReader.read(dir, this.filenames[this.index++], g);
	}

}
