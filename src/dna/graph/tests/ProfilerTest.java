package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.Metric.ApplicationType;
import dna.metrics.Metric.MetricType;
import dna.profiler.GraphProfiler;
import dna.profiler.MetricsProfiler;
import dna.profiler.GraphProfiler.ProfilerType;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.Batch;
import dna.updates.Update;

@RunWith(Parameterized.class)
public class ProfilerTest {

	private GraphDataStructure gds;
	private Graph graph;
	private ApplicationType applicationType;
	private Metric metric;
	private String metricKey;

	public ProfilerTest(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType,
			ApplicationType applicationType) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.gds = new GraphDataStructure(nodeListType, graphEdgeListType,
				nodeEdgeListType, nodeType, edgeType);
		this.gds.setEdgeType(edgeType);
		this.graph = gds.newGraphInstance("ABC", 1L, 10, 10);
		this.applicationType = applicationType;
		GraphProfiler.activate();
		
		this.graph = generateGraph();
		metric = new TestMetric("test", this.applicationType, MetricType.unknown);
		metric.setGraph(graph);
		this.metricKey = metric.getName() + MetricsProfiler.initialAddition;
	}

	private Graph generateGraph() {
		Graph g = new Graph("test", 1, this.gds);
		
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		g.addNode(n1);
		g.addNode(n2);
		
		Edge e = gds.newEdgeInstance(n1, n2);
		e.connectToNodes();
		g.addEdge(e);
		
		return g;
	}
	
	@Before
	public void resetProfiler() {
		GraphProfiler.reset();
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1} {2} {3} {4} {5}")
	public static Collection<Object> testPairs() {
		ArrayList<Object> result = new ArrayList<>();
		Class nodeListType = null;
		for (Class loopNodeListType : GlobalTestParameters.dataStructures) {
			if (!(INodeListDatastructure.class
					.isAssignableFrom(loopNodeListType)))
				continue;
			nodeListType = loopNodeListType;
		}

		Class edgeListType = null;
		for (Class loopEdgeListType : GlobalTestParameters.dataStructures) {
			if (!(IEdgeListDatastructure.class
					.isAssignableFrom(loopEdgeListType)))
				continue;
			edgeListType = loopEdgeListType;
		}

		Class nodeEdgeListType = null;
		for (Class loopNodeEdgeListType : GlobalTestParameters.dataStructures) {
			if (!(IEdgeListDatastructure.class
					.isAssignableFrom(loopNodeEdgeListType)))
				continue;
			nodeEdgeListType = loopNodeEdgeListType;
		}

		for (Class nodeType : GlobalTestParameters.nodeTypes) {
			for (Class edgeType : GlobalTestParameters.edgeTypes) {
				if ((UndirectedEdge.class.isAssignableFrom(edgeType) && DirectedNode.class
						.isAssignableFrom(nodeType))
						|| (DirectedEdge.class.isAssignableFrom(edgeType) && UndirectedNode.class
								.isAssignableFrom(nodeType)))
					continue;

				for (ApplicationType a : ApplicationType.values()) {
					result.add(new Object[] { nodeListType, edgeListType,
							nodeEdgeListType, nodeType, edgeType, a });
				}
			}
		}

		return result;
	}

	@Test
	public void testContainsNodeGlobalIsCountedInMetric() {
		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsNodeGlobal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsNodeGlobal));
	}

	@Test
	public void testContainsNodeLocalIsCountedInMetric() {
		assumeTrue(graph.isDirected());

		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsNodeLocal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsNodeLocal));
	}

	@Test
	public void testContainsEdgeGlobalIsCountedInMetric() {
		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsEdgeGlobal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsEdgeGlobal));
	}

	@Test
	public void testContainsEdgeLocalIsCountedInMetric() {
		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsEdgeLocal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.ContainsEdgeLocal));
	}

	@Test
	public void testSizeNodeGlobalIsCountedInMetric() {
		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeNodeGlobal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeNodeGlobal));
	}

	@Test
	public void testSizeNodeLocalIsCountedInMetric() {
		assumeTrue(graph.isDirected());

		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeNodeLocal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeNodeLocal));
	}

	@Test
	public void testSizeEdgeGlobalIsCountedInMetric() {
		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeEdgeGlobal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeEdgeGlobal));
	}

	@Test
	public void testSizeEdgeLocalIsCountedInMetric() {
		assertEquals(0, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeEdgeLocal));
		metric.compute();
		assertEquals(1, GraphProfiler.getCount(metricKey,
				ProfilerType.SizeEdgeLocal));
	}

	@SuppressWarnings(value = { "rawtypes" })
	private class TestMetric extends Metric {
		public TestMetric(String name, ApplicationType type,
				MetricType metricType) {
			super(name, type, metricType);
		}

		@Override
		public boolean applyBeforeBatch(Batch b) {
			return false;
		}

		@Override
		public boolean applyAfterBatch(Batch b) {
			return false;
		}

		@Override
		public boolean applyBeforeUpdate(Update u) {
			return false;
		}

		@Override
		public boolean applyAfterUpdate(Update u) {
			return false;
		}

		@Override
		public boolean compute() {
			g.containsNode(mock(Node.class));
			
			Node n1 = g.getNode(1);
			Edge e = g.getRandomEdge();
			
			if ( n1 instanceof DirectedNode ) {
				DirectedNode dn1 = (DirectedNode) n1;
				dn1.hasNeighbor(dn1);
			}
			
			g.containsEdge(e);
			n1.hasEdge(e);
			
			g.getNodeCount();
			g.getEdgeCount();

			if ( n1 instanceof DirectedNode ) {
				DirectedNode dn1 = (DirectedNode) n1;
				dn1.getInDegree();
				dn1.getNeighborCount();
			}
			if ( n1 instanceof UndirectedNode ) {
				UndirectedNode un1 = (UndirectedNode) n1;
				un1.getDegree();
			}
			
			return false;
		}

		@Override
		protected void init_() {
		}

		@Override
		public void reset_() {
		}

		@Override
		protected Value[] getValues() {
			return null;
		}

		@Override
		protected Distribution[] getDistributions() {
			return null;
		}

		@Override
		protected NodeValueList[] getNodeValueLists() {
			return null;
		}

		@Override
		public boolean equals(Metric m) {
			return false;
		}

		@Override
		public boolean isApplicable(Graph g) {
			return false;
		}

		@Override
		public boolean isApplicable(Batch b) {
			return false;
		}

		@Override
		public boolean isComparableTo(Metric m) {
			return false;
		}
		
	}

}
