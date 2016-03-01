package dna.parallel.collation;

import dna.parallel.collation.clustering.UndirectedClusteringCoefficientNodeCutCollation;
import dna.parallel.collation.clustering.UndirectedClusteringCoefficientNonOverlappingCollation;
import dna.parallel.collation.clustering.UndirectedClusteringCoefficientOverlappingCollation;
import dna.parallel.util.Sleeper;

public class CollationFromArgs {
	public static enum CollationType {
		UndirectedClusteringCoefficientNodeCut, UndirectedClusteringCoefficientNonOverlapping, UndirectedClusteringCoefficientOverlapping
	}

	@SuppressWarnings("rawtypes")
	public static Collation parse(CollationType collationType, String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper,
			String... args) {
		switch (collationType) {
		case UndirectedClusteringCoefficientNodeCut:
			return new UndirectedClusteringCoefficientNodeCutCollation(auxDir,
					inputDir, partitionCount, run, sleeper);
		case UndirectedClusteringCoefficientNonOverlapping:
			return new UndirectedClusteringCoefficientNonOverlappingCollation(
					auxDir, inputDir, partitionCount, run, sleeper);
		case UndirectedClusteringCoefficientOverlapping:
			return new UndirectedClusteringCoefficientOverlappingCollation(
					auxDir, inputDir, partitionCount, run, sleeper);
		default:
			throw new IllegalArgumentException("invalid collation type: "
					+ collationType);
		}
	}
}
