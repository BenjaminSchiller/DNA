package dna.graph;

public class GraphDatastructures<EdgeType extends Edge> {

	protected Class<? extends Graph<? extends Node<EdgeType>, EdgeType>> graphType;

	protected Class<? extends Node<EdgeType>> nodeType;

	protected Class<EdgeType> edgeType;

	public GraphDatastructures(
			Class<? extends Graph<? extends Node<EdgeType>, EdgeType>> graphType,
			Class<? extends Node<EdgeType>> nodeType, Class<EdgeType> edgeType) {
		this.graphType = graphType;
		this.nodeType = nodeType;
		this.edgeType = edgeType;
	}

	public String toString() {
		return "GraphType: " + this.graphType + "\nNodeType: " + this.nodeType
				+ "\nEdgeType: " + this.edgeType;
	}

	public Class<? extends Graph<? extends Node<EdgeType>, EdgeType>> getGraphType() {
		return graphType;
	}

	public Class<? extends Node<EdgeType>> getNodeType() {
		return nodeType;
	}

	public Class<EdgeType> getEdgeType() {
		return edgeType;
	}

}
