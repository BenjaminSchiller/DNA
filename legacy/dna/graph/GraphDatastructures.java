package dna.graph;

import dna.io.BatchReader;
import dna.io.BatchWriter;

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
		return this.graphType.getSimpleName() + " / "
				+ this.nodeType.getSimpleName() + " / "
				+ this.edgeType.getSimpleName();
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

	public abstract N newNodeInstance(int index, double weight);

	public abstract N newNodeInstance(String str);

	public abstract N[] newNodeArray(int length);

	public abstract E newEdgeInstance(N src, N dst);

	public abstract E newEdgeInstance(N src, N dst, double weight);

	public abstract E newEdgeInstance(String str, G graph);

	public BatchReader<G, N, E> getBatchReader() {
		return new BatchReader<G, N, E>(this);
	}

	public BatchWriter<G, N, E> getBatchWriter() {
		return new BatchWriter<G, N, E>();
	}

}
