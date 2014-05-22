package dna.graph.edges;

import dna.graph.nodes.DirectedNode;

public class DummyDirectedEdge extends DirectedEdge implements IEdgeDummy {
	private int srcIndex;
	private int dstIndex;

	public DummyDirectedEdge(DirectedNode src, DirectedNode dst) {
		super(src, dst);
	}
	
	public void setNodes(int n1, int n2) {
		this.srcIndex = n1;
		this.dstIndex = n2;
	}
	
	@Override
	public int getN1Index() {
		return getSrcIndex();
	}
	
	@Override
	public int getN2Index() {
		return getDstIndex();
	}
	
	public int getSrcIndex() {
		return srcIndex;
	}
	
	public int getDstIndex() {
		return dstIndex;
	}
	
	public int hashCode() {
		return getHashcode(srcIndex, dstIndex);
	}

}
