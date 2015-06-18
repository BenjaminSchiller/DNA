package dna.graph.datastructures.config;

import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;

public class DSConfigUndirected extends DSConfig {

	public Class<? extends IDataStructure> adj;

	public DSConfigUndirected(Class<? extends IDataStructure> ds) {
		this(ds, ds, ds);
	}

	public DSConfigUndirected(Class<? extends IDataStructure> V,
			Class<? extends IDataStructure> E,
			Class<? extends IDataStructure> adj) {
		super(V, E);
		this.adj = adj;
	}

	public String toString() {
		return "V: " + this.V.getSimpleName() + ", E: "
				+ this.E.getSimpleName() + ", adj: " + this.adj.getSimpleName();
	}

	@Override
	public GraphDataStructure getGDS() {
		return new GraphDataStructure(GraphDataStructure.getList(
				ListType.GlobalNodeList, this.V, ListType.GlobalEdgeList,
				this.E, ListType.LocalEdgeList, this.adj),
				UndirectedNode.class, UndirectedEdge.class);
	}

	@Override
	public String getStimpleName(String sep) {
		return V.getSimpleName() + sep + E.getSimpleName() + sep
				+ adj.getSimpleName();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DSConfigUndirected))
			return false;
		DSConfigUndirected cfg = (DSConfigUndirected) obj;
		return this.V.equals(cfg.V) && this.E.equals(cfg.E)
				&& this.adj.equals(cfg.adj);
	}
}
