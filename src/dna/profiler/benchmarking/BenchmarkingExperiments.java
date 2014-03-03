package dna.profiler.benchmarking;

import java.util.ArrayList;
import java.util.EnumMap;

import org.perfidix.Benchmark;
import org.perfidix.annotation.BeforeFirstRun;
import org.perfidix.annotation.Bench;
import org.perfidix.annotation.BenchClass;
import org.perfidix.result.BenchmarkResult;

import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.datastructures.IReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.util.Rand;

@BenchClass
public class BenchmarkingExperiments {
	private static Class<? extends IDataStructure> classToBenchmark;

	private GraphDataStructure gds;
	private INodeListDatastructure nodeListToBenchmark;
	private INodeListDatastructureReadable nodeListToBenchmarkCasted;
	private IEdgeListDatastructure edgeListToBenchmark;
	private IEdgeListDatastructureReadable edgeListToBenchmarkCasted;

	private static final int repetitions = 5;
	private static int[] inputSizes = new int[] { 1000, 5000, 10000 };
	public static int operationSize = 50;
	private static int maxListSize = (int) (getMax(inputSizes)
			+ Math.ceil(operationSize / 2) + operationSize);

	INode[] nodeList = new INode[maxListSize + 2];
	IEdge[] edgeList = new IEdge[maxListSize + 2];

	Integer[] randomIDsInList;
	Integer[] randomIDsNotInList;

	Edge[] randomEdgesInList;
	Edge[] randomEdgesNotInList;

	// Variables needed in the benchmarked methods. Define them here to ease
	// allocation
	boolean res;
	Integer i;

