package dna.graph.edges;

import dna.graph.nodes.UndirectedNode;

public class DummyUndirectedEdge extends UndirectedEdge implements IEdgeDummy {

	public DummyUndirectedEdge(UndirectedNode src, UndirectedNode dst) {
		super(src, dst);
	}

	public void setNodes(int n1, int n2) {
		this.n1.setIndex(Math.min(n1, n2));
		this.n2.setIndex(Math.max(n1, n2));
	}
}
