package dna.graph.edges;

import dna.graph.nodes.DirectedNode;

public class DummyDirectedEdge extends DirectedEdge implements IEdgeDummy {

	public DummyDirectedEdge(DirectedNode src, DirectedNode dst) {
		super(src, dst);
	}

	public void setNodes(int n1, int n2) {
		this.n1.setIndex(n1);
		this.n2.setIndex(n2);
	}
}
