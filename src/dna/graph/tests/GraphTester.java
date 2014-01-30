package dna.graph.tests;

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.Graph;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IWeighted;
import dna.profiler.ProfilerMeasurementData;
import dna.util.Config;

@RunWith(Parameterized.class)
public class GraphTester {
	private Graph graph;
	private GraphDataStructure gds;
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;

	public GraphTester(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType,
				nodeEdgeListType, nodeType, edgeType);
		this.gds.setEdgeType(edgeType);
		this.graph = gds.newGraphInstance("ABC", 1L, 10, 10);
		this.nodeType = nodeType;
		this.edgeType = edgeType;
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1} {2} {3} {4}")
	public static Collection<Object> testPairs() {
		ArrayList<Object> result = new ArrayList<>();
		for (Class nodeListType : GlobalTestParameters.dataStructures) {
			for (Class edgeListType : GlobalTestParameters.dataStructures) {
				for (Class nodeEdgeListType : GlobalTestParameters.dataStructures) {
					for (Class nodeType : GlobalTestParameters.nodeTypes) {
						for (Class edgeType : GlobalTestParameters.edgeTypes) {
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

							if (edgeListType == DEmpty.class
									|| nodeEdgeListType == DEmpty.class)
								continue;

							result.add(new Object[] { nodeListType,
									edgeListType, nodeEdgeListType, nodeType,
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
					assertNotNull(gds.getComplexityClass(lt, at, pType));
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
		Node n = gds.newNodeInstance("42");

		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(n);
		assertTrue(graph.containsNode(n));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void addWeightedNode() {
		assumeTrue(IWeightedNode.class.isAssignableFrom(nodeType));

		Object mock = mockedWeight(nodeType, true);
		IWeightedNode n = gds.newWeightedNode(1, mock);
		assertEquals(mock, n.getWeight());
		assertTrue(graph.addNode((Node) n));

		Object mock2 = mockedWeight(nodeType, false);
		assertNotEquals("mockedWeight not returning two different mocks", mock,
				mock2);
		IWeightedNode n2 = gds.newWeightedNode(1, mock2);
		assertEquals(mock2, n2.getWeight());
		assertFalse(graph.addNode((Node) n2));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void addWeightedEdge() {
		assumeTrue(IWeightedEdge.class.isAssignableFrom(edgeType));

		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		Object mock = mockedWeight(edgeType, true);
		IWeightedEdge e = gds.newWeightedEdge(n1, n2, mock);
		assertEquals(mock, e.getWeight());
		assertTrue(graph.addEdge((Edge) e));

		Object mock2 = mockedWeight(edgeType, false);
		assertNotEquals("mockedWeight not returning two different mocks", mock,
				mock2);
		IWeightedEdge e2 = gds.newWeightedEdge(n1, n2, mock2);
		assertEquals(mock2, e2.getWeight());
		assertFalse(
				"Adding the same edge with different weight a second time succeeded (graph edge list: "
						+ this.gds.getGlobalEdgeListType() + ")",
				graph.addEdge((Edge) e2));
	}

	@Test
	public void addEdgeByID() {
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		Edge e = gds.newEdgeInstance(n1, n2);
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

		Edge e = gds.newEdgeInstance(edgeString, graph);
		graph.addEdge(e);
		n1.addEdge(e);
		n2.addEdge(e);

		assertTrue(n1.hasEdge(e));
		assertTrue(n2.hasEdge(e));
	}

	@Test
	public void checkGetEdgeByDummy() {
		assumeTrue(gds.isReadable());
		assumeTrue(IWeighted.class.isAssignableFrom(edgeType));

		Object mock = mockedWeight(edgeType, true);

		// Create a "real" edge first
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);
		IWeightedEdge<?> e = gds.newWeightedEdge(n1, n2, mock);
		graph.addEdge((Edge) e);

		// Then create a dummy using the nodes, with obvious inequal weights
		mock = mockedWeight(edgeType, false);
		IWeightedEdge<?> eDummy = gds.newWeightedEdge(n1, n2, mock);
		assertEquals(e, eDummy);
		assertNotEquals(e.getWeight(), eDummy.getWeight());

		eDummy = (IWeightedEdge<?>) graph.getEdge((Edge) eDummy);
		assertEquals(e, eDummy);
		assertEquals(e.getWeight(), eDummy.getWeight());
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

		for (Class<? extends Edge> edge : GlobalTestParameters.edgeTypes) {
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
	public Object mockedWeight(Class<?> type, boolean kindSelector) {
		Class<?> weightType = null;
		if (Node.class.isAssignableFrom(type)) {
			weightType = gds.getNodeWeightType();
		} else if (Edge.class.isAssignableFrom(type)) {
			weightType = gds.getEdgeWeightType();
		} else {
			fail("Can't get weight type for " + type);
		}

		if (weightType == null)
			fail("Cannot get weight for " + type.getSimpleName());

		switch (weightType.getSimpleName()) {
		case "Integer":
			if (kindSelector)
				return (Integer) 1;
			else
				return (Integer) 2;
		case "Double":
			if (kindSelector)
				return (Double) 1d;
			else
				return 2d;
		default:
			fail("Cannot mock type " + weightType.getName());
		}
		return null;
	}

}
