package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.generators.CliqueGenerator;
import dna.graph.generators.EmptyGraphGenerator;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.IGraphGenerator;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IWeighted;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.EdgeWeightSelection;
import dna.graph.weights.Weights.NodeWeightSelection;
import dna.io.BatchReader;
import dna.io.BatchWriter;
import dna.io.GraphReader;
import dna.io.GraphWriter;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.RandomBatch;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;

@RunWith(Parameterized.class)
public class BatchTest {
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;
	private Class<? extends IGraphGenerator> generator;
	private Constructor<? extends GraphGenerator> generatorConstructor;
	private GraphDataStructure gds;
	private GraphGenerator gg;
	private BatchGenerator bGen;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private int nodeSize, edgeSize, nodeAdd, nodeRem, edgeAdd, edgeRem,
			nodeWeightChanges, edgeWeightChanges;

	public BatchTest(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType,
			Class<? extends GraphGenerator> generator)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.nodeType = nodeType;
		this.edgeType = edgeType;
		this.generator = generator;
		initSizes();

		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType,
				nodeEdgeListType, nodeType, edgeType);
		try {
			generatorConstructor = generator.getConstructor(
					GraphDataStructure.class, int.class, int.class);
			gg = generatorConstructor.newInstance(gds, nodeSize, edgeSize);
		} catch (NoSuchMethodException e) {
			generatorConstructor = generator.getConstructor(
					GraphDataStructure.class, int.class);
			gg = generatorConstructor.newInstance(gds, nodeSize);
		}

		this.bGen = new RandomBatch(nodeAdd, nodeRem, nodeWeightChanges,
				getNodeWeightSelector(), edgeAdd, edgeRem, edgeWeightChanges,
				getEdgeWeightSelector());
	}

	private EdgeWeightSelection getEdgeWeightSelector() {
		EdgeWeightSelection res = null;
		String prefix;

		if (!IWeightedEdge.class.isAssignableFrom(edgeType))
			return null;

		// Determine which weight type is used
		Class<?> weightType = gds.getEdgeWeightType();
		if (Integer.class.equals(weightType)) {
			prefix = Weights.IntWeightPrefix;
		} else if (Double.class.equals(weightType)) {
			prefix = Weights.DoubleWeightPrefix;
		} else {
			throw new RuntimeException("Can not determine weight type for "
					+ gds.getEdgeType());
		}

		EdgeWeightSelection[] values = EdgeWeightSelection.values();
		do {
			int pos = new Random().nextInt(values.length);
			if (values[pos].toString().startsWith(prefix))
				res = values[pos];
		} while (res == null);
		return res;
	}

	private NodeWeightSelection getNodeWeightSelector() {
		NodeWeightSelection res = null;
		String prefix;

		if (!IWeightedNode.class.isAssignableFrom(nodeType))
			return null;

		// Determine which weight type is used
		Class<?> weightType = gds.getNodeWeightType();
		if (Integer.class.equals(weightType)) {
			prefix = Weights.IntWeightPrefix;
		} else if (Double.class.equals(weightType)) {
			prefix = Weights.DoubleWeightPrefix;
		} else {
			throw new RuntimeException("Can not determine weight type for "
					+ gds.getNodeType());
		}

		NodeWeightSelection[] values = NodeWeightSelection.values();
		do {
			int pos = new Random().nextInt(values.length);
			if (values[pos].toString().startsWith(prefix))
				res = values[pos];
		} while (res == null);
		return res;
	}

	public void initSizes() {
		nodeSize = 100;
		edgeSize = 150;

		if (this.generator == CliqueGenerator.class) {
			/**
			 * As clique graphs are large, generate a smaller one please!
			 */
			nodeSize = (int) Math.min(Math.floor(nodeSize / 2), 30);
			edgeSize = nodeSize * (nodeSize - 1);

			if (UndirectedNode.class.isAssignableFrom(nodeType))
				edgeSize = (int) edgeSize / 2;
		}

		nodeAdd = nodeSize;
		nodeRem = nodeSize / 2;
		nodeWeightChanges = nodeSize / 2;
		edgeAdd = edgeSize;
		edgeRem = edgeSize / 2;
		edgeWeightChanges = edgeSize / 2;

		// Adding edges in a clique graph is nonsense
		if (this.generator == CliqueGenerator.class) {
			edgeAdd = 0;
		}

		if (!IWeightedNode.class.isAssignableFrom(nodeType))
			nodeWeightChanges = 0;
		if (!IWeightedEdge.class.isAssignableFrom(edgeType))
			edgeWeightChanges = 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Parameterized.Parameters(name = "{0} {1} {2} {3} {4} {5}")
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

								if (generator == EmptyGraphGenerator.class)
									continue;
								gds = new GraphDataStructure(nodeListType,
										edgeListType, nodeEdgeListType,
										nodeType, edgeType);
								GraphGenerator gg;
								try {
									generatorConstructor = generator
											.getConstructor(
													GraphDataStructure.class,
													int.class, int.class);
									gg = generatorConstructor.newInstance(gds,
											0, 0);
								} catch (NoSuchMethodException e) {
									generatorConstructor = generator
											.getConstructor(
													GraphDataStructure.class,
													int.class);
									gg = generatorConstructor.newInstance(gds,
											0);
								}

								if (!gg.canGenerateNodeType(nodeType))
									continue;
								if (!gg.canGenerateEdgeType(edgeType))
									continue;

								result.add(new Object[] { nodeListType,
										edgeListType, nodeEdgeListType,
										nodeType, edgeType, generator });
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
		Batch b = bGen.generate(g);
		BatchSanitization.sanitize(b);
		assertTrue(b.apply(g));
	}

	@Test
	public void batchEqualityTest() {
		Graph g = gg.generate();

		Batch b1 = new Batch(gds, 0, 0);
		Batch b2 = new Batch(gds, 0, 0);

		Update eR = new EdgeRemoval(g.getRandomEdge());
		NodeRemoval nR = new NodeRemoval(g.getRandomNode());

		b1.add(eR);
		b1.add(nR);

		b2.add(eR);

		assertNotEquals(b1, b2);

		b2.add(nR);

		assertEquals(b1, b2);
	}

	@Test
	public void checkHashCodesDontChangeWithChangedWeights() {
		assumeTrue(IWeighted.class.isAssignableFrom(nodeType)
				&& IWeighted.class.isAssignableFrom(edgeType));

		Graph g = gg.generate();
		Batch b = bGen.generate(g);
		BatchSanitization.sanitize(b);

		// Get the first elements from weight-based updates
		NodeWeight nwUpdate = b.getNodeWeights().iterator().next();
		int nwUpdateHashCode = nwUpdate.hashCode();
		EdgeWeight ewUpdate = b.getEdgeWeights().iterator().next();
		int ewUpdateHashCode = ewUpdate.hashCode();

		b.apply(g);

		assertEquals("Hashcode has changed after applying the update",
				nwUpdateHashCode, nwUpdate.hashCode());
		assertEquals("Hashcode has changed after applying the update",
				ewUpdateHashCode, ewUpdate.hashCode());
	}

	@Test
	public void batchWriteAndRead() throws ClassNotFoundException, IOException {
		String tempFolder = folder.getRoot().getAbsolutePath();

		Graph g = gg.generate();
		GraphWriter.write(g, tempFolder, "gGen");

		Graph gRead = GraphReader.read(tempFolder, "gGen");
		assertEquals(g, gRead);

		Batch b = bGen.generate(g);
		BatchSanitization.sanitize(b);

		assertTrue(BatchWriter.write(b, tempFolder, "bGen"));

		b.apply(g);
		GraphWriter.write(g, tempFolder, "gGenUpdated");

		// All stuff is written now, read it in again and check for equality
		Batch b2 = BatchReader.read(tempFolder, "bGen", gRead);
		BatchWriter.write(b2, tempFolder, "bRead");

		assertTrue(b2.apply(gRead));
		assertEquals(b, b2);

		GraphWriter.write(gRead, tempFolder, "gReadUpdated");

		assertEquals(g, gRead);

	}

}
