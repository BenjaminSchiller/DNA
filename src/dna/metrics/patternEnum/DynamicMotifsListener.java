package dna.metrics.patternEnum;

import dna.graph.edges.IEdge;
import dna.graph.nodes.Node;

public interface DynamicMotifsListener {
	public void edgeAddedEvent(IEdge e);
	public void edgeRemovedEvent(IEdge e);
	public void nodeAddedEvent(Node n);
	public void nodeRemovedEvent(Node n);
	public void initialized();
}
