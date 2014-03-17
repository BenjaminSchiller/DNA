package dna.profiler.benchmarking;

import java.util.ArrayList;
import java.util.EnumMap;

import org.perfidix.Benchmark;
import org.perfidix.annotation.BeforeFirstRun;
import org.perfidix.annotation.Bench;
import org.perfidix.annotation.BenchClass;
import org.perfidix.result.BenchmarkResult;

import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DEmpty;
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
import dna.util.Config;
import dna.util.Rand;

@BenchClass
public class BenchmarkingExperiments {
	private static Class<? extends IDataStructure> classToBenchmark;

	private GraphDataStructure gds;
	private INodeListDatastructure nodeListToBenchmark;
	private INodeListDatastructureReadable nodeListToBenchmarkCasted;
	private IEdgeListDatastructure edgeListToBenchmark;
	private IEdgeListDatastructureReadable edgeListToBenchmarkCasted;

	private static int[] inputSizes;
	private int operationSize;
	private int maxListSize;

	INode[] nodeList;
	IEdge[] edgeList;

	Integer[] randomIDsInList;
	Integer[] randomIDsNotInList;
	INode[] randomNodesNotInList;

	Edge[] randomEdgesInList;
	Edge[] randomEdgesNotInList;

	// Variables needed in the benchmarked methods. Define them here to ease
	// allocation
	boolean res;
	Integer i;

