package dna.metrics.richClubConnectivity;

import java.util.HashSet;
import java.util.Set;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCPerDegreeDyn extends RCCPerDegree {

	public RCCPerDegreeDyn() {
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
		Set<DirectedNode> richClub = this.richClubs.get(node.getOutDegree());
		richClub.add(node);
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
		this.richClubs.get(srcDegree).remove(src);

		if (this.richClubs.containsKey(srcDegree)) {
			int updateEdgesNew = 0;
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (n.getSrc() != dst && n.getSrc().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (n.getDst() != dst && n.getDst().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}

			int temp = this.richClubEdges.get(srcDegree) + updateEdgesNew;
			this.richClubEdges.put(srcDegree, temp);
			temp = this.richClubEdges.get(srcDegree + 1) - updateEdgesNew;
			this.richClubEdges.put(srcDegree - 1, temp);
		} else {

			int updateEdgesNew = 0;
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (n.getSrc() != dst && n.getSrc().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (n.getDst() != dst && n.getDst().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			Set<DirectedNode> temp = new HashSet<DirectedNode>();
			temp.add(src);
			this.richClubs.put(srcDegree, temp);
			this.richClubEdges.put(srcDegree, updateEdgesNew);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		if (srcDegree > dstDegree) {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) - 1);
		} else {
			this.richClubEdges.put(srcDegree,
					this.richClubEdges.get(srcDegree) - 1);
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

		// new src Degree exists in richclub
		if (this.richClubs.containsKey(srcDegree)) {
			int updateEdgesNew = 0;

			// find all edges that connect the node to nodes with higher or the
			// same degree
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (n.getSrc() != dst && n.getSrc().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (n.getDst() != dst && n.getDst().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}

			// update the edge count for the richClub change
			int temp = this.richClubEdges.get(srcDegree) + updateEdgesNew;
			this.richClubEdges.put(srcDegree, temp);
			temp = this.richClubEdges.get(srcDegree - 1) - updateEdgesNew;
			this.richClubEdges.put(srcDegree - 1, temp);

		} else {

			int updateEdgesNew = 0;
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (n.getSrc() != dst && n.getSrc().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (n.getDst() != dst && n.getDst().getOutDegree() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			Set<DirectedNode> temp = new HashSet<DirectedNode>();
			temp.add(src);
			this.richClubs.put(srcDegree, temp);
			this.richClubEdges.put(srcDegree, updateEdgesNew);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		if (srcDegree >= dstDegree) {
			int temp = this.richClubEdges.get(srcDegree) + 1;
			this.richClubEdges.put(srcDegree, temp);
		} else {
			int temp = this.richClubEdges.get(dstDegree) + 1;
			this.richClubEdges.put(dstDegree, temp);
		}

		// update the rc membership
		this.richClubs.get(srcDegree).add(src);
		this.richClubs.get(srcDegree - 1).remove(src);
		calculateRCC();
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		Set<DirectedNode> richClub = this.richClubs.get(node.getOutDegree());
		richClub.remove(node);
		int updateEdges = 0;

		for (DirectedEdge ed : node.getIncomingEdges()) {
			if (ed.getSrc().getOutDegree() >= node.getOutDegree()) {
				updateEdges++;
			} else {
				int temp = richClubEdges.get(ed.getSrc().getOutDegree());
				richClubEdges.put(ed.getSrc().getOutDegree(), temp - 1);
			}
		}
		for (DirectedEdge ed : node.getOutgoingEdges()) {
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
