package dna.graph.datastructures.config;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;

public abstract class DSConfig {

	public Class<? extends IDataStructure> V;
	public Class<? extends IDataStructure> E;

	public DSConfig(Class<? extends IDataStructure> V,
			Class<? extends IDataStructure> E) {
		this.V = V;
		this.E = E;
	}

	public abstract GraphDataStructure getGDS();

	public abstract String getStimpleName(String sep);

	public static DSConfig convert(GraphDataStructure gds) {
		if (gds.createsDirected()) {
			Class<? extends IDataStructure> V, E, in, out, neighbors;
			V = gds.getListClass(ListType.GlobalNodeList);
			E = gds.getListClass(ListType.GlobalEdgeList);
			in = gds.getListClass(ListType.LocalInEdgeList);
			out = gds.getListClass(ListType.LocalOutEdgeList);
			neighbors = gds.getListClass(ListType.LocalNodeList);
			return new DSConfigDirected(V, E, in, out, neighbors);
		} else {
			Class<? extends IDataStructure> V, E, adj;
			V = gds.getListClass(ListType.GlobalNodeList);
			E = gds.getListClass(ListType.GlobalEdgeList);
			adj = gds.getListClass(ListType.LocalEdgeList);
			return new DSConfigUndirected(V, E, adj);
		}
	}
}
