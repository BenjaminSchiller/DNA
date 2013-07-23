package dna.metrics.richClubConnectivity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.Update;

public class RCCPerDegreeComp extends RCCPerDegree {
	public RCCPerDegreeComp() {
		super("RCCPerDegreeComp", ApplicationType.Recomputation);
	}

	@Override
	public boolean compute() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : (Collection<DirectedNode>) this.g.getNodes()) {
				int degree = n.getOutDegree();
				this.highestDegree = Math.max(highestDegree, degree);
				if (richClubs.containsKey(degree)) {
					this.richClubs.get(degree).add(n);
				} else {
					Set<DirectedNode> temp = new HashSet<DirectedNode>();
					temp.add(n);
					this.richClubs.put(degree, temp);
				}
			}

			HashSet<DirectedNode> currentrichclub = new HashSet<DirectedNode>();
			for (int currentDegree : this.richClubs.keySet()) {
				int edges = 0;
				for (DirectedNode n : richClubs.get(currentDegree)) {

					for (DirectedEdge ed : n.getOutgoingEdges()) {
						if (ed.getDst().getOutDegree() >= currentDegree) {
							edges++;
						}
					}
					for (DirectedEdge ed : n.getIncomingEdges()) {
						if (ed.getSrc().getOutDegree() >= currentDegree) {
							edges++;
						}
					}
				}
				richClubEdges.put(currentDegree, edges);
				currentrichclub.addAll(richClubs.get(currentDegree));
			}

			calculateRCC();

			return true;
		}
		return false;
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
	protected void init_() {
		// TODO Auto-generated method stub

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

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}
}
