package dna.profiler.benchmarking;

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
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;

@BenchClass
public class BenchmarkingExperiments {
	private static Class<? extends IDataStructure> classToBenchmark;

	private GraphDataStructure gds;
	private IDataStructure nodeListToBenchmark;
	private IDataStructure edgeListToBenchmark;

	private static final int repetitions = 15;
	private static int[] inputSizes = new int[] { 1000, 5000, 10000 };
	public static int operationSize = 50;
	private static int maxListSize = (int) (getMax(inputSizes)
			+ Math.ceil(operationSize / 2) + operationSize);

	INode[] nodeList = new INode[maxListSize + 2];
	IEdge[] edgeList = new IEdge[maxListSize + 2];

	public BenchmarkingExperiments(String dsClass) {
		try {
			this.classToBenchmark = (Class<? extends IDataStructure>) Class
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

	@SuppressWarnings("unchecked")
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
		nodeListToBenchmark = gds.newList(ListType.GlobalNodeList);
		edgeListToBenchmark = gds.newList(ListType.GlobalEdgeList);

		for (int i = 0; i < (setupSize - Math.ceil(operationSize / 2)); i++) {
			nodeListToBenchmark.add(nodeList[i]);
			edgeListToBenchmark.add(edgeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUpGds")
	public void Init(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		nodeListToBenchmark = gds.newList(ListType.GlobalNodeList);
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (int i = setupSize; i < (setupSize + Math.ceil(operationSize / 2)); i++) {
			nodeListToBenchmark.add(nodeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (int i = setupSize; i < (setupSize + Math.ceil(operationSize / 2)); i++) {
			edgeListToBenchmark.add(edgeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsSuccess(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (int i = 0; i < operationSize; i++) {
			nodeListToBenchmark.contains(nodeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsFailure(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (int i = 0; i < operationSize; i++) {
			nodeListToBenchmark.contains(nodeList[setupSize + i]);
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
