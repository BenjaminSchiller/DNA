package Tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import Utils.parameters.Parameter;
import DataStructures.*;
import Factories.GraphGenerator;
import Factories.RandomDirectedGraphGenerator;
import Factories.RandomUndirectedDoubleWeightedGraphGenerator;
import Graph.Graph;
import Graph.Nodes.Node;
import IO.GraphReader;
import IO.GraphWriter;

@RunWith(Parameterized.class)
public class GeneratorsTest {
	private Class<? extends Node> nodeType;
	private Class<? extends INodeListDatastructure> nodeListType;
	private Class<? extends IEdgeListDatastructure> graphEdgeListType;
	private Class<? extends IEdgeListDatastructure> nodeEdgeListType;
	private Class<? extends GraphGenerator> generator;
	private Constructor<? extends GraphGenerator> generatorConstructor;
	private GraphDataStructure gds;
	
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
		this.generatorConstructor = generator.getConstructor(String.class, Parameter[].class,
				GraphDataStructure.class, long.class, int.class, int.class);
		
		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1} {2} {3} {4}")
	public static Collection testPairs() {
		Class[] dataStructures = { DArrayList.class, DArray.class, DHashMap.class, DHashSet.class };
		Class[] graphGenerators = { RandomDirectedGraphGenerator.class, RandomUndirectedDoubleWeightedGraphGenerator.class };

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
						
						try {
							nodeType = (Class<? extends Node>) generator.getField("nodeType").get(null);
						} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
								| SecurityException e) {
							e.printStackTrace();
							throw new RuntimeException("Field nodeType is missing in generator");
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
		int nodeSize = 100;
		int edgeSize = 150;
				
		GraphGenerator gg = this.generatorConstructor.newInstance("ABC", new Parameter[]{}, gds, 0, nodeSize, edgeSize);
		Graph g = gg.generate();
		
		assertEquals(nodeSize, g.getNodeCount());
		assertEquals(edgeSize, g.getEdgeCount());
	}
	
	@Test
	public void testWriteRead() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException {
		int nodeSize = 200;
		int edgeSize = 300;
		
		GraphGenerator gg = this.generatorConstructor.newInstance("ABC", new Parameter[]{}, gds, 0, nodeSize, edgeSize);
		Graph g = gg.generate();
		
		String graphName = gds.getDataStructures();
		
		String tempFolder = folder.getRoot().getAbsolutePath();
		
		GraphWriter gw = new GraphWriter();
		gw.write(g, tempFolder, graphName);

		GraphReader gr = new GraphReader();
		Graph g2 = gr.read(tempFolder, graphName, null);
				
		assertEquals(gds, g2.getGraphDatastructures());
		assertEquals(g, g2);
	}
	
	@Test
	public void testWriteReadWithErrorInNode() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException {
		int nodeSize = 10;
		int edgeSize = 15;
		
		GraphGenerator gg = this.generatorConstructor.newInstance("ABC", new Parameter[]{}, gds, 0, nodeSize, edgeSize);
		Graph g = gg.generate();
		
		String graphName = gds.getDataStructures();
		
		String tempFolder = folder.getRoot().getAbsolutePath();
		
		GraphWriter gw = new GraphWriter();
		gw.write(g, tempFolder, graphName);

		GraphReader gr = new GraphReader();
		Graph g2 = gr.read(tempFolder, graphName, null);
				
		assertEquals(gds, g2.getGraphDatastructures());		
		assertEquals(g, g2);
		
		// Change getStringRepresentation now to see that it is used for equality checks
		Node nodeReal = g.getNode(g.getNodeCount() - 1);
		assertNotNull(nodeReal);
		g.removeNode(nodeReal);
		Node nodeMocked = mock(this.nodeType);
		when(nodeMocked.getStringRepresentation()).thenReturn("");
		g.addNode(nodeMocked);	
		assertNotEquals(g, g2);
	}	
	
}
