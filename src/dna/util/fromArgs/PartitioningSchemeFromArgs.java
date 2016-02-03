package dna.util.fromArgs;

import dna.metrics.parallelization.partitioning.schemes.BFSPartitioning;
import dna.metrics.parallelization.partitioning.schemes.DFSPartitioning;
import dna.metrics.parallelization.partitioning.schemes.EqualSizePartitioning;
import dna.metrics.parallelization.partitioning.schemes.LPAPartitioning;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme.PartitioningType;
import dna.metrics.parallelization.partitioning.schemes.RandomPartitioning;

public class PartitioningSchemeFromArgs {
	public static enum PartitioningSchemeType {
		BFS, DFS, EQUAL_SIZE, LPA, RANDOM
	}

	public static PartitioningScheme parse(
			PartitioningSchemeType partitioningSchemeType,
			PartitioningType partitioningType, int partitionCount,
			String... args) {
		switch (partitioningSchemeType) {
		case BFS:
			return new BFSPartitioning(partitioningType, partitionCount);
		case DFS:
			return new DFSPartitioning(partitioningType, partitionCount);
		case EQUAL_SIZE:
			return new EqualSizePartitioning(partitioningType, partitionCount);
		case LPA:
			return new LPAPartitioning(partitioningType, partitionCount);
		case RANDOM:
			return new RandomPartitioning(partitioningType, partitionCount);
		default:
			throw new IllegalArgumentException(
					"unknown partitioning scheme type: "
							+ partitioningSchemeType);
		}
	}
}
