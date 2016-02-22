package dna.parallel.partitioning;

public class PartitioningFromArgs {
	public static enum PartitioningType {
		BFS, DFS, EqualSize, LPA, Random
	}

	public static Partitioning parse(PartitioningType partitioningType,
			String... args) {
		switch (partitioningType) {
		case BFS:
			return new BFSPartitioning();
		case DFS:
			return new DFSPartitioning();
		case EqualSize:
			return new EqualSizePartitioning();
		case LPA:
			return new LPAPartitioning();
		case Random:
			return new RandomPartitioning();
		default:
			throw new IllegalArgumentException("unknown partitioning type: "
					+ partitioningType);
		}
	}
}
