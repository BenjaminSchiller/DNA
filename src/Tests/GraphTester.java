package Tests;

import java.lang.reflect.InvocationTargetException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Graph.DirectedNode;
import Graph.Graph;
import Graph.Node;
import Graph.UndirectedNode;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class GraphTester {
	private Graph graph;
	private Class<? extends Node> nodeType;
	private Class<? extends INodeListDatastructure> nodeListType;
	private Class<? extends IEdgeListDatastructure> graphEdgeListType;
	private Class<? extends IEdgeListDatastructure> nodeEdgeListType;

	public GraphTester(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends Node> nodeType)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.graph = new Graph("Test", 0L, nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
		this.nodeListType = nodeListType;
		this.graphEdgeListType = graphEdgeListType;
		this.nodeEdgeListType = nodeEdgeListType;
		this.nodeType = nodeType;
	}

	@Parameterized.Parameters(name = "{0} {1} {2} {3}")
	public static Collection testPairs() {
		Class[] dataStructures = { DArrayList.class, DHashSet.class };
		Class[] nodeTypes = { UndirectedNode.class, DirectedNode.class };

		ArrayList result = new ArrayList<>();
		for (Class nodeListType : dataStructures) {
			for (Class edgeListType : dataStructures) {
				for (Class nodeEdgeListType : dataStructures) {
					for (Class nodeType : nodeTypes) {
						if ( !(INodeListDatastructure.class.isAssignableFrom(nodeListType))) continue;
						if ( !(IEdgeListDatastructure.class.isAssignableFrom(edgeListType))) continue;
						if ( !(IEdgeListDatastructure.class.isAssignableFrom(nodeEdgeListType))) continue;						
						result.add(new Object[] { nodeListType, edgeListType, nodeEdgeListType, nodeType });
					}
				}
			}
		}

		return result;
	}

	@Test
	public void addNode() {
		Node dummy = mock(nodeType);
		when(dummy.getIndex()).thenReturn(42);
		
		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(dummy);
		assertTrue(graph.containsNode(dummy));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}
	
	@Test
	public void removeNode() {
		Node dummy = mock(nodeType);
		when(dummy.getIndex()).thenReturn(42);

		Node dummy2 = mock(nodeType);
		when(dummy2.getIndex()).thenReturn(23);

		Node dummy3 = mock(nodeType);
		when(dummy3.getIndex()).thenReturn(17);
		
		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(dummy);
		graph.addNode(dummy2);
		
		assertEquals(42, graph.getMaxNodeIndex());
		graph.removeNode(dummy);
		
		assertEquals(23, graph.getMaxNodeIndex());
		
		assertFalse(graph.containsNode(dummy3));
		assertFalse(graph.removeNode(dummy3));
		
		graph.removeNode(dummy2);
		assertEquals(-1, graph.getMaxNodeIndex());
	}
	
	@Test
	public void nameAndTimestamp() {
		java.util.Date date= new java.util.Date();
		long ts = date.getTime();
		String name = Long.toString(ts);
		Graph g = new Graph(name, ts, nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
		assertEquals(name,g.getName());
		assertEquals(ts, g.getTimestamp());
	}
}
