package dna.tests;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;

import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.jna.platform.win32.WinUser.BLENDFUNCTION;

import dna.graph.BlueprintsGraph;
import dna.graph.DNAGraphFactory;
import dna.graph.Graph;
import dna.graph.DNAGraphFactory.DNAGraphType;
import dna.graph.IGraph;
import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.canonical.CliqueGraph;
import dna.graph.generators.canonical.RingGraph;
import dna.graph.generators.canonical.RingStarGraph;
import dna.graph.generators.canonical.StarGraph;
import dna.graph.generators.random.RandomGraph;
import dna.graph.nodes.DirectedBlueprintsNode;
import dna.graph.nodes.Node;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeighted;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;
import dna.util.Config;
import dna.util.IOUtils;
import dna.util.Rand;

@RunWith(Parameterized.class)
public class GraphDatabaseTester {

	private IGraph graph;
	private GraphDataStructure gds;
	private DNAGraphFactory.DNAGraphType chosenGDB;

	private enum TestGDS {
		DIRECTEDGDB(), DIRECTEDVGDB(), DIRECTEDEGDB(), DIRECTEDVEGDB(), 
		UNDIRECTEDGDB(), UNDIRECTEDVGDB(), UNDIRECTEDEGDB(), UNDIRECTEDVEGDB();
		
		public GraphDataStructure getGDS()
		{
			switch(this)
			{
			case DIRECTEDEGDB:
				return GDS.directedEGDB(IntWeight.class,
						WeightSelection.RandPos100);
			case DIRECTEDGDB:
				return GDS.directedGDB();
			case DIRECTEDVEGDB:
				return GDS.directedVEGDB(DoubleWeight.class, WeightSelection.RandTrim1, IntWeight.class,
						WeightSelection.RandPos100);
			case DIRECTEDVGDB:
				return GDS.directedVGDB(DoubleWeight.class, WeightSelection.RandTrim1);
			case UNDIRECTEDEGDB:
				return GDS.undirectedEGDB(IntWeight.class,
						WeightSelection.RandPos100);
			case UNDIRECTEDGDB:
				return GDS.undirectedGDB();
			case UNDIRECTEDVEGDB:
				return GDS.undirectedVEGDB(DoubleWeight.class, WeightSelection.RandTrim1, IntWeight.class,
						WeightSelection.RandPos100);
			case UNDIRECTEDVGDB:
				return GDS.undirectedVGDB(DoubleWeight.class, WeightSelection.RandTrim1);
			default:
				return null;			
			}
		}
	}

	@Parameters(name = "{index}: Test graph database {0} with {1}")
	public static ArrayList<Object> data() {
		ArrayList<Object> result = new ArrayList<>();	
		for (DNAGraphType graph : DNAGraphType.values()) {
			if (graph == DNAGraphType.DNA || graph == DNAGraphType.CONFIG)
				continue;
			for (TestGDS gds : TestGDS.values())
			{
				result.add(new Object[] {graph, gds});
			}
		}

		return result;
	}

	public GraphDatabaseTester(DNAGraphFactory.DNAGraphType gdb, TestGDS testgds) {
		this.chosenGDB = gdb;
		this.gds = testgds.getGDS();
	}

	@Before
	public void setUp() {
		this.graph = this.gds.newGraphDBInstanceOfType(
				this.chosenGDB.toString(), 0, this.chosenGDB, 1000, true,
				SystemUtils.getJavaIoTmpDir().getAbsolutePath() + IOUtils.getPathForOS("/GDB/" + Rand.rand.nextInt() + "/"));
	}

	@After
	public void tearDown() {
		if (this.graph != null) {
			this.graph.close();
		}

		this.graph = null;
	}

	@Test
	public void addNodeByID() {
		Node n = gds.newNodeInstance(42);

		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(n);
		assertTrue(graph.containsNode(n));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}