	@SuppressWarnings("unchecked")
	public BenchmarkingExperiments(String dsClass) {
		try {
			BenchmarkingExperiments.classToBenchmark = (Class<? extends IDataStructure>) Class
					.forName(dsClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getMax(int[] list) {
		int res = 0;
		for (int in : list) {
			res = Math.max(res, in);
		}
		return res;
	}

	@BeforeFirstRun
	public void setupGeneralLists() {
		EnumMap<ListType, Class<? extends IDataStructure>> list = GraphDataStructure
				.getList(ListType.GlobalEdgeList, DArray.class,
						ListType.GlobalNodeList, DArray.class);
		GraphDataStructure gds = new GraphDataStructure(list,
				DirectedNode.class, DirectedEdge.class);

		Node node, formerNode;
		Edge edge;

		node = gds.newNodeInstance(0);
		nodeList[0] = node;

		for (int i = 0; i < maxListSize; i++) {
			formerNode = node;

			node = gds.newNodeInstance(i + 1);
			nodeList[i + 1] = node;

			edge = gds.newEdgeInstance(formerNode, node);
			edgeList[i] = edge;
		}
	}

	public static Object[][] testInput() {
		Object[][] inputSet = new Object[inputSizes.length][2];
		int counter = 0;
		for (int inputSize : inputSizes) {
			inputSet[counter] = new Object[] { classToBenchmark, inputSize };
			counter++;
		}
		return inputSet;
	}

	public void setUpGds(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {

		// Generate the set of nodes to be inserted here
		EnumMap<ListType, Class<? extends IDataStructure>> list = GraphDataStructure
				.getList(ListType.GlobalEdgeList, dsClass,
						ListType.GlobalNodeList, dsClass);
		gds = new GraphDataStructure(list, DirectedNode.class,
				DirectedEdge.class);
	}

	public void setUp(Class<? extends IDataStructure> dsClass, Integer setupSize)
			throws Exception {
		setUpGds(dsClass, setupSize);
		nodeListToBenchmark = (INodeListDatastructure) gds
				.newList(ListType.GlobalNodeList);
		edgeListToBenchmark = (IEdgeListDatastructure) gds
				.newList(ListType.GlobalEdgeList);

		int initialSize = (int) (setupSize - Math.ceil(operationSize / 2));

		for (int i = 0; i < initialSize; i++) {
			nodeListToBenchmark.add(nodeList[i]);
			edgeListToBenchmark.add(edgeList[i]);
		}

		int rand;
		IEdge e;

		randomIDsInList = new Integer[operationSize];
		randomIDsNotInList = new Integer[operationSize];
		randomEdgesInList = new Edge[operationSize];
		randomEdgesNotInList = new Edge[operationSize];

		ArrayList<Integer> tempNodesInList = new ArrayList<Integer>();
		ArrayList<Integer> tempNodesNotInList = new ArrayList<Integer>();
		ArrayList<Edge> tempEdgesInList = new ArrayList<Edge>();

		for (int i = 0; i < operationSize; i++) {
			do {
				rand = Rand.rand.nextInt(initialSize);
			} while (tempNodesInList.contains(rand));
			tempNodesInList.add(rand);

			do {
				rand = Rand.rand.nextInt(Integer.MAX_VALUE);
			} while (rand < initialSize || tempNodesNotInList.contains(rand));
			tempNodesNotInList.add(rand);

			do {
				rand = Rand.rand.nextInt(initialSize);
				e = edgeList[rand];
			} while (tempEdgesInList.contains(e));
			tempEdgesInList.add((Edge) e);

			e = gds.newEdgeInstance((Node) nodeList[i], (Node) nodeList[i]);
			randomEdgesNotInList[i] = (Edge) e;
		}

		tempNodesInList.toArray(randomIDsInList);
		tempNodesNotInList.toArray(randomIDsNotInList);
		tempEdgesInList.toArray(randomEdgesInList);

		nodeListToBenchmarkCasted = null;
		if (IReadable.class.isAssignableFrom(classToBenchmark))
			nodeListToBenchmarkCasted = (INodeListDatastructureReadable) nodeListToBenchmark;

		edgeListToBenchmarkCasted = null;
		if (edgeListToBenchmark.getClass().isAssignableFrom(IReadable.class))
			edgeListToBenchmarkCasted = (IEdgeListDatastructureReadable) edgeListToBenchmark;
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUpGds")
	public void Init(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		nodeListToBenchmark = (INodeListDatastructure) gds
				.newList(ListType.GlobalNodeList);
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = setupSize; i < (setupSize + Math.ceil(operationSize / 2)); i++) {
			nodeListToBenchmark.add(nodeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = setupSize; i < (setupSize + Math.ceil(operationSize / 2)); i++) {
			edgeListToBenchmark.add(edgeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsSuccess(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.contains(nodeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsFailure(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.contains(nodeList[setupSize + i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetSuccess_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmarkCasted == null)
			return;
		for (int i : randomIDsInList) {
			nodeListToBenchmarkCasted.get(i);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetSuccess_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmarkCasted == null)
			return;
		for (Edge e : randomEdgesInList) {
			res = edgeListToBenchmarkCasted.get(e) != null;
			if (!res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetFailure_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmarkCasted == null)
			return;
		for (int i : randomIDsNotInList) {
			res = nodeListToBenchmarkCasted.get(i) != null;
			if (!res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetFailure_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmarkCasted == null)
			return;
		for (Edge e : randomEdgesNotInList) {
			res = edgeListToBenchmarkCasted.get(e) != null;
			if (res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Random_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmarkCasted == null)
			return;
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmarkCasted.getRandom();
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Random_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmarkCasted == null)
			return;
		for (i = 0; i < operationSize; i++) {
			edgeListToBenchmarkCasted.getRandom();
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveSuccess_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = nodeListToBenchmark.remove(nodeList[randomIDsInList[i]]);
			if (!res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveFailure_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = nodeListToBenchmark.remove(nodeList[randomIDsNotInList[i]]);
			if (res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveSuccess_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = edgeListToBenchmark.remove(randomEdgesInList[i]);
			if (!res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveFailure_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = edgeListToBenchmark.remove(randomEdgesNotInList[i]);
			if (res)
				throw new RuntimeException("Misdefined benchmark");
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Size(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.size();
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Iterator(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.iterator();
		}
	}

	public static void main(String[] args) {
		BenchmarkingConf benchmarkingConf = new BenchmarkingConf();
		final Benchmark bm = new Benchmark(benchmarkingConf);
		bm.add(new BenchmarkingExperiments(args[0]));
		final BenchmarkResult res = bm.run();
		new BenchmarkingVisitor().visitBenchmark(res);
		// new TabularSummaryOutput().visitBenchmark(res);
	}
}
