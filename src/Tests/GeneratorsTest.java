package Tests;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import Utils.parameters.Parameter;
import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.GraphDataStructure;
import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Factories.DirectedGraphGenerator;
import Factories.GraphGenerator;
import Factories.RandomDirectedGraphGenerator;
import Factories.UndirectedGraphGenerator;
import Graph.Graph;
import Graph.Nodes.DirectedNode;
import Graph.Nodes.Node;
import Graph.Nodes.UndirectedNode;

@RunWith(Parameterized.class)
public class GeneratorsTest {
	private Class<? extends Node> nodeType;
	private Class<? extends INodeListDatastructure> nodeListType;
	private Class<? extends IEdgeListDatastructure> graphEdgeListType;
	private Class<? extends IEdgeListDatastructure> nodeEdgeListType;
	private Class<? extends GraphGenerator> generator;

	public GeneratorsTest(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, 
			Class<? extends Node> nodeType, Class<? extends GraphGenerator> generator)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.nodeListType = nodeListType;
		this.graphEdgeListType = graphEdgeListType;
		this.nodeEdgeListType = nodeEdgeListType;
		this.nodeType = nodeType;
		this.generator = generator;
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1} {2} {3} {4}")
	public static Collection testPairs() {
		Class[] dataStructures = { DArrayList.class, DHashSet.class };
		Class[] graphGenerators = { RandomDirectedGraphGenerator.class };

		ArrayList<Object> result = new ArrayList<>();
		for (Class nodeListType : dataStructures) {
			for (Class edgeListType : dataStructures) {
				for (Class nodeEdgeListType : dataStructures) {
					for (Class generator : graphGenerators) {
						Class<? extends Node> nodeType = null;
						
						if (!(INodeListDatastructure.class.isAssignableFrom(nodeListType)))
							continue;
						if (!(IEdgeListDatastructure.class.isAssignableFrom(edgeListType)))
							continue;
						if (!(IEdgeListDatastructure.class.isAssignableFrom(nodeEdgeListType)))
							continue;
						
						if ( DirectedGraphGenerator.class.isAssignableFrom(generator)) {
							nodeType = DirectedNode.class;
						} else if (UndirectedGraphGenerator.class.isAssignableFrom(generator)) {
							nodeType = UndirectedNode.class;
						} 
						
						result.add(new Object[] { nodeListType, edgeListType, nodeEdgeListType, nodeType, generator });
					}
				}
			}
		}

		return result;
	}

	@Test
	public void testGraphGeneration() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		int nodeSize = 10;
		int edgeSize = 15;
		
		GraphDataStructure gds = new GraphDataStructure(nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
		
		Constructor<? extends GraphGenerator> c = generator.getConstructor(String.class, long.class, Parameter[].class,
			GraphDataStructure.class, int.class, int.class);
		GraphGenerator gg = c.newInstance("ABC", 0, new Parameter[]{}, gds, nodeSize, edgeSize);
		Graph g = gg.generate();
		
		assertEquals(nodeSize, g.getNodeCount());
		assertEquals(edgeSize, g.getEdgeCount());
	}
	
}
