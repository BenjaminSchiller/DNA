package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import dna.graph.IWeighted;
import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.edges.DirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.RandomGraphGenerator;
import dna.graph.nodes.DirectedDoubleWeightedNode;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
import dna.metrics.clusterCoefficient.OpenTriangleClusteringCoefficientUpdate;
import dna.metrics.degree.DegreeDistributionUpdate;
import dna.profiler.GraphProfiler;
import dna.series.AggregationException;
import dna.series.Series;
import dna.updates.BatchGenerator;
import dna.updates.directed.RandomDirectedBatch;
import dna.util.MathHelper;
import dna.util.parameters.Parameter;

public class SingleTests {
	
	@Test
	public void metricTest() throws AggregationException, IOException,
			MetricNotApplicableException {
		GraphProfiler.activate();
	
	GraphDataStructure gds = new GraphDataStructure(DArrayList.class,
			DArrayList.class, DArrayList.class, DirectedNode.class,
			DirectedEdge.class);
	GraphGenerator gg = new RandomGraphGenerator("test",
			new Parameter[] {}, gds, 1, 40, 40);
	BatchGenerator<DirectedNode, DirectedEdge> batchGen = new RandomDirectedBatch(
			5, 5, 5, 5, gds);
	Metric[] metrics = new Metric[] { new DegreeDistributionUpdate(),
			new OpenTriangleClusteringCoefficientUpdate() };
	Series s = new Series(gg, batchGen, metrics, "./graphs/", "test");
	s.generate(3, 3);
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
		GraphDataStructure gds = new GraphDataStructure(DArray.class,
				DArray.class, DArray.class, DirectedDoubleWeightedNode.class, DirectedDoubleWeightedEdge.class);
		IWeighted n = gds.newWeightedNode(1, 1d);
		assertTrue(n instanceof DirectedDoubleWeightedNode);
		assertEquals(1d, n.getWeight());

		boolean caughtException = false;
		try {
			IWeighted n2 = gds.newWeightedNode(2, "Test");
		} catch (RuntimeException e) {
			caughtException = true;
		}
		assertTrue("Node n2 instantiated with incompatible signature", caughtException);
	}
}
