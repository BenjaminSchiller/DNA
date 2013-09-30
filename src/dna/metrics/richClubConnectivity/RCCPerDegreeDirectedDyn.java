package dna.metrics.richClubConnectivity;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCPerDegreeDirectedDyn extends RCCPerDegreeDirected {

	public RCCPerDegreeDirectedDyn() {
		super("RCCPerDegreeDyn", ApplicationType.AfterUpdate);
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
		if (u instanceof NodeAddition) {
			return applyAfterNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemoval(u);
		}
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();
		this.richClubs.put(node.getOutDegree(),
				this.richClubs.get(node.getOutDegree()) + 1);
		calculateRCC();
		return true;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();

		// Current removal the deleted edge is still in the set from the source
		// Node
		int edges = 0;

		for (IElement iE : src.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getDst().getOutDegree() > srcDegree) {
				edges++;
			}

		}
		for (IElement iE : src.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getSrc().getOutDegree() > srcDegree) {
				edges++;
			}

		}

		if (dstDegree < srcDegree + 1) {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) - 1);
			this.richClubEdges.put(srcDegree + 1,
					this.richClubEdges.get(srcDegree + 1) - edges);

		} else {
			this.richClubEdges.put(srcDegree + 1,
					this.richClubEdges.get(srcDegree + 1) - (edges + 1));

		}

		this.richClubs
				.put(srcDegree + 1, this.richClubs.get(srcDegree + 1) - 1);
		if (this.richClubs.get(srcDegree + 1) == 0) {
			this.richClubs.remove(srcDegree + 1);
			this.richClubEdges.remove(srcDegree + 1);
			this.richClubCoefficienten.remove(srcDegree + 1);
		}

		if (this.richClubs.containsKey(srcDegree)) {
			this.richClubs.put(srcDegree, this.richClubs.get(srcDegree) + 1);
			this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
					+ edges);

		} else {
			this.richClubs.put(srcDegree, 1);
			this.richClubEdges.put(srcDegree, edges);
		}

		calculateRCC();
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();
		int edges = 0;

		for (IElement iE : src.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getDst().getOutDegree() >= srcDegree) {
				edges++;
			}

		}
		for (IElement iE : src.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getSrc().getOutDegree() >= srcDegree) {
				edges++;
			}

		}

		if (dstDegree < srcDegree) {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) + 1);
			this.richClubEdges.put(srcDegree - 1,
					this.richClubEdges.get(srcDegree - 1) - edges);

		} else {
			this.richClubEdges.put(srcDegree - 1,
					this.richClubEdges.get(srcDegree - 1) - (edges - 1));

		}

		this.richClubs
				.put(srcDegree - 1, this.richClubs.get(srcDegree - 1) - 1);
		if (this.richClubs.get(srcDegree - 1) == 0) {
			this.richClubs.remove(srcDegree - 1);
			this.richClubEdges.remove(srcDegree - 1);
			this.richClubCoefficienten.remove(srcDegree - 1);
		}

		if (this.richClubs.containsKey(srcDegree)) {
			this.richClubs.put(srcDegree, this.richClubs.get(srcDegree) + 1);
			this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
					+ edges);

		} else {
			this.richClubs.put(srcDegree, 1);
			this.richClubEdges.put(srcDegree, edges);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		calculateRCC();
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		this.richClubs.put(node.getOutDegree(),
				this.richClubs.get(node.getOutDegree()) - 1);
		int updateEdges = 0;

		for (IElement iE : node.getIncomingEdges()) {
			DirectedEdge ed = (DirectedEdge) iE;
			if (ed.getSrc().getOutDegree() >= node.getOutDegree()) {
				updateEdges++;
			} else {
				int temp = richClubEdges.get(ed.getSrc().getOutDegree());
				richClubEdges.put(ed.getSrc().getOutDegree(), temp - 1);
			}
		}
		for (IElement iE : node.getOutgoingEdges()) {
			DirectedEdge ed = (DirectedEdge) iE;
			if (ed.getDst().getOutDegree() >= node.getOutDegree()) {
				updateEdges++;
			} else {
				int temp = richClubEdges.get(ed.getSrc().getOutDegree());
				richClubEdges.put(ed.getSrc().getOutDegree(), temp - 1);
			}
		}
		int temp = richClubEdges.get(node.getOutDegree());
		richClubEdges.put(node.getOutDegree(), temp - updateEdges);
		calculateRCC();
		return true;
	}

}
