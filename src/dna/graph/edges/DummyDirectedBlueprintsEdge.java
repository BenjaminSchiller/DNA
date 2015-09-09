package dna.graph.edges;

import dna.graph.nodes.DirectedBlueprintsNode;

public class DummyDirectedBlueprintsEdge extends DirectedBlueprintsEdge implements IEdgeDummy {

	public DummyDirectedBlueprintsEdge(DirectedBlueprintsNode src, DirectedBlueprintsNode dst) {
		super(src, dst);
	}

	public void setNodes(int n1, int n2) {
		this.n1.setIndex(n1);
		this.n2.setIndex(n2);
	}
}
