package dna.metrics.richClubConnectivity;

import java.util.Collection;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.Update;

public class RCCFirstKNodesComp extends RCCFirstKNodes {
	public RCCFirstKNodesComp() {
		super("RCCFirstKNodesComp", ApplicationType.Recomputation);
	}

	@Override
	public boolean compute() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : (Collection<DirectedNode>) this.g.getNodes()) {

				int degree = n.getOutDegree();
				this.degrees.add(degree);
				if (nodesSortedByDegree.containsKey(degree)) {
					this.nodesSortedByDegree.get(degree).add(n);
				} else {
					LinkedList<DirectedNode> temp = new LinkedList<>();
					temp.add(n);
					this.nodesSortedByDegree.put(degree, temp);
				}

			}

			LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
			int size = this.degrees.size();
			for (int i = 0; i < size; i++) {
				int currentDegree = this.degrees.last();
				this.degrees.remove(currentDegree);
				temp.addAll(nodesSortedByDegree.get(currentDegree));
			}

			// First k biggest Nodes to richClub
			richClub.addAll(temp.subList(0, richClubSize));
			// the rest are maintained in Rest
			rest.addAll(temp.subList(richClubSize, temp.size()));

			for (DirectedNode n : richClub) {
				for (DirectedEdge e : n.getOutgoingEdges()) {
					if (richClub.contains(e.getDst())) {
						edgesBetweenRichClub++;
					}
				}
			}
		}
		caculateRCC();
		return true;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

}
