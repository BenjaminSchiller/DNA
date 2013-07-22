package dna.metrics.clusterCoefficient;

import dna.updates.Batch;
import dna.updates.Update;

/**
 * 
 * clustering coefficient for closed triangles (recomputation version). as in an
 * undirected graph, we define a closed triangles if an (undirected) edge exists
 * between all three nodes which are part of the triangle, i.e., (a<->b, a<->c,
 * b<->c). in directed networks, we consider a closed triangle to exist in case
 * the edge in both directions exists between both neighbors of the origin
 * (a->b, a<-b, a->c, a<-c, b->c, b<-c), i.e., the open triangles (a,b,c) and
 * (a,c,b) exists.
 * 
 * Therefore, this metric is applicable to directed and undirected networks.
 * 
 * @author benni
 * 
 */
@SuppressWarnings("rawtypes")
public class ClosedTriangleClusteringCoefficientRecomp extends
		ClosedTriangleClusteringCoefficient {

	public ClosedTriangleClusteringCoefficientRecomp() {
		super("closedTriangleClusteringCoefficientRecomp",
				ApplicationType.Recomputation);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
