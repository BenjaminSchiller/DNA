package dna.graph.datastructures.config;

import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;

public class DSConfigDirected extends DSConfig {

	public Class<? extends IDataStructure> in;
	public Class<? extends IDataStructure> out;
	public Class<? extends IDataStructure> neighbors;

	public DSConfigDirected(Class<? extends IDataStructure> ds) {
		this(ds, ds, ds);
	}

	public DSConfigDirected(Class<? extends IDataStructure> V,
			Class<? extends IDataStructure> E,
			Class<? extends IDataStructure> adj) {
		this(V, E, adj, adj, adj);
	}

	public DSConfigDirected(Class<? extends IDataStructure> V,
			Class<? extends IDataStructure> E,
			Class<? extends IDataStructure> in,
			Class<? extends IDataStructure> out,
			Class<? extends IDataStructure> neighbors) {
		super(V, E);
		this.in = in;
		this.out = out;
		this.neighbors = neighbors;
	}

	public String toString() {
		return "V: " + this.V.getSimpleName() + ", E: "
				+ this.E.getSimpleName() + ", in: " + this.in.getSimpleName()
				+ ", out: " + this.out.getSimpleName() + ", n: "
				+ this.neighbors.getSimpleName();
	}

	@Override
	public GraphDataStructure getGDS() {
		return new GraphDataStructure(GraphDataStructure.getList(
				ListType.GlobalNodeList, this.V, ListType.GlobalEdgeList,
				this.E, ListType.LocalInEdgeList, this.in,
				ListType.LocalOutEdgeList, this.out, ListType.LocalNodeList,
				this.neighbors), DirectedNode.class, DirectedEdge.class);
	}

	@Override
	public String getStimpleName(String sep) {
		return V.getSimpleName() + sep + E.getSimpleName() + sep
				+ in.getSimpleName() + sep + out.getSimpleName() + sep
				+ neighbors.getSimpleName();
	}
}
