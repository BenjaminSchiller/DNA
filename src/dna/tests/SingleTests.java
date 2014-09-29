package dna.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.EnumMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dna.graph.ClassPointers;
import dna.graph.Graph;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.DHashTable;
import dna.graph.datastructures.DLinkedHashMultimap;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.RandomGraph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.nodes.UndirectedWeightedNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.MetricNotApplicableException;
import dna.series.AggregationException;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.random.RandomBatch;
import dna.util.Config;
import dna.util.Log;
import dna.util.Log.LogLevel;
import dna.util.MathHelper;
import dna.util.Timer;

public class SingleTests {
	EnumMap<ListType, Class<? extends IDataStructure>> listTypes;

	@Before
	public void init() {
		listTypes = new EnumMap<ListType, Class<? extends IDataStructure>>(
				ListType.class);
		for (ListType lt : ListType.values()) {
			listTypes.put(lt, DArrayList.class);
		}
	}

	@Test
	public void metricTest() throws AggregationException, IOException,
			MetricNotApplicableException {
		Config.overwrite("PROFILER_ACTIVATED", "true");
		LogLevel oldLogLevel = Log.getLogLevel();
		Log.setLogLevel(LogLevel.warn);

		GraphDataStructure gds = new GraphDataStructure(listTypes,
				DirectedNode.class, DirectedEdge.class);
		GraphGenerator gg = new RandomGraph(gds, 40, 40);
		BatchGenerator batchGen = new RandomBatch(5, 5, 5, 5);
		// TODO add again after change to new metrics
		// Metric[] metrics = new Metric[] { new DegreeDistributionU(),
		// new DirectedClusteringCoefficientU() };
		// Series s = new Series(gg, batchGen, metrics, "./graphs/", "test");
		// s.generate(2, 1);
		Log.setLogLevel(oldLogLevel);
	}

	@Test
	public void testMathHelper() {
		assertEquals(0, MathHelper.parseInt("0"));
		assertEquals(1, MathHelper.parseInt("1"));
		assertEquals(10, MathHelper.parseInt("10"));
		assertEquals(100, MathHelper.parseInt("100"));

		assertEquals(1, MathHelper.parseInt("1abc###"));
		assertEquals(1, MathHelper.parseInt("1abc###0012"));
		assertEquals(10, MathHelper.parseInt("10abc###0012"));
		assertEquals(10, MathHelper.parseInt("10.abc###0012"));
	}

