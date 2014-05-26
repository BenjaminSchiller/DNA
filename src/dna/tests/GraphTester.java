package dna.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.ClassPointers;
import dna.graph.Graph;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeighted;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;
import dna.profiler.ProfilerMeasurementData;
import dna.util.Config;

@RunWith(Parameterized.class)
public class GraphTester {
	private Graph graph;
	private GraphDataStructure gds;
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;

	public GraphTester(
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.gds = new GraphDataStructure(listTypes, nodeType, edgeType,
				DoubleWeight.class, WeightSelection.RandTrim1, IntWeight.class,
				WeightSelection.RandPos100);
		this.gds.setEdgeType(edgeType);
		this.graph = gds.newGraphInstance("ABC", 1L, 10, 10);
		this.nodeType = nodeType;
		this.edgeType = edgeType;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Parameterized.Parameters(name = "{0} {1} {2}")
	public static Collection<Object> testPairs() {
		ArrayList<Object> result = new ArrayList<>();
		for (Class nodeListType : ClassPointers.dataStructures) {
			for (Class edgeListType : ClassPointers.dataStructures) {
				for (Class nodeEdgeListType : ClassPointers.dataStructures) {
					for (Class nodeType : ClassPointers.nodeTypes) {
						for (Class edgeType : ClassPointers.edgeTypes) {
							if ((UndirectedEdge.class
									.isAssignableFrom(edgeType) && DirectedNode.class
									.isAssignableFrom(nodeType))
									|| (DirectedEdge.class
											.isAssignableFrom(edgeType) && UndirectedNode.class
											.isAssignableFrom(nodeType)))
								continue;

							if (!(INodeListDatastructure.class
									.isAssignableFrom(nodeListType)))
								continue;
							if (!(IEdgeListDatastructure.class
									.isAssignableFrom(edgeListType)))
								continue;
							if (!(IEdgeListDatastructure.class
									.isAssignableFrom(nodeEdgeListType)))
								continue;

							if (nodeListType == DEmpty.class
									|| edgeListType == DEmpty.class
									|| nodeEdgeListType == DEmpty.class)
								continue;

							EnumMap<ListType, Class<? extends IDataStructure>> listTypes = new EnumMap<ListType, Class<? extends IDataStructure>>(
									ListType.class);
							listTypes
									.put(ListType.GlobalNodeList, nodeListType);
							listTypes
									.put(ListType.GlobalEdgeList, edgeListType);
							listTypes.put(ListType.LocalEdgeList,
									nodeEdgeListType);

							result.add(new Object[] { listTypes, nodeType,
									edgeType });
						}
					}
				}
			}
		}

		return result;
	}

	@Test
	public void datastructureKnowsAboutItsComplexity() {
		for (ListType lt : ListType.values()) {
			for (AccessType at : AccessType.values()) {
				for (ProfilerMeasurementData.ProfilerDataType pType : ProfilerMeasurementData.ProfilerDataType
						.values()) {
					if ( ProfilerMeasurementData.getDependencies(pType).length > 0) {
						/**
						 * Ignore this, as a PDT with dependencies does not declare it's own data
						 */
						continue;
					}
					assertNotNull(gds.getCostData(lt, at, pType));
				}
			}
		}
	}

	@Test
	public void testGraphDataStructureEqualsReadWrite() {
		String gdsString = gds.getDataStructures();
		GraphDataStructure gds2 = new GraphDataStructure(gdsString);
		assertEquals(gds, gds2);
	}

	@Test
	public void addNodeByID() {
		Node n = gds.newNodeInstance(42);

		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(n);
		assertTrue(graph.containsNode(n));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}

	@Test
	public void addNodeByString() {
		String nodeString = "42";
		if (graph.getGraphDatastructures().createsWeightedNodes()) {
			nodeString += Weight.WeightDelimiter + "1";
		}

		Node n = gds.newNodeInstance(nodeString);

		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(n);
		assertTrue(graph.containsNode(n));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}

	@Test
	public void addWeightedNode() {
		assumeTrue(IWeightedNode.class.isAssignableFrom(nodeType));

		Weight mock = mockedWeight(nodeType, true);
		Node n = gds.newWeightedNode(1, mock);
		assertEquals(mock, ((IWeighted) n).getWeight());
		assertTrue(graph.addNode((Node) n));

		Weight mock2 = mockedWeight(nodeType, false);
		assertNotEquals("mockedWeight not returning two different mocks", mock,
				mock2);
		Node n2 = gds.newWeightedNode(1, mock2);
		assertEquals(mock2, ((IWeighted) n2).getWeight());
		assertFalse(graph.addNode((Node) n2));
	}

	@Test
	public void addWeightedEdge() {
		assumeTrue(IWeightedEdge.class.isAssignableFrom(edgeType));

		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		Weight mock = mockedWeight(edgeType, true);
		Edge e = gds.newWeightedEdge(n1, n2, mock);
		assertEquals(mock, ((IWeighted) e).getWeight());
		assertTrue(graph.addEdge((Edge) e));

		Weight mock2 = mockedWeight(edgeType, false);
		assertNotEquals("mockedWeight not returning two different mocks", mock,
				mock2);
		Edge e2 = gds.newWeightedEdge(n1, n2, mock2);
		assertEquals(mock2, ((IWeighted) e2).getWeight());
		assertFalse(
				"Adding the same edge with different weight a second time succeeded (graph edge list: "
						+ gds.getListClass(ListType.GlobalEdgeList) + ")",
				graph.addEdge((Edge) e2));
	}

	@Test
	public void addEdgeByID() {
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		Edge e = null;
		if (graph.getGraphDatastructures().createsWeightedEdges()) {
			e = (Edge) gds.newWeightedEdge(n1, n2, new IntWeight(1));
		} else {
			e = gds.newEdgeInstance(n1, n2);
		}
		graph.addEdge(e);
		n1.addEdge(e);
		n2.addEdge(e);

		assertTrue(n1.hasEdge(e));
		assertTrue(n2.hasEdge(e));
	}

	@Test
	public void addEdgeByString() {
		assumeTrue(gds.isReadable());

		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		String edgeString;
		if (graph.isDirected()) {
			edgeString = "1" + Config.get("EDGE_DIRECTED_DELIMITER") + "2";
		} else {
			edgeString = "1" + Config.get("EDGE_UNDIRECTED_DELIMITER") + "2";
		}
		if (gds.createsWeightedEdges()) {
			edgeString += Weight.WeightDelimiter + "1";
		}

		Edge e = gds.newEdgeInstance(edgeString, graph);
		graph.addEdge(e);
		n1.addEdge(e);
		n2.addEdge(e);

		assertTrue(n1.hasEdge(e));
		assertTrue(n2.hasEdge(e));
	}

	@Test
	public void removeNode() {
		Node dummy = gds.newNodeInstance(0);
		Node dummy2 = gds.newNodeInstance(1);
		Node dummy3 = gds.newNodeInstance(2);

		assertEquals(-1, graph.getMaxNodeIndex());
		assertTrue(graph.addNode(dummy));
		assertTrue(graph.addNode(dummy2));

		assertEquals(1, graph.getMaxNodeIndex());
		assertTrue(graph.removeNode(dummy));

		assertEquals(1, graph.getMaxNodeIndex());

		assertFalse(graph.containsNode(dummy3));
		assertFalse(graph.removeNode(dummy3));

		assertTrue(graph.removeNode(dummy2));
		assertEquals(0, graph.getNodeCount());
		assertEquals(-1, graph.getMaxNodeIndex());
	}

	@Test
	public void nameAndTimestamp() {
		java.util.Date date = new java.util.Date();
		long ts = date.getTime();
		String name = Long.toString(ts);
		Graph g = new Graph(name, ts, this.gds);
		assertEquals(name, g.getName());
		assertEquals(ts, g.getTimestamp());
	}

	@Test
	public void graphEqualityForBasics() {
		long timestamp = 1L;

		Graph g1 = new Graph("N", timestamp, this.gds);
		Graph g2 = new Graph("N", timestamp, this.gds);
		Graph g3 = new Graph("N", timestamp + 1, this.gds);
		Graph g4 = new Graph("O", timestamp, this.gds);
		assertEquals(g1, g2);
		assertNotEquals(g1, g3);
		assertNotEquals(g2, g3);
		assertNotEquals(g1, g4);
		assertNotEquals(g2, g4);
		assertNotEquals(g3, g4);
	}

	@Test
	public void graphEqualityForNodes() {
		long timestamp = 1L;

		Graph g1 = new Graph("N", timestamp, this.gds);
		Graph g2 = new Graph("N", timestamp, this.gds);

		Node g1n1 = this.gds.newNodeInstance(42);
		Node g1n2 = this.gds.newNodeInstance(23);
		Node g2n1 = this.gds.newNodeInstance(42);
		Node g2n2 = this.gds.newNodeInstance(23);

		if (gds.createsWeightedNodes()) {
			((IWeighted) g1n1).setWeight(new IntWeight(1));
			((IWeighted) g1n2).setWeight(new IntWeight(1));
			((IWeighted) g2n1).setWeight(new IntWeight(1));
			((IWeighted) g2n2).setWeight(new IntWeight(1));
		}

		assertTrue(g1.addNode(g1n1));
		assertNotEquals(g1, g2);

		assertTrue(g2.addNode(g2n1));
		assertEquals(g1, g2);

		assertFalse(g1.removeNode(g1n2));
		assertEquals(g1, g2);

		assertFalse(g2.removeNode(g2n2));
		assertEquals(g1, g2);

		assertTrue(g1.removeNode(g1n1));
		assertNotEquals(g1, g2);

		assertTrue(g2.removeNode(g2n1));
		assertEquals(g1, g2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEdgesOfOtherTypes() {
		Node n1 = this.gds.newNodeInstance(1);
		Node n2 = this.gds.newNodeInstance(2);

		for (Class<? extends Edge> edge : ClassPointers.edgeTypes) {
			if ((UndirectedEdge.class.isAssignableFrom(edge) && DirectedNode.class
					.isAssignableFrom(nodeType))
					|| (DirectedEdge.class.isAssignableFrom(edge) && UndirectedNode.class
							.isAssignableFrom(nodeType))) {
				try {
					this.gds.setEdgeType(edge);
					Edge e = this.gds.newEdgeInstance(n1, n2);
					fail("Generated edge of type " + e.getClass()
							+ " on node type " + nodeType);
				} catch (RuntimeException e) {
					// Everything's fine, this should not be possible. Mixing
					// directed and undirected
					// IElements is no good idea
				}
			}
		}
	}

	/**
	 * Get a mocked weight for the type t
	 * 
	 * @param type
	 * @param kindSelector
	 *            We want to get two distinguishable dummies from this function,
	 *            so select which you like please
	 * @return
	 */
	public Weight mockedWeight(Class<?> type, boolean kindSelector) {
		if (kindSelector)
			return new IntWeight(1);
		else
			return new IntWeight(2);
	}

}
