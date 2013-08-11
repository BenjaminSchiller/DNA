package genericsWithTest.Tests;

import genericsWithTest.DirectedNode;
import genericsWithTest.Graph;
import genericsWithTest.Node;
import genericsWithTest.UndirectedNode;
import genericsWithTest.DataStructures.DArrayList;
import genericsWithTest.DataStructures.DHashSet;
import genericsWithTest.DataStructures.IEdgeListDatastructure;
import genericsWithTest.DataStructures.INodeListDatastructure;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class GraphTester {
	private Graph graph;
	private Class<? extends Node> nodeType;

	public GraphTester(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends Node> nodeType)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.graph = new Graph("Test", 0L, nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
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
		graph.addNode(dummy);
		assertTrue(graph.containsNode(dummy));
	}
}