	@SuppressWarnings("unchecked")
	public BenchmarkingExperiments(String dsClass, BenchmarkingConf benchmarkingConf) {
		try {
			BenchmarkingExperiments.classToBenchmark = (Class<? extends IDataStructure>) Class
					.forName(dsClass);
			BenchmarkingExperiments.inputSizes = benchmarkingConf.getInputSizes();
			this.operationSize = benchmarkingConf.getOperationSize();
			this.maxListSize = (int) (getMax(inputSizes)
					+ Math.ceil(operationSize / 2) + operationSize);
			nodeList = new INode[maxListSize + 2];
			edgeList = new IEdge[maxListSize + 2];			
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
		Config.overwrite("GRAPHDATASTRUCTURE_OVERRIDE_CHECKS", "true");

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

	private void initForNodeList(Class<? extends IDataStructure> dsClass,
			int initialSize) {
		if (nodeListToBenchmark == null
				|| nodeListToBenchmark.size() != initialSize) {
			nodeListToBenchmark = (INodeListDatastructure) gds
					.newList(ListType.GlobalNodeList);
			for (int i = 0; i < initialSize; i++) {
				nodeListToBenchmark.add(nodeList[i]);
			}
		}

		int rand;
		INode n;

		randomIDsInList = new Integer[operationSize];
		randomIDsNotInList = new Integer[operationSize];
		randomNodesNotInList = new INode[operationSize];

		ArrayList<Integer> tempNodesInList = new ArrayList<Integer>();
		ArrayList<Integer> tempNodesNotInList = new ArrayList<Integer>();

		for (int i = 0; i < operationSize; i++) {
			do {
				rand = Rand.rand.nextInt(initialSize);
			} while (tempNodesInList.contains(rand));
			tempNodesInList.add(rand);

			do {
				rand = Rand.rand.nextInt(Integer.MAX_VALUE);
			} while (rand < initialSize || tempNodesNotInList.contains(rand));
			tempNodesNotInList.add(rand);
			n = gds.newNodeInstance(rand);
			randomNodesNotInList[i] = n;

		}

		tempNodesInList.toArray(randomIDsInList);
		tempNodesNotInList.toArray(randomIDsNotInList);

		nodeListToBenchmarkCasted = null;
		if (IReadable.class.isAssignableFrom(dsClass)) {
			nodeListToBenchmarkCasted = (INodeListDatastructureReadable) nodeListToBenchmark;
		}
	}

	private void initForEdgeList(Class<? extends IDataStructure> dsClass,
			int initialSize) {
		if (edgeListToBenchmark == null
				|| edgeListToBenchmark.size() != initialSize) {
			edgeListToBenchmark = (IEdgeListDatastructure) gds
					.newList(ListType.GlobalEdgeList);
			for (int i = 0; i < initialSize; i++) {
				edgeListToBenchmark.add(edgeList[i]);
			}
		}

		int rand;
		IEdge e;

		randomEdgesInList = new Edge[operationSize];
		randomEdgesNotInList = new Edge[operationSize];

		ArrayList<Edge> tempEdgesInList = new ArrayList<Edge>();

		for (int i = 0; i < operationSize; i++) {
			do {
				rand = Rand.rand.nextInt(initialSize);
				e = edgeList[rand];
			} while (tempEdgesInList.contains(e));
			tempEdgesInList.add((Edge) e);

			e = gds.newEdgeInstance((Node) nodeList[i], (Node) nodeList[i]);
			randomEdgesNotInList[i] = (Edge) e;
		}

		tempEdgesInList.toArray(randomEdgesInList);

		edgeListToBenchmarkCasted = null;
		if (IReadable.class.isAssignableFrom(dsClass)) {
			edgeListToBenchmarkCasted = (IEdgeListDatastructureReadable) edgeListToBenchmark;
		}
	}

	public void setUp(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		setUpGds(dsClass, setupSize);
		int initialSize = (int) (setupSize - Math.ceil(operationSize / 2));

		if (INodeListDatastructure.class.isAssignableFrom(dsClass)) {
			initForNodeList(dsClass, initialSize);
		}

		if (IEdgeListDatastructure.class.isAssignableFrom(dsClass)) {
			initForEdgeList(dsClass, initialSize);
		}

	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUpGds")
	public void Init(Class<? extends IDataStructure> dsClass, Integer setupSize) {
		edgeListToBenchmark = (IEdgeListDatastructure) gds
				.newList(ListType.GlobalEdgeList);
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = setupSize; i < (setupSize + Math.ceil(operationSize / 2)); i++) {
			nodeListToBenchmark.add(nodeList[i]);
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Add_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = setupSize; i < (setupSize + Math.ceil(operationSize / 2)); i++) {
			edgeListToBenchmark.add(edgeList[i]);
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsSuccess_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.contains(nodeList[i]);
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsSuccess_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			edgeListToBenchmark.contains(edgeList[i]);
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsFailure_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.contains(nodeList[setupSize + i]);
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void ContainsFailure_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			edgeListToBenchmark.contains(randomEdgesNotInList[i]);
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetSuccess_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmarkCasted == null)
			throw new RuntimeException("No benchmarking here");
		for (int i : randomIDsInList) {
			res = nodeListToBenchmarkCasted.get(i) != null;
			if (!res) {
				System.err
						.println("Misdefined benchmark GetSuccess_Node for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetSuccess_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmarkCasted == null)
			throw new RuntimeException("No benchmarking here");
		for (Edge e : randomEdgesInList) {
			res = edgeListToBenchmarkCasted.get(e) != null;
			if (!res && dsClass != DEmpty.class) {
				System.err
						.println("Misdefined benchmark GetSuccess_Edge for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetFailure_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmarkCasted == null)
			throw new RuntimeException("No benchmarking here");
		for (int i : randomIDsNotInList) {
			res = nodeListToBenchmarkCasted.get(i) != null;
			if (res) {
				System.err.println("Misdefined benchmark GetFailure_Node");
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void GetFailure_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmarkCasted == null)
			throw new RuntimeException("No benchmarking here");
		for (Edge e : randomEdgesNotInList) {
			res = edgeListToBenchmarkCasted.get(e) != null;
			if (res) {
				System.err
						.println("Misdefined benchmark GetFailure_Edge for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Random_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmarkCasted == null)
			throw new RuntimeException("No benchmarking here");
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmarkCasted.getRandom();
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Random_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmarkCasted == null)
			throw new RuntimeException("No benchmarking here");
		for (i = 0; i < operationSize; i++) {
			edgeListToBenchmarkCasted.getRandom();
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveSuccess_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = nodeListToBenchmark.remove(nodeList[randomIDsInList[i]]);
			if (!res) {
				System.err
						.println("Misdefined benchmark RemoveSuccess_Node for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveFailure_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = nodeListToBenchmark.remove(randomNodesNotInList[i]);
			if (res) {
				System.err
						.println("Misdefined benchmark RemoveFailure_Node for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveSuccess_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = edgeListToBenchmark.remove(randomEdgesInList[i]);
			if (!res) {
				System.err
						.println("Misdefined benchmark RemoveSuccess_Edge for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void RemoveFailure_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		for (i = 0; i < operationSize; i++) {
			res = edgeListToBenchmark.remove(randomEdgesNotInList[i]);
			if (res && dsClass != DEmpty.class) {
				System.err
						.println("Misdefined benchmark RemoveFailure_Edge for class "
								+ dsClass.getSimpleName()
								+ " and input size "
								+ setupSize);
				return;
			}
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Size_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmark == null)
			throw new RuntimeException("No benchmarking here");
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.size();
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Size_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmark == null)
			throw new RuntimeException("No benchmarking here");
		for (i = 0; i < operationSize; i++) {
			edgeListToBenchmark.size();
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Iterator_Node(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (nodeListToBenchmark == null)
			throw new RuntimeException("No benchmarking here");
		for (i = 0; i < operationSize; i++) {
			nodeListToBenchmark.iterator();
		}
	}

	@Bench(dataProvider = "testInput", beforeEachRun = "setUp")
	public void Iterator_Edge(Class<? extends IDataStructure> dsClass,
			Integer setupSize) {
		if (edgeListToBenchmark == null)
			throw new RuntimeException("No benchmarking here");
		for (i = 0; i < operationSize; i++) {
			edgeListToBenchmark.iterator();
		}
	}

	public static void main(String[] args) {
		BenchmarkingConf benchmarkingConf = new BenchmarkingConf();
		final Benchmark bm = new Benchmark(benchmarkingConf);
		bm.add(new BenchmarkingExperiments(args[0], benchmarkingConf));
		final BenchmarkResult res = bm.run();
		new BenchmarkingVisitor(benchmarkingConf).visitBenchmark(res);
		// new TabularSummaryOutput().visitBenchmark(res);
	}
}
