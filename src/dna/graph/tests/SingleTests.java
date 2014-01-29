package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.EnumMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedDoubleWeightedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.RandomGraphGenerator;
import dna.graph.nodes.DirectedDoubleWeightedNode;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.UndirectedDoubleWeightedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IWeighted;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
import dna.metrics.clusterCoefficient.DirectedClusteringCoefficientU;
import dna.metrics.degree.DegreeDistributionU;
import dna.profiler.Profiler;
import dna.series.AggregationException;
import dna.series.Series;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.random.RandomBatch;
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
		Profiler.activate();

		GraphDataStructure gds = new GraphDataStructure(listTypes,
				DirectedNode.class, DirectedEdge.class);
		GraphGenerator gg = new RandomGraphGenerator(gds, 40, 40);
		BatchGenerator batchGen = new RandomBatch(5, 5, 5, 5);
		Metric[] metrics = new Metric[] { new DegreeDistributionU(),
				new DirectedClusteringCoefficientU() };
		Series s = new Series(gg, batchGen, metrics, "./graphs/", "test");
		s.generate(2, 1);
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

	@SuppressWarnings("rawtypes")
	@Test
	public void checkWeightedNode() {
		GraphDataStructure gds = new GraphDataStructure(listTypes,
				DirectedDoubleWeightedNode.class,
				DirectedDoubleWeightedEdge.class);
		IWeighted n = gds.newWeightedNode(1, 1d);
		assertTrue(n instanceof DirectedDoubleWeightedNode);
		assertEquals(1d, n.getWeight());

		boolean caughtException = false;
		try {
			IWeighted n2 = gds.newWeightedNode(2, "Test");
		} catch (RuntimeException e) {
			caughtException = true;
		}
		assertTrue("Node n2 instantiated with incompatible signature",
				caughtException);
	}

	@Ignore
	@Test
	public void performanceTester() {
		GraphDataStructure gds = new GraphDataStructure(listTypes,
				DirectedDoubleWeightedNode.class,
				DirectedDoubleWeightedEdge.class);

		int limit = 1000000;
		IWeightedNode n;

		Timer t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			n = gds.newWeightedNode(i, 1d);
		}
		System.out.println("Generating " + limit + " node instances via gds: "
				+ t.end());

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			n = new DirectedDoubleWeightedNode(i, 1d, gds);
		}
		System.out.println("Generating " + limit
				+ " node instances via direct constructor: " + t.end());

		DirectedDoubleWeightedEdge e;
		DirectedNode n1 = new DirectedDoubleWeightedNode(1, gds);
		DirectedDoubleWeightedNode n2 = new DirectedDoubleWeightedNode(2, gds);

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			e = (DirectedDoubleWeightedEdge) gds.newWeightedEdge(n1, n2, 1d);
		}
		System.out.println("Generating " + limit
				+ " directed edge instances via gds: " + t.end());

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			e = new DirectedDoubleWeightedEdge(n1, n2, 1d);
		}
		System.out.println("Generating " + limit
				+ " edge instances via direct constructor: " + t.end());

		gds.setEdgeType(UndirectedDoubleWeightedEdge.class);
		UndirectedDoubleWeightedEdge ue;
		UndirectedNode un1 = new UndirectedDoubleWeightedNode(1, gds);
		UndirectedDoubleWeightedNode un2 = new UndirectedDoubleWeightedNode(2,
				gds);

		t = new Timer("Test");
		for (int i = 0; i < limit; i++) {
			ue = (UndirectedDoubleWeightedEdge) gds.newWeightedEdge(un1, un2,
					1d);
		}
		System.out.println("Generating " + limit
				+ " undirected edge instances via gds: " + t.end());
	}
}
