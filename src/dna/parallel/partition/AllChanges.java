package dna.parallel.partition;

import dna.parallel.auxData.AuxData;
import dna.parallel.auxData.SeparatedAuxData;
import dna.parallel.auxData.OverlappingAuxData;
import dna.parallel.nodeAssignment.NodeAssignment;
import dna.updates.batch.Batch;

public class AllChanges {
	public Batch b;
	public Batch[] batches;
	@SuppressWarnings("rawtypes")
	public AuxData auxAdd;
	@SuppressWarnings("rawtypes")
	public AuxData auxRemove;

	@SuppressWarnings("rawtypes")
	public AllChanges(Batch b, Batch[] batches, AuxData auxAdd,
			AuxData auxRemove) {
		this.b = b;
		this.batches = batches;
		this.auxAdd = auxAdd;
		this.auxRemove = auxRemove;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static AllChanges split(AllPartitions all, Batch b, AuxData aux,
			NodeAssignment nodeAssignment) {
		switch (all.partitionType) {
		case NodeCut:
			return null;
		case Separated:
			return SeparatedPartition
					.split((AllPartitions<SeparatedPartition, SeparatedAuxData>) all,
							b, nodeAssignment);
		case Overlapping:
			return OverlappingPartition
					.split((AllPartitions<OverlappingPartition, OverlappingAuxData>) all,
							b, nodeAssignment);
		default:
			throw new IllegalArgumentException("unknown partition type: "
					+ all.partitionType);
		}
	}

	public String getBatchesSummary() {
		int na = 0, nr = 0, nw = 0, ea = 0, er = 0, ew = 0;
		for (Batch b : this.batches) {
			na += b.getNodeAdditionsCount();
			nr += b.getNodeRemovalsCount();
			nw += b.getNodeWeightsCount();
			ea += b.getEdgeAdditionsCount();
			er += b.getEdgeRemovalsCount();
			ew += b.getEdgeWeightsCount();
		}
		return "(" + na + "," + nr + "," + nw + "/" + ea + "," + er + "," + ew
				+ ")";
	}

	public String toString() {
		return this.b.toString() + " ==> " + this.getBatchesSummary();
	}
}
