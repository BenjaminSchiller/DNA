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
import dna.graph.tests.GlobalTestParameters;
import dna.util.Config;

@BenchClass
public class BenchmarkingExperiments {
	IDataStructure nodeListToBenchmark;
	IDataStructure edgeListToBenchmark;
	
	private static final int repetitions = 10;
	private static int[] inputSizes = new int[] { 500, 1000, 5000, 10000, 20000, 50000 };
	public static int operationSize = 50;
	private static int maxListSize = getMax(inputSizes) + operationSize + 2;
	
	INode[] nodeList = new INode[maxListSize + 2];
	IEdge[] edgeList = new IEdge[maxListSize + 2];
	
	public static int getMax(int[] list) {
		int res = 0;
		for ( int in: list) {
			res = Math.max(res, in);
		}
		return res;
	}
	
	@BeforeFirstRun
	public void setupGeneralLists() {
		Config.overwrite("GNUPLOT_PATH", "C:\\Program Files (x86)\\Cygwin\\bin\\gnuplot.exe");
		
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
			nodeList[i+1] = node;

			edge = gds.newEdgeInstance(formerNode, node);
			edgeList[i] = edge;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object[][] testInput() {
		Object[][] inputSet = new Object[GlobalTestParameters.dataStructures.length
				* inputSizes.length][2];
		int counter = 0;
		for (Class<? extends IDataStructure> dsClass : GlobalTestParameters.dataStructures) {
			for (int inputSize : inputSizes) {
				inputSet[counter] = new Object[] { dsClass, inputSize };
				counter++;
			}
		}
		return inputSet;
	}

	public void setUp(Class<? extends IDataStructure> dsClass, Integer setupSize)
			throws Exception {
		// Generate the set of nodes to be inserted here
		EnumMap<ListType, Class<? extends IDataStructure>> list = GraphDataStructure
				.getList(ListType.GlobalEdgeList, dsClass,
						ListType.GlobalNodeList, dsClass);
		GraphDataStructure gds = new GraphDataStructure(list,
				DirectedNode.class, DirectedEdge.class);

		nodeListToBenchmark = gds.newList(ListType.GlobalNodeList);
		edgeListToBenchmark = gds.newList(ListType.GlobalEdgeList);
		
		for ( int i = 0; i < setupSize; i++) {
			nodeListToBenchmark.add(nodeList[i]);
			edgeListToBenchmark.add(edgeList[i]);
		}
	}

	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Node(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		for ( int i = setupSize; i < (setupSize + operationSize); i++) {
			nodeListToBenchmark.add(nodeList[i]);
		}
	}
	
	@Bench(runs = repetitions, dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Edge(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		for ( int i = setupSize; i < (setupSize + operationSize); i++) {
			edgeListToBenchmark.add(edgeList[i]);
		}
	}	

	public static void main(String[] args) {
		final Benchmark bm = new Benchmark(new BenchmarkingConf());
		bm.add(BenchmarkingExperiments.class);
		final BenchmarkResult res = bm.run();
		new BenchmarkingVisitor().visitBenchmark(res);
		// new TabularSummaryOutput().visitBenchmark(res);
	}
}
