package dna.graph.generators.timestamped;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.Log;
import dna.util.parameters.Parameter;

/**
 * 
 * Reads a graph from a file using the specified TimestampedReader. Two modes
 * for generating a graph from a sub-set of all edges are available: TIMESTAMP
 * and EDGE_COUNT. For TIMESTAMP, all edges with a timestamp less than of equal
 * to the specified timestamp are included in the initial graph. For EDGE_COUNT,
 * all edges are sorted by ascending timestamp and the specified number is used
 * for the initial graph.
 * 
 * @author benni
 *
 */
public class TimestampedGraph extends GraphGenerator {

	public enum TimestampedGraphType {
		TIMESTAMP, EDGE_COUNT
	}

	private TimestampedReader reader;

	private TimestampedGraphType type;

	private long parameter;

	/**
	 * 
	 * @param reader
	 *            timestamped reader to use
	 * @param gds
	 *            GDS
	 * @param type
	 *            TIMESTAMP or EDGE_COUNT
	 * @param parameter
	 *            timestamp or edge count
	 * @param nodesInit
	 *            number of nodes to init the graph with
	 * @param edgesInit
	 *            number of edges to init the graph with
	 */
	public TimestampedGraph(TimestampedReader reader, GraphDataStructure gds,
			TimestampedGraphType type, long parameter, int nodesInit,
			int edgesInit) {
		super(reader.getName(), new Parameter[] {}, gds, 0, nodesInit,
				edgesInit);
		this.reader = reader;
		this.type = type;
		this.parameter = parameter;
	}

	/**
	 * 
	 * @param reader
	 *            timestamped reader to use
	 * @param gds
	 *            GDS
	 * @param type
	 *            TIMESTAMP or EDGE_COUNT
	 * @param parameter
	 *            timestamp or edge count
	 */
	public TimestampedGraph(TimestampedReader reader, GraphDataStructure gds,
			TimestampedGraphType type, long parameter) {
		this(reader, gds, type, parameter, 0, 0);
	}

	@Override
	public Graph generate() {
		try {
			this.reader.read();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		Graph g = this.newGraphInstance();
		TimestampedEdge e = null;
		int duplicateEdges = 0;
		int loops = 0;
		while ((e = this.next(g)) != null) {
			if (e.getFrom() == e.getTo()) {
				loops++;
				continue;
			}
			if (g.containsEdge(e.getFrom(), e.getTo())) {
				duplicateEdges++;
				continue;
			}

			Node from = g.getNode(e.getFrom());
			if (from == null) {
				from = this.gds.newNodeInstance(e.getFrom());
				g.addNode(from);
			}
			Node to = g.getNode(e.getTo());
			if (to == null) {
				to = this.gds.newNodeInstance(e.getTo());
				g.addNode(to);
			}
			Edge edge = this.gds.newEdgeInstance(from, to);
			edge.connectToNodes();
			g.addEdge(edge);

			g.setTimestamp(e.getTimestamp());
		}
		Log.debug("removed " + duplicateEdges + " duplicate edges and " + loops
				+ " loops");
		return g;
	}

	private TimestampedEdge next(Graph g) {
		switch (this.type) {
		case EDGE_COUNT:
			if (g.getEdgeCount() >= this.parameter) {
				return null;
			}
			return this.reader.next();
		case TIMESTAMP:
			return this.reader.next(this.parameter);
		default:
			return null;
		}
	}
}
