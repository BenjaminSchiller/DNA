package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityForOneDegree;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedRichClubConnectivityForOneDegreeU extends
		UndirectedRichClubConnectivityForOneDegree {

	public UndirectedRichClubConnectivityForOneDegreeU(int minDegree) {
		super("RCCForOneDegreeDyn", ApplicationType.AfterUpdate, minDegree);
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

	/**
	 * Check whether the Edge Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (this.richClub.contains(node1) && this.richClub.contains(node2)
				&& node1.getDegree() >= this.minDegree
				&& node2.getDegree() >= this.minDegree) {
			this.richClubEdges -= 2;
		} else {
			if (this.richClub.contains(node1) && this.richClub.contains(node2)) {
				this.richClubEdges -= 2;
			}
			checkNodeREM(node1);
			checkNodeREM(node2);
		}
		return true;
	}

	/**
	 * Check whether the Degree changes at node has influence on RichClub
	 * 
	 * @param node
	 * @return void
	 */
	private void checkNodeREM(UndirectedNode node) {
		if (node.getDegree() < this.minDegree && this.richClub.contains(node)) {
			this.richClub.remove(node);
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode n = edge.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					this.richClubEdges -= 2;
				}
			}
		}
	}

	/**
	 * Check whether the Edge Addition has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (this.richClub.contains(node1) && this.richClub.contains(node2)) {
			this.richClubEdges += 2;
		} else {
			checkNodeADD(node1);
			checkNodeADD(node2);
		}
		return true;

	}

	/**
	 * Check whether the the Degree changes at node has influence on RichClub
	 * 
	 * @param node
	 * @return void
	 */
	private void checkNodeADD(UndirectedNode node) {
		if (node.getDegree() >= this.minDegree && !this.richClub.contains(node)) {
			this.richClub.add(node);
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdge;
				UndirectedNode n = ed.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					this.richClubEdges += 2;
				}
			}
		}
	}

	/**
	 * Check whether the Node Removal has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		if (this.richClub.contains(node)) {
			this.richClub.remove(node);
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode n = edge.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					checkNodeREM(n);
					this.richClubEdges -= 2;
				}
			}
		} else {
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode n = edge.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					checkNodeREM(n);
				}
			}
		}
		return true;
	}

	/**
	 * Check whether the Node Addition has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterNodeAddition(Update u) {
		return true;
	}

}