	@Test
	public void addNodeByString() {
		String nodeString = "42";
		if (graph.getGraphDatastructures().createsWeightedNodes()) {
			nodeString += Weight.WeightDelimiter + "1";
		}

		Node n = gds.newNodeInstance(nodeString);

		assertEquals(-1, graph.getMaxNodeIndex());
		graph.addNode(n);
		assertTrue(graph.containsNode(n));
		assertEquals(1, graph.getNodeCount());
		assertEquals(42, graph.getMaxNodeIndex());
	}

	@Test
	public void addEdgeByID() {
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		Edge e = null;
		if (graph.getGraphDatastructures().createsWeightedEdges()) {
			e = (Edge) gds.newWeightedEdge(n1, n2, new IntWeight(1));
		} else {
			e = gds.newEdgeInstance(n1, n2);
		}
		graph.addEdge(e);
		
		Edge e2 = graph.getEdge(n1,n2);
		
		assertEquals(e, e2);

		assertTrue(n1.hasEdge(e));
		assertTrue(n2.hasEdge(e));
	}

	@Test
	public void addEdgeByString() {
		assumeTrue(gds.isReadable());

		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		graph.addNode(n1);
		graph.addNode(n2);

		String edgeString;
		if (graph.isDirected()) {
			edgeString = "1" + Config.get("EDGE_DIRECTED_DELIMITER") + "2";
		} else {
			edgeString = "1" + Config.get("EDGE_UNDIRECTED_DELIMITER") + "2";
		}
		if (gds.createsWeightedEdges()) {
			edgeString += Weight.WeightDelimiter + "1";
		}

		Edge e = gds.newEdgeInstance(edgeString, graph);
		
		graph.addEdge(e);
		n1.addEdge(e);
		n2.addEdge(e);

		assertTrue(n1.hasEdge(e));
		assertTrue(n2.hasEdge(e));
	}

	@Test
	public void removeNode() {
		Node dummy = gds.newNodeInstance(0);
		Node dummy2 = gds.newNodeInstance(1);
		Node dummy3 = gds.newNodeInstance(2);

		assertEquals(-1, graph.getMaxNodeIndex());
		assertTrue(graph.addNode(dummy));
		assertTrue(graph.addNode(dummy2));

		assertEquals(1, graph.getMaxNodeIndex());
		assertTrue(graph.removeNode(dummy));

		assertEquals(1, graph.getMaxNodeIndex());

		assertFalse(graph.containsNode(dummy3));
		assertFalse(graph.removeNode(dummy3));

		assertTrue(graph.removeNode(dummy2));
		assertEquals(0, graph.getNodeCount());
		assertEquals(-1, graph.getMaxNodeIndex());
	}

	@Test
	public void nameAndTimestamp() {
		java.util.Date date = new java.util.Date();
		long ts = date.getTime();
		String name = Long.toString(ts);
		IGraph g = new Graph(name, ts, this.gds);
		assertEquals(name, g.getName());
		assertEquals(ts, g.getTimestamp());
	}

	@Test
	public void graphEqualityForBasics() {
		long timestamp = 1L;
		
		IGraph g1 = new BlueprintsGraph("N", timestamp, this.gds, this.chosenGDB, 1000, true, IOUtils.getPathForOS("data/GDB/1/"));
		IGraph g2 = new BlueprintsGraph("N", timestamp, this.gds, this.chosenGDB, 1000, true, IOUtils.getPathForOS("data/GDB/2/"));
		IGraph g3 = new BlueprintsGraph("N", timestamp + 1, this.gds, this.chosenGDB, 1000, true, IOUtils.getPathForOS("data/GDB/3/"));
		IGraph g4 = new BlueprintsGraph("O", timestamp, this.gds, this.chosenGDB, 1000, true, IOUtils.getPathForOS("data/GDB/4/"));		
		assertEquals(g1, g2);
		assertNotEquals(g1, g3);
		assertNotEquals(g2, g3);
		assertNotEquals(g1, g4);
		assertNotEquals(g2, g4);
		assertNotEquals(g3, g4);
		
		g1.close();
		g2.close();
		g3.close();
		g4.close();
	}

