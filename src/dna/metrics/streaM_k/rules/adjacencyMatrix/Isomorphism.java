package dna.metrics.streaM_k.rules.adjacencyMatrix;

import org.jgrapht.Graph;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * 
 * Util class for determining weather two given graph represented as adajcency
 * matrices are isomorph to each other.
 * 
 * This tests if done using the GraphIsomorphismInspector implementation from
 * the jGraph library.
 * 
 * @author benni
 *
 */
public class Isomorphism {

	/**
	 * 
	 * @param am1
	 *            first adjacency matrix
	 * @param am2
	 *            second adjacency matrix
	 * @return true if the given undirected graphs are isomorph to each other
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isIsomorph(UndirectedAdjacencyMatrix am1,
			UndirectedAdjacencyMatrix am2) {

		GraphIsomorphismInspector iso = null;
		Graph<Integer, DefaultEdge> g1 = getGraph(am1);
		Graph<Integer, DefaultEdge> g2 = getGraph(am2);
		try {
			iso = AdaptiveIsomorphismInspectorFactory
					.createIsomorphismInspector(g1, g2);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		return iso.hasNext();
	}

	private static Graph<Integer, DefaultEdge> getGraph(
			UndirectedAdjacencyMatrix am) {
		Graph<Integer, DefaultEdge> g = new DefaultDirectedGraph<Integer, DefaultEdge>(
				DefaultEdge.class);
		for (int i = 0; i < am.getNodeCount(); i++) {
			g.addVertex(i);
		}
		for (int i = 0; i < am.getNodeCount(); i++) {
			for (int j = 0; j < am.getNodeCount(); j++) {
				if (am.hasEdge(i, j)) {
					g.addEdge(i, j);
				}
			}
		}
		return g;
	}

}
