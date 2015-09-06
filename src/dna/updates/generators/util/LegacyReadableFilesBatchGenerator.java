package dna.updates.generators.util;

import dna.graph.IGraph;
import dna.io.LegacyBatchReader;
import dna.updates.batch.Batch;

public class LegacyReadableFilesBatchGenerator extends
		ReadableFilesBatchGenerator {

	public LegacyReadableFilesBatchGenerator(String dir, String prefix,
			FilenameIndexType filenameType, String suffix) {
		super(dir, prefix, filenameType, suffix);
	}

	@Override
	public Batch generate(IGraph g) {
		return LegacyBatchReader.read(this.dir, this.getFilename(g), g);
	}

}
