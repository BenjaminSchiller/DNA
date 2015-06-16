package dna.graph.datastructures.count;

import java.util.LinkedList;
import java.util.ListIterator;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.count.OperationCount.AggregationType;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Config;
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

	protected static boolean enabled = false;

	public static void enable() {
		enabled = true;
	}

	public static void disable() {
		enabled = false;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static OperationCounts graphGeneration;
	public static OperationCounts metricInit;

	public static LinkedList<OperationCounts> batchGeneration;
	public static LinkedList<OperationCounts> batchApplication;

	public static final String suffix = Config.get("SUFFIX_COUNTING");

	public static final String graphGenerationFilename = Config
			.get("COUNTING_GRAPH_GENERATION") + suffix;
	public static final String metricInitFilename = Config
			.get("COUNTING_METRIC_INIT") + suffix;
	public static final String batchGenerationFilename = Config
			.get("COUNTING_BATCH_GENERATION") + suffix;
	public static final String batchApplicationFilename = Config
			.get("COUNTING_BATCH_APPLICATION") + suffix;

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
		initOC();
	}

	public static void endMetricInit(Graph g) {
		metricInit = oc;
		setSizes(g);
		initOC();
	}

	public static void endBatchGeneration(Graph g) {
		batchGeneration.add(oc);
		setSizes(g);
		initOC();
	}

	public static void endBatchApplication(Graph g) {
		batchApplication.add(oc);
		setSizes(g);
		initOC();
	}

	public static OperationCounts[] getLastRounds(int rounds,
			LinkedList<OperationCounts> list) {
		if (list.size() < rounds) {
			throw new IllegalArgumentException("cannot get last " + rounds
					+ " rounds from only " + list.size() + " rounds in list");
		}
		OperationCounts[] last = new OperationCounts[rounds];
		ListIterator<OperationCounts> li = list.listIterator(list.size());
		int index = 0;
		while (index < rounds) {
			last[index++] = li.previous();
		}
		return last;
	}

	public static OperationCounts[] getLastRounds(int rounds) {
		if (batchApplication.size() < rounds) {
			throw new IllegalArgumentException("cannot get last " + rounds
					+ " rounds from only " + batchApplication.size()
					+ " rounds in list");
		}
		OperationCounts[] last = new OperationCounts[rounds * 2];
		ListIterator<OperationCounts> li1 = batchGeneration
				.listIterator(batchGeneration.size());
		ListIterator<OperationCounts> li2 = batchApplication
				.listIterator(batchApplication.size());
		int index = last.length - 1;
		while (index > 0) {
			last[index--] = li2.previous();
			last[index--] = li1.previous();
		}
		return last;
	}

	public static OperationCounts addLastRounds(int rounds, AggregationType at) {
		OperationCounts[] ocs = getLastRounds(rounds);
		return ocs[0].add(at, ocs);
	}
}
