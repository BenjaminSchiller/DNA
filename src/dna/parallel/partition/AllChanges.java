package dna.parallel.partition;

import dna.updates.batch.Batch;

public class AllChanges {
	public Batch b;
	public Batch[] batches;

	public AllChanges(Batch b, Batch[] batches) {
		this.b = b;
		this.batches = batches;
	}

	public static AllChanges split(AllPartitions all, Batch b) {
		switch (all.partitionType) {
		case NodeCut:
			return null;
		case NonOverlapping:
			return NonOverlappingPartition.split(all, b);
		case Overlapping:
			return null;
		default:
			throw new IllegalArgumentException("unknown partition type: "
					+ all.partitionType);
		}
	}
}
