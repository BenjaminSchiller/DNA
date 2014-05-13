package dna.updates.generators.util;

import dna.graph.Graph;
import dna.io.LegacyBatchReader;
import dna.updates.batch.Batch;

public class LegacyReadableFilesBatchGenerator extends
		ReadableFilesBatchGenerator {

	public LegacyReadableFilesBatchGenerator(String dir, String prefix,
			FilenameIndexType filenameType, String suffix) {
		super(dir, prefix, filenameType, suffix);
	}

	@Override
	public Batch generate(Graph g) {
		return LegacyBatchReader.read(this.dir, this.getFilename(g), g);
	}

}
