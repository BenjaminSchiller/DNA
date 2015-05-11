package dna.profiler;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.count.OperationCounts;
import dna.graph.datastructures.count.OperationCountsDirected;
import dna.graph.datastructures.count.OperationCountsUndirected;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Log;

public class Counting {
	public static OperationCounts oc;

	public static void init(GraphDataStructure gds) {
		if (gds.isEdgeType(DirectedEdge.class)) {
			oc = new OperationCountsDirected();
		} else if (gds.isEdgeType(UndirectedEdge.class)) {
			oc = new OperationCountsUndirected();
		} else {
			Log.error("cannot initiate Counting for edge type "
					+ gds.getEdgeType());
		}
	}
}
