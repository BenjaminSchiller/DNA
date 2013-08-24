package Tests;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import Utils.Keywords;
import DataStructures.DArray;
import DataStructures.DArrayList;
import DataStructures.DHashMap;
import DataStructures.DHashSet;
import DataStructures.DLinkedList;
import DataStructures.GraphDataStructure;
import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Graph.Graph;
import Graph.Edges.Edge;
import Graph.Nodes.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class GraphTester {
	private Graph graph;
	private GraphDataStructure gds;
	
	public static Class[] nodeTypes = { UndirectedNode.class, UndirectedDoubleWeightedNode.class,
			DirectedNode.class, DirectedDoubleWeightedNode.class };	

	public GraphTester(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends Node> nodeType)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
		this.graph = gds.newGraphInstance("ABC", 1L, 10, 10);
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1} {2} {3}")
	public static Collection<Object> testPairs() {
		Class[] dataStructures = { DArray.class, DArrayList.class, DHashMap.class, DHashSet.class, DLinkedList.class };

		ArrayList<Object> result = new ArrayList<>();
		for (Class nodeListType : dataStructures) {
			for (Class edgeListType : dataStructures) {
				for (Class nodeEdgeListType : dataStructures) {
					for (Class nodeType : nodeTypes) {
							if (!(INodeListDatastructure.class.isAssignableFrom(nodeListType)))
								continue;
							if (!(IEdgeListDatastructure.class.isAssignableFrom(edgeListType)))
								continue;
							if (!(IEdgeListDatastructure.class.isAssignableFrom(nodeEdgeListType)))
								continue;
							result.add(new Object[] { nodeListType, edgeListType, nodeEdgeListType, nodeType });
					}
				}
			}
		}

		return result;
	}
	
	@Test
	public void testGraphDataStructureEqualsReadWrite() {
		String gdsString = gds.getDataStructures();
		GraphDataStructure gds2 = new GraphDataStructure(gdsString);
		assertEquals(gds,gds2);
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
		if ( graph.isDirected() ) {
			edgeString = "1" + Keywords.directedEdgeDelimiter + "2";
		} else {
			edgeString = "1" + Keywords.undirectedEdgeDelimiter + "2";
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
		Node dummy2 =  gds.newNodeInstance(1);
		Node dummy3 =  gds.newNodeInstance(2);
		
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
		java.util.Date date= new java.util.Date();
		long ts = date.getTime();
		String name = Long.toString(ts);
		Graph g = new Graph(name, ts, this.gds);
		assertEquals(name,g.getName());
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
		
		g1.addNode(g1n1);
		assertNotEquals(g1,g2);
		
		g2.addNode(g2n1);
		assertEquals(g1, g2);
		
		g1.removeNode(g1n2);
		assertEquals(g1, g2);
		
		g2.removeNode(g2n2);
		assertEquals(g1, g2);
		
		g1.removeNode(g1n1);
		assertNotEquals(g1,g2);
		
		g2.removeNode(g2n1);
		assertEquals(g1, g2);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void elementEqualities() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Class<? extends Node> originalNodeType = gds.getNodeType(); 
				
		Node n = this.gds.newNodeInstance(0);
		assertTrue(n.deepEquals(n));
		
		for(Class<? extends Node> otherElementClass: nodeTypes) {
			this.gds.setNodeType(otherElementClass);
			
			// Check for edges
			Edge e = this.gds.newEdgeInstance(n, n);
			assertTrue(e.deepEquals(e));
			assertFalse(n.deepEquals(e));
			assertFalse(e.deepEquals(n));
			
			// Check for *other* nodes
			if ( otherElementClass == originalNodeType ) continue;
			Node n2 = this.gds.newNodeInstance(1);
			assertTrue(n2.deepEquals(n2));
			assertFalse(n.deepEquals(n2));
			assertFalse(n2.deepEquals(n));			
		}
	}
}