	@Ignore
	@Test
	public void performanceTester() {
		GraphDataStructure gds = new GraphDataStructure(listTypes,
				DirectedWeightedNode.class, DirectedWeightedEdge.class);

		int limit = 1000000;
		Node n;

		Timer t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			n = gds.newWeightedNode(i, new DoubleWeight(2d));
		}
		System.out.println("Generating " + limit + " node instances via gds: "
				+ t.end());

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			n = new DirectedWeightedNode(i, new DoubleWeight(1d), gds);
		}
		System.out.println("Generating " + limit
				+ " node instances via direct constructor: " + t.end());

		DirectedWeightedEdge e;
		DirectedNode n1 = new DirectedWeightedNode(1, gds);
		DirectedWeightedNode n2 = new DirectedWeightedNode(2, gds);

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			e = (DirectedWeightedEdge) gds.newWeightedEdge(n1, n2,
					new DoubleWeight(1d));
		}
		System.out.println("Generating " + limit
				+ " directed edge instances via gds: " + t.end());

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			e = new DirectedWeightedEdge(n1, n2, new DoubleWeight(1d));
		}
		System.out.println("Generating " + limit
				+ " edge instances via direct constructor: " + t.end());

		gds.setEdgeType(UndirectedWeightedEdge.class);
		UndirectedWeightedEdge ue;
		UndirectedNode un1 = new UndirectedWeightedNode(1, gds);
		UndirectedWeightedNode un2 = new UndirectedWeightedNode(2, gds);

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			ue = (UndirectedWeightedEdge) gds.newWeightedEdge(un1, un2,
					new DoubleWeight(1d));
		}
		System.out.println("Generating " + limit
				+ " undirected edge instances via gds: " + t.end());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void checkUnorderedInsertionDoesNotCauseExceptions() {
		/**
		 * This test case is somehow tricky. This is where the bug occured: a
		 * generator did not generate nodes in ascending index order. Thus, the
		 * node list contained entries eg. in the order 1, 2, 5, 4, 3. Getting
		 * the node with index 5 by it's index looked at position 5. At this
		 * place, the node with index 3 is stored. As all index-based lists (at
		 * the point of writing it: DArrayList, DHashArrayList, DLinkedLists)
		 * dealed with empty positions, but not with un-ordered lists, all nodes
		 * above position 5 would get checked for having the node with index 5,
		 * but not the ones below position 5
		 */
		for (Class dsClass : ClassPointers.dataStructures) {
			if (!INodeListDatastructureReadable.class.isAssignableFrom(dsClass))
				continue;
			if (dsClass == DEmpty.class)
				continue;

			EnumMap<ListType, Class<? extends IDataStructure>> list = GraphDataStructure
					.getList(ListType.GlobalNodeList, dsClass,
							ListType.GlobalEdgeList, dsClass);
			GraphDataStructure gds = new GraphDataStructure(list,
					DirectedNode.class, DirectedEdge.class);
			INodeListDatastructureReadable ds = (INodeListDatastructureReadable) gds
					.newList(ListType.GlobalNodeList);

			int nodeIndex = 5;
			Node n1 = gds.newNodeInstance(nodeIndex);
			ds.add(n1);
			assertNotNull("DS.get yields NULL on " + dsClass, ds.get(nodeIndex));

			nodeIndex = 2;
			Node n2 = gds.newNodeInstance(nodeIndex);
			ds.add(n2);
			assertNotNull("DS.get yields NULL on " + dsClass, ds.get(nodeIndex));

			nodeIndex = 1;
			Node n3 = gds.newNodeInstance(nodeIndex);
			ds.add(n3);
			assertNotNull("DS.get yields NULL on " + dsClass, ds.get(nodeIndex));
		}
	}

	@Test
	public void equalityForDirectedEdgeDummies() {
		GraphDataStructure gds = new GraphDataStructure(listTypes,
				DirectedNode.class, DirectedEdge.class);
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);

		Edge eReal = gds.newEdgeInstance(n1, n2);
		Edge eDummy = gds.getDummyEdge(n1.getIndex(), n2.getIndex());
		assertEquals(eReal, eDummy);
		assertEquals(eReal.getHashString(), eDummy.getHashString());

		n1 = gds.newNodeInstance(23);
		n2 = gds.newNodeInstance(42);

		eDummy = gds.getDummyEdge(n1.getIndex(), n2.getIndex());
		assertFalse(eReal.equals(eDummy));
		assertFalse(eDummy.equals(eReal));
		assertTrue(eDummy.equals(eDummy));

		eReal = gds.newEdgeInstance(n1, n2);
		assertEquals(eReal, eDummy);
		assertEquals(eReal.getHashString(), eDummy.getHashString());

		eReal = gds.newEdgeInstance(n2, n1);
		assertNotEquals(eReal, eDummy);
		assertNotEquals(eReal.getHashString(), eDummy.getHashString());
	}

	@Test
	public void equalityForUndirectedEdgeDummies() {
		GraphDataStructure gds = new GraphDataStructure(listTypes,
				UndirectedNode.class, UndirectedEdge.class);
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);

		Edge eReal = gds.newEdgeInstance(n1, n2);
		Edge eDummy = gds.getDummyEdge(n1.getIndex(), n2.getIndex());
		assertEquals(eReal, eDummy);

		n1 = gds.newNodeInstance(23);
		n2 = gds.newNodeInstance(42);

		eDummy = gds.getDummyEdge(n1.getIndex(), n2.getIndex());
		assertFalse(eReal.equals(eDummy));
		assertFalse(eDummy.equals(eReal));
		assertTrue(eDummy.equals(eDummy));

		eReal = gds.newEdgeInstance(n2, n1);
		assertEquals(eReal, eDummy);
		assertEquals(eReal.getHashString(), eDummy.getHashString());
	}

	@Test
	public void checkDummyEdgeContainsBehavesLikeGet() {
		DHashTable ht = new DHashTable(ListType.GlobalEdgeList, Edge.class);

		GraphDataStructure gds = new GraphDataStructure(listTypes,
				UndirectedNode.class, UndirectedEdge.class);
		Graph g = gds.newGraphInstance("", 0, 2, 1);

		Node n1 = gds.newNodeInstance(1);
		g.addNode(n1);

		Node n2 = gds.newNodeInstance(2);
		g.addNode(n2);

		Edge eReal = gds.newEdgeInstance(n1, n2);
		g.addEdge(eReal);

		ht.add(eReal);

		assertTrue(ht.contains(eReal));

		Edge dummyEdge = g.getEdge(n1, n2);
		assertTrue(ht.contains(dummyEdge));

		dummyEdge = g.getEdge(n2, n1);
		assertTrue(ht.contains(dummyEdge));
	}

	@Test
	public void checkDuplicateHashesInMultimap() {
		GraphDataStructure undirected = new GraphDataStructure(
				GraphDataStructure.getList(ListType.GlobalNodeList,
						DHashSet.class, ListType.GlobalEdgeList,
						DLinkedHashMultimap.class, ListType.LocalEdgeList,
						DHashSet.class), UndirectedNode.class,
				UndirectedEdge.class);

		Node n1 = undirected.newNodeInstance(266);
		Node n2 = undirected.newNodeInstance(80264);
		Node n3 = undirected.newNodeInstance(267);
		Node n4 = undirected.newNodeInstance(14728);

		Edge e1 = undirected.newEdgeInstance(n1, n2);
		Edge e2 = undirected.newEdgeInstance(n3, n4);

		assertEquals(e1.getHashString(), e2.getHashString());

		IDataStructure list = undirected.newList(ListType.GlobalEdgeList);
		assertTrue(list.add(e1));
		assertTrue(list.contains(e1));

		assertFalse(list.contains(e2));
		assertTrue(list.add(e2));
		assertTrue(list.contains(e2));
	}
}