	@Test
	public void graphEqualityForNodes() {
		long timestamp = 1L;

		IGraph g1 = new BlueprintsGraph("N", timestamp, this.gds, this.chosenGDB, 1000, true, IOUtils.getPathForOS("data/GDB/1/"));
		IGraph g2 = new BlueprintsGraph("N", timestamp, this.gds, this.chosenGDB, 1000, true, IOUtils.getPathForOS("data/GDB/2/"));

		Node g1n1 = this.gds.newNodeInstance(42);
		Node g1n2 = this.gds.newNodeInstance(23);
		Node g2n1 = this.gds.newNodeInstance(42);
		Node g2n2 = this.gds.newNodeInstance(23);

		if (gds.createsWeightedNodes()) {
			((IWeighted) g1n1).setWeight(new IntWeight(1));
			((IWeighted) g1n2).setWeight(new IntWeight(1));
			((IWeighted) g2n1).setWeight(new IntWeight(1));
			((IWeighted) g2n2).setWeight(new IntWeight(1));
		}

		assertTrue(g1.addNode(g1n1));
		assertNotEquals(g1, g2);

		assertTrue(g2.addNode(g2n1));
		assertEquals(g1, g2);

		assertFalse(g1.removeNode(g1n2));
		assertEquals(g1, g2);

		assertFalse(g2.removeNode(g2n2));
		assertEquals(g1, g2);

		assertTrue(g1.removeNode(g1n1));
		assertNotEquals(g1, g2);

		assertTrue(g2.removeNode(g2n1));
		assertEquals(g1, g2);
		
		g1.close();
		g2.close();
	}
	
	@Test
	public void graphGenerator()
	{
		IGraph graph;
		
		int n = 10;
		int e = (n * (n-1));
		if (gds.createsUndirected())
		{
			e = e /2;
		}
		
		//Random
		RandomGraph random = new RandomGraph(gds, n, e);
		graph = random.generate();
		assertEquals(graph.getNodeCount(), n);
		assertEquals(((BlueprintsGraph)graph).getNodeCountFromDB(), n);
		assertEquals(graph.getEdgeCount(), e);
		assertEquals(((BlueprintsGraph)graph).getEdgeCountFromDB(), e);
		graph.close();

		StarGraph star = new StarGraph(gds, n);
		graph = star.generate();
		assertEquals(graph.getNodeCount(), n);
		assertEquals(((BlueprintsGraph)graph).getNodeCountFromDB(), n);
		assertEquals(graph.getEdgeCount(), (n - 1));
		assertEquals(((BlueprintsGraph)graph).getEdgeCountFromDB(), (n - 1));
		graph.close();
		
		RingGraph ring = new RingGraph(gds, n);
		graph = ring.generate();
		assertEquals(graph.getNodeCount(), n);
		assertEquals(((BlueprintsGraph)graph).getNodeCountFromDB(), n);
		assertEquals(graph.getEdgeCount(), n);
		assertEquals(((BlueprintsGraph)graph).getEdgeCountFromDB(), n);
		graph.close();
		
		RingStarGraph ringstar =  new RingStarGraph(gds, n);
		graph = ringstar.generate();
		assertEquals(graph.getNodeCount(), n);
		assertEquals(((BlueprintsGraph)graph).getNodeCountFromDB(), n);
		assertEquals(graph.getEdgeCount(), ((2 * n ) - 2));
		assertEquals(((BlueprintsGraph)graph).getEdgeCountFromDB(), ((2 * n ) - 2));
		graph.close();
		
		CliqueGraph clique = new CliqueGraph(gds, n);
		graph = clique.generate();
		assertEquals(graph.getNodeCount(), n);
		assertEquals(((BlueprintsGraph)graph).getNodeCountFromDB(), n);
		graph.close();
		
		
		
		
	}
}
