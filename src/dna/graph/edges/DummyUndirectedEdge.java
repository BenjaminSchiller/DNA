package dna.graph.edges;

import dna.graph.nodes.UndirectedNode;

public class DummyUndirectedEdge extends UndirectedEdge implements IEdgeDummy {
	private int srcIndex;
	private int dstIndex;

	public DummyUndirectedEdge(UndirectedNode src, UndirectedNode dst) {
		super(src, dst);
	}
	
	public void setNodes(int n1, int n2) {
		this.srcIndex = Math.min(n1, n2);
		this.dstIndex = Math.max(n1, n2);
	}
	
	public int getNode1Index() {
		return srcIndex;
	}
	
	public int getNode2Index() {
		return dstIndex;
	}
	
	public int hashCode() {
		return getHashcode(srcIndex, dstIndex);
	}

}
