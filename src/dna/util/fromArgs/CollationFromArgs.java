package dna.util.fromArgs;

import dna.metrics.parallelization.collation.Collation;
import dna.metrics.parallelization.collation.clustering.UndirectedClusteringCoefficientCollationNonOverlapping;
import dna.metrics.parallelization.collation.clustering.UndirectedClusteringCoefficientCollationOverlapping;

public class CollationFromArgs {
	public static enum CollationType {
		UndirectedClusteringCoefficientCollationNonOverlapping, UndirectedClusteringCoefficientCollationOverlapping
	}

	@SuppressWarnings("rawtypes")
	public static Collation parse(CollationType collationType, String... args) {
		switch (collationType) {
		case UndirectedClusteringCoefficientCollationNonOverlapping:
			return new UndirectedClusteringCoefficientCollationNonOverlapping();
		case UndirectedClusteringCoefficientCollationOverlapping:
			return new UndirectedClusteringCoefficientCollationOverlapping();
		default:
			throw new IllegalArgumentException("unknown collation type: "
					+ collationType);
		}
	}
}
