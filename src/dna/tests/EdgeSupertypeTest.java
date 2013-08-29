package dna.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.datastructures.DArray;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;

@RunWith(Parameterized.class)
public class EdgeSupertypeTest {
	private GraphDataStructure gds;
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;

	public EdgeSupertypeTest(Class<? extends Node> nodeType, Class<? extends Edge> edgeType) {
		this.gds = new GraphDataStructure(DArray.class, DArray.class, DArray.class, nodeType);
		this.nodeType = nodeType;
		this.edgeType = edgeType;
	}

	@Parameterized.Parameters(name = "{0} {1}")
	public static Collection<Object> testPairs() {
		ArrayList<Object> result = new ArrayList<>();
		for (Class<?> nodeType : GlobalTestParameters.nodeTypes) {
			for (Class<?> edgeType : GlobalTestParameters.edgeTypes) {
				result.add(new Object[] { nodeType, edgeType });
			}
		}
		System.out.println(result);
		return result;
	}

	@Test
	public void testOtherEdgeType() {
		assumeTrue((DirectedNode.class.isAssignableFrom(nodeType) && DirectedEdge.class.isAssignableFrom(edgeType))
				|| (UndirectedNode.class.isAssignableFrom(nodeType) && UndirectedEdge.class.isAssignableFrom(edgeType)));

		this.gds.setEdgeType(edgeType);

		Node n0 = this.gds.newNodeInstance(0);
		Node n1 = this.gds.newNodeInstance(1);

		Edge e = this.gds.newEdgeInstance(n0, n1);
		assertNotNull(e);
	}
}
