package dna.graph;

public abstract class GraphDatastructures<G extends Graph<N, E>, N extends Node<E>, E extends Edge> {

	protected Class<G> graphType;

	protected Class<N> nodeType;

	protected Class<E> edgeType;

	public GraphDatastructures(Class<G> graphType, Class<N> nodeType,
			Class<E> edgeType) {
		this.graphType = graphType;
		this.nodeType = nodeType;
		this.edgeType = edgeType;
	}

	public String toString() {
		return "GraphType: " + this.graphType + "\nNodeType: " + this.nodeType
				+ "\nEdgeType: " + this.edgeType;
	}

	public Class<G> getGraphType() {
		return graphType;
	}

	public Class<N> getNodeType() {
		return nodeType;
	}

	public Class<E> getEdgeType() {
		return edgeType;
	}

	public abstract G newGraphInstance(String name, long timestamp, int nodes,
			int edges);

	public abstract N newNodeInstance(int index);

	public abstract E newEdgeInstance(N src, N dst);

}
