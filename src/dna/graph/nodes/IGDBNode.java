package dna.graph.nodes;

import dna.graph.IGraph;

public interface IGDBNode<N> {

	public abstract void setGraph(IGraph graph);

	public abstract IGraph getGraph();

	public abstract void setGDBNode(N node);

	public abstract N getGDBNode();

}