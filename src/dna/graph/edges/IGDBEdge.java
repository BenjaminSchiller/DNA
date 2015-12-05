package dna.graph.edges;

import dna.graph.IGraph;

public interface IGDBEdge<E> {

	public abstract void setGraph(IGraph graph);

	public abstract IGraph getGraph();
	
	public abstract void setGDBEdgeId(Object gdbEdgeId);

	public abstract Object getGDBEdgeId();

	public abstract E getGDBEdge();

}