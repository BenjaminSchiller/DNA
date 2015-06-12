package dna.profiler;

import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.count.OperationCounts;
import dna.graph.datastructures.count.OperationCountsDirected;
import dna.graph.datastructures.count.OperationCountsUndirected;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Log;

public class Counting {
	public static GraphDataStructure gds;
	public static OperationCounts oc;

	public static void init(GraphDataStructure gds) {
		Counting.gds = gds;
		initOC();
	}

	protected static void initOC() {
		if (gds.isEdgeType(DirectedEdge.class)) {
			oc = new OperationCountsDirected();
		} else if (gds.isEdgeType(UndirectedEdge.class)) {
			oc = new OperationCountsUndirected();
		} else {
			Log.error("cannot initiate Counting for edge type "
					+ gds.getEdgeType());
		}
	}

	public static void setSizes(Graph g) {
		Counting.oc.setSizes(g);
	}

	public static OperationCounts graphGeneration;
	public static OperationCounts metricInit;

	public static LinkedList<OperationCounts> batchGeneration;
	public static LinkedList<OperationCounts> batchApplication;

	public static void startRun() {
		graphGeneration = null;
		metricInit = null;
		batchGeneration = new LinkedList<OperationCounts>();
		batchApplication = new LinkedList<OperationCounts>();
		initOC();
	}

	public static void endGraphGeneration(Graph g) {
		graphGeneration = oc;
		setSizes(g);
		// System.out.println("endGraphGeneration");
		// System.out.println(oc);
		initOC();
	}

	public static void endMetricInit(Graph g) {
		metricInit = oc;
		setSizes(g);
		// System.out.println("endMetricInit");
		// System.out.println(oc);
		initOC();
	}

	public static void endBatchGeneration(Graph g) {
		batchGeneration.add(oc);
		setSizes(g);
		// System.out.println("endBatchGeneration nr: " +
		// batchGeneration.size());
		// System.out.println(oc);
		initOC();
	}

	public static void endBatchApplication(Graph g) {
		batchApplication.add(oc);
		setSizes(g);
		// System.out
		// .println("endBatchApplication nr: " + batchApplication.size());
		// System.out.println(oc);
		initOC();
	}
}
