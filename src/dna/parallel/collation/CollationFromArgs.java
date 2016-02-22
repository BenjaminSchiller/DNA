package dna.parallel.collation;

import dna.parallel.collation.clustering.UndirectedClusteringCoefficientNodeCutCollation;
import dna.parallel.collation.clustering.UndirectedClusteringCoefficientNonOverlappingCollation;
import dna.parallel.collation.clustering.UndirectedClusteringCoefficientOverlappingCollation;

public class CollationFromArgs {
	public static enum CollationType {
		UndirectedClusteringCoefficientNodeCut, UndirectedClusteringCoefficientNonOverlapping, UndirectedClusteringCoefficientOverlapping
	}

	@SuppressWarnings("rawtypes")
	public static Collation parse(CollationType collationType, String dir,
			int partitionCount, int run, String... args) {
		switch (collationType) {
		case UndirectedClusteringCoefficientNodeCut:
			return new UndirectedClusteringCoefficientNodeCutCollation(dir,
					partitionCount, run);
		case UndirectedClusteringCoefficientNonOverlapping:
			return new UndirectedClusteringCoefficientNonOverlappingCollation(
					dir, partitionCount, run);
		case UndirectedClusteringCoefficientOverlapping:
			return new UndirectedClusteringCoefficientOverlappingCollation(dir,
					partitionCount, run);
		default:
			throw new IllegalArgumentException("invalid collation type: "
					+ collationType);
		}
	}
}
