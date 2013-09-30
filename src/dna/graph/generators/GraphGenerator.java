package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class GraphGenerator extends ParameterList implements
		IGraphGenerator {
	protected long timestampInit;
	protected int nodesInit;
	protected int edgesInit;

	protected GraphDataStructure gds;

	public GraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit) {
		super(name, params);

		this.timestampInit = timestampInit;
		this.nodesInit = nodesInit;
		this.edgesInit = edgesInit;

		this.gds = gds;

		if (gds != null && !gds.isReadable()) {
			throw new RuntimeException(
					"Cannot generate a graph if any datastructure is not readable");
		}

	}

	public Graph newGraphInstance() {
		if (!this.canGenerateNodeType(gds.getNodeType())) {
			throw new RuntimeException(
					"This generator can not be run with a graph data structure containing a node of type "
							+ this.gds.getNodeType());
		}

		return this.gds.newGraphInstance(this.getName(), this.timestampInit,
				this.nodesInit, this.edgesInit);
	}

	public GraphDataStructure getGraphDataStructure() {
		return this.gds;
	}

	@Override
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return true;
	}

	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		return true;
	}

	public static String buildName(String name, GraphDataStructure gds) {
		if (gds.createsDirected()) {
			return "Directed" + name;
		}
		if (gds.createsUndirected()) {
			return "Undirected" + name;
		}
		return name;
	}

}
