package dna.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;
import dna.util.Log;

/**
 * 
 * Reads a batch from a file using an old (legacy) file format.
 * 
 * @author benni
 * 
 */
public class LegacyBatchReader extends BatchReader {

	public static enum LegacyUpdateType {
		NODE_ADDITION, NODE_REMOVAL, NODE_WEIGHT, EDGE_ADDITION, EDGE_REMOVAL, EDGE_WEIGHT
	};

	public static final String typeDelimiter = "#";

	public static final String weightDelimiter = "--";

	public static Batch read(String dir, String filename, IGraph g) {
		Reader reader = null;

		try {
			reader = new Reader(dir, filename);

			reader.readKeyword(BatchWriter.fromKeyword);
			long from = reader.readLong();

			reader.readKeyword(BatchWriter.toKeyword);
			long to = reader.readLong();

			Batch b = new Batch(g.getGraphDatastructures(), from, to);

			reader.readKeyword(BatchWriter.updatesKeyword);

			HashMap<Integer, Node> addedNodes = new HashMap<Integer, Node>();
			HashSet<Edge> addedEdges = new HashSet<Edge>();

			String line = null;
			while ((line = reader.readString()) != null) {
				Update u = parseLine(line, g, addedNodes);

				b.add(u);
				if (u instanceof NodeAddition) {
					Node n = (Node) ((NodeAddition) u).getNode();
					addedNodes.put(n.getIndex(), n);
				} else if (u instanceof EdgeAddition) {
					Edge e = (Edge) ((EdgeAddition) u).getEdge();
					addedEdges.add(e);
				}
			}

			return b;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	protected static Update parseLine(String line, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		String[] temp = line.split(LegacyBatchReader.typeDelimiter);
		LegacyUpdateType type = LegacyUpdateType.valueOf(temp[0]);

		switch (type) {
		case EDGE_ADDITION:
			return new EdgeAddition(temp[1], g.getGraphDatastructures(), g,
					addedNodes);
		case EDGE_REMOVAL:
			int[] edge = LegacyBatchReader.parseEdge(temp[1].split("@")[0]);
			return new EdgeRemoval(edge[0], edge[1],
					g.getGraphDatastructures(), g);
		case EDGE_WEIGHT:
			String[] temp2 = temp[1].split("--");
			edge = LegacyBatchReader.parseEdge(temp2[0].split("@")[0]);
			IWeightedEdge e = (IWeightedEdge) g.getEdge(g.getNode(edge[0]),
					g.getNode(edge[1]));
			Weight w = g.getGraphDatastructures().newEdgeWeight(temp2[1]);
			return new EdgeWeight(e, w);
		case NODE_ADDITION:
			return new NodeAddition(temp[1], g.getGraphDatastructures());
		case NODE_REMOVAL:
			int index = Integer.parseInt(temp[1].split("@")[0]);
			return new NodeRemoval(g.getNode(index));
		case NODE_WEIGHT:
			temp2 = temp[1].split("--");
			index = Integer.parseInt(temp2[0].split("@")[0]);
			IWeightedNode node = (IWeightedNode) g.getNode(index);
			w = g.getGraphDatastructures().newEdgeWeight(temp2[1]);
			return new NodeWeight(node, w);
		default:
			Log.error("cannot parse (legacy) update from string: " + line);
			return null;
		}
	}

	private static int[] parseEdge(String edge) {
		String[] index;
		if (edge.contains("<->")) {
			index = edge.split("<->");
		} else if (edge.contains("->")) {
			index = edge.split("->");
		} else {
			Log.error("cannot parse (legacy) edge for ER from string: " + edge);
			return null;
		}
		return new int[] { Integer.parseInt(index[0]),
				Integer.parseInt(index[1]) };
	}
}
