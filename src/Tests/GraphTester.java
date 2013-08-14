package Tests;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.GraphDataStructure;
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
	private GraphDataStructure gds;

	public GraphTester(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends Node> nodeType)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
		this.graph = new Graph("Test", 0L, gds);
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1} {2} {3}")
	public static Collection<Object> testPairs() {
		Class[] dataStructures = { DArrayList.class, DHashSet.class };
		Class[] nodeTypes = { UndirectedNode.class, DirectedNode.class };

		ArrayList<Object> result = new ArrayList<>();
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
		Node dummy = mock(gds.getNodeType());
		when(dummy.getIndex()).thenReturn(42);
		
		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(dummy);
		assertTrue(graph.containsNode(dummy));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}
	
	@Test
	public void removeNode() {
		Node dummy = mock(gds.getNodeType());
		when(dummy.getIndex()).thenReturn(42);

		Node dummy2 = mock(gds.getNodeType());
		when(dummy2.getIndex()).thenReturn(23);

		Node dummy3 = mock(gds.getNodeType());
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
		Graph g = new Graph(name, ts, this.gds);
		assertEquals(name,g.getName());
		assertEquals(ts, g.getTimestamp());
	}
}
