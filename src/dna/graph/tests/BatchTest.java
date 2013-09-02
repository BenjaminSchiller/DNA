package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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

import dna.graph.Graph;
import dna.graph.IWeighted;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.IGraphGenerator;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.io.BatchReader;
import dna.io.BatchWriter;
import dna.io.GraphReader;
import dna.io.GraphWriter;
import dna.updates.Batch;
import dna.updates.BatchGenerator;
import dna.updates.EdgeRemoval;
import dna.updates.NodeRemoval;
import dna.updates.Update;
import dna.updates.directed.DirectedBatchGenerator;
import dna.updates.undirected.UndirectedBatchGenerator;
import dna.util.parameters.Parameter;

@RunWith(Parameterized.class)
public class BatchTest {
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;
	private Class<? extends IGraphGenerator> generator;
	private Constructor<? extends GraphGenerator> generatorConstructor;
	private Constructor<? extends BatchGenerator<?, ?>> bGenC;
	private GraphDataStructure gds;
	private GraphGenerator gg;
	private BatchGenerator<Node, Edge> bGen;

	private final int nodeSize = 100;
	private final int edgeSize = 150;

	private final int nodeAdd = nodeSize;
	private final int nodeRem = (int) Math.floor(nodeSize / 2);
	private final int edgeAdd = edgeSize;
	private final int edgeRem = (int) Math.floor(edgeSize / 2);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public BatchTest(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType,
			Class<? extends GraphGenerator> generator,
			Class<? extends BatchGenerator<?, ?>> bGen)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.nodeType = nodeType;
		this.edgeType = edgeType;
		this.generator = generator;
		this.generatorConstructor = generator.getConstructor(String.class,
				Parameter[].class, GraphDataStructure.class, long.class,
				int.class, int.class);

		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType,
				nodeEdgeListType, nodeType, edgeType);
		this.gg = this.generatorConstructor.newInstance("ABC",
				new Parameter[] {}, gds, 0, nodeSize, edgeSize);

		this.bGenC = bGen.getConstructor(int.class, int.class, int.class,
				int.class, GraphDataStructure.class);
		this.bGen = (BatchGenerator<Node, Edge>) bGenC.newInstance(nodeAdd,
				nodeRem, edgeAdd, edgeRem, this.gds);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Parameterized.Parameters(name = "{0} {1} {2} {3} {4} {5} {6}")
	public static Collection testPairs() throws NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Constructor<? extends GraphGenerator> generatorConstructor;
		GraphDataStructure gds;

		ArrayList<Object> result = new ArrayList<>();
		for (Class nodeListType : GlobalTestParameters.dataStructures) {
			for (Class edgeListType : GlobalTestParameters.dataStructures) {
				for (Class nodeEdgeListType : GlobalTestParameters.dataStructures) {
					for (Class generator : GlobalTestParameters.graphGenerators) {
						for (Class edgeType : GlobalTestParameters.edgeTypes) {
							for (Class nodeType : GlobalTestParameters.nodeTypes) {
								for (Class bGen : GlobalTestParameters.batchGenerators) {
									if (!(INodeListDatastructureReadable.class
											.isAssignableFrom(nodeListType)))
										continue;
									if (!(IEdgeListDatastructureReadable.class
											.isAssignableFrom(edgeListType)))
										continue;
									if (!(IEdgeListDatastructureReadable.class
											.isAssignableFrom(nodeEdgeListType)))
										continue;

									if ((UndirectedEdge.class
											.isAssignableFrom(edgeType) && DirectedNode.class
											.isAssignableFrom(nodeType))
											|| (DirectedEdge.class
													.isAssignableFrom(edgeType) && UndirectedNode.class
													.isAssignableFrom(nodeType)))
										continue;

									if ((UndirectedEdge.class
											.isAssignableFrom(edgeType) && DirectedBatchGenerator.class
											.isAssignableFrom(bGen))
											|| (DirectedEdge.class
													.isAssignableFrom(edgeType) && UndirectedBatchGenerator.class
													.isAssignableFrom(bGen)))
										continue;

									generatorConstructor = generator
											.getConstructor(String.class,
													Parameter[].class,
													GraphDataStructure.class,
													long.class, int.class,
													int.class);
									gds = new GraphDataStructure(nodeListType,
											edgeListType, nodeEdgeListType,
											nodeType, edgeType);
									GraphGenerator gg = generatorConstructor
											.newInstance("ABC",
													new Parameter[] {}, gds, 0,
													5, 5);

									if (!gg.canGenerateNodeType(nodeType))
										continue;
									if (!gg.canGenerateEdgeType(edgeType))
										continue;

									result.add(new Object[] { nodeListType,
											edgeListType, nodeEdgeListType,
											nodeType, edgeType, generator, bGen });
								}
							}
						}
					}
				}
			}
		}

		return result;
	}

	@Test
	public void testRandomBatchGenerator() {
		Graph g = gg.generate();
		Batch<?> b = bGen.generate(g);
		b.sanitize();
		assertTrue(b.apply(g));
	}

	@Test
	public void batchEqualityTest() {
		Graph g = gg.generate();

		Batch<Edge> b1 = new Batch<Edge>(gds, 0, 0);
		Batch<Edge> b2 = new Batch<Edge>(gds, 0, 0);

		Update<Edge> eR = new EdgeRemoval<Edge>(g.getRandomEdge());
		NodeRemoval<Edge> nR = new NodeRemoval<>(g.getRandomNode());

		b1.add(eR);
		b1.add(nR);

		b2.add(eR);

		assertNotEquals(b1, b2);

		b2.add(nR);

		assertEquals(b1, b2);
	}

	@Test
	public void batchWriteAndRead() throws ClassNotFoundException, IOException {
		String tempFolder = folder.getRoot().getAbsolutePath();

		Graph g = gg.generate();
		GraphWriter gw = new GraphWriter();
		gw.write(g, tempFolder, "gGen");

		Batch<Edge> b = bGen.generate(g);
		b.sanitize();
		
		BatchWriter<Node, Edge> bw = new BatchWriter<>();
		assertTrue(bw.write(b, tempFolder, "bGen"));

		BatchReader<Node, Edge, IWeighted> br = new BatchReader<>(this.gds);
		Batch<Edge> b2 = br.read(tempFolder, "bGen", g);
		bw.write(b2, tempFolder, "bRead");
		assertEquals(b, b2);
				
		b.apply(g);
		gw.write(g, tempFolder, "gGenUpdated");
		
		GraphReader gr = new GraphReader();
		Graph gRead = gr.read(tempFolder, "gGen");
		assertTrue(b2.apply(gRead));
		gw.write(gRead, tempFolder, "gReadUpdated");
		
		assertEquals(g, gRead);
		
	}

}
