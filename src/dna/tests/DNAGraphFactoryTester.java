package dna.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dna.graph.BlueprintsGraph;
import dna.graph.DNAGraphFactory;
import dna.graph.Graph;
import dna.graph.DNAGraphFactory.DNAGraphType;
import dna.graph.IGDBGraph;
import dna.graph.IGraph;
import dna.graph.datastructures.GDS;
import dna.util.Config;

@RunWith(Parameterized.class)
public class DNAGraphFactoryTester {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private DNAGraphFactory.DNAGraphType chosenGDB;

	@Parameters(name = "{index}: Test graph database {0}")
	public static ArrayList<Object> data() {
		ArrayList<Object> result = new ArrayList<>();
		for (DNAGraphType graph : DNAGraphType.values()) {
			if (graph.equals(DNAGraphType.CONFIG))
				continue;
			result.add(new Object[] { graph });
		}

		return result;
	}

	public DNAGraphFactoryTester(DNAGraphFactory.DNAGraphType gdb) {
		this.chosenGDB = gdb;
	}

	@Test
	public void createGraph_Success() {
		IGraph graph = null;

		Config.overwrite("GF_GRAPHTYPE", chosenGDB.toString());

		if (chosenGDB == DNAGraphType.DNA) {
			graph = DNAGraphFactory.newGraphInstance(chosenGDB,
					chosenGDB.toString(), 0, GDS.directed());
			assertNotEquals(graph, null);
			assertTrue(graph instanceof Graph);
		} else if (chosenGDB == DNAGraphType.BITSY_DURABLE
				|| chosenGDB == DNAGraphType.BITSY_NON_DURABLE
				|| chosenGDB == DNAGraphType.NEO4J2
				|| chosenGDB == DNAGraphType.ORIENTDBNOTX
				|| chosenGDB == DNAGraphType.TINKERGRAPH) {
			graph = DNAGraphFactory.newGraphInstance(chosenGDB,
					chosenGDB.toString(), 0, GDS.directedGDB());
			assertNotEquals(graph, null);
			assertTrue(graph instanceof BlueprintsGraph);
			assertTrue(graph instanceof IGDBGraph);
			assertEquals(((IGDBGraph<?>) graph).getGraphDatabaseType(), chosenGDB);
		}

		assertEquals(graph.getInstanceType(), this.chosenGDB);
	}

	@Test
	public void createGraph_Fail() throws RuntimeException {
		Config.overwrite("GF_GRAPHTYPE", chosenGDB.toString());

		try {

			if (chosenGDB == DNAGraphType.DNA) {
				DNAGraphFactory.newGraphInstance(chosenGDB,
						chosenGDB.toString(), 0, GDS.directedGDB());
			} else if (chosenGDB == DNAGraphType.BITSY_DURABLE
					|| chosenGDB == DNAGraphType.BITSY_NON_DURABLE
					|| chosenGDB == DNAGraphType.NEO4J2
					|| chosenGDB == DNAGraphType.ORIENTDBNOTX
					|| chosenGDB == DNAGraphType.TINKERGRAPH) {
				DNAGraphFactory.newGraphInstance(chosenGDB,
						chosenGDB.toString(), 0, GDS.directed());
			}
		} catch (Exception e) {
				assertTrue(e instanceof RuntimeException);
				assertEquals(e.getMessage(), "The chosen graph type and the node type for the graph datastructure are incompatible.");
		}
	}
}
