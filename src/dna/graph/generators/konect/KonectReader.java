package dna.graph.generators.konect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.io.Reader;

public class KonectReader {

	public static enum KonectEdgeType {
		ADD, ADD_REMOVE, MULTI_UNWEIGHTED, RATING, RATING_ADD_ONE
	}

	public static enum KonectGraphType {
		TIMESTAMP, PROCESSED_EDGES, TOTAL_EDGES, TOTAL_NODES
	}

	public static enum KonectBatchType {
		TIMESTAMP, TIMESTAMPS, PROCESSED_EDGES, BATCH_SIZE, EDGE_GROWTH, NODE_GROWTH
	}

	public String dir;
	public String filename;
	public String name;

	protected Reader reader;

	public GraphDataStructure gds;

	public KonectEdgeType edgeType;

	public KonectReader(String dir, String filename, String name,
			GraphDataStructure gds, KonectEdgeType edgeType)
			throws FileNotFoundException {
		this.dir = dir;
		this.filename = filename;
		this.name = name;
		this.reader = new Reader(dir, filename);
		this.gds = gds;
		this.edgeType = edgeType;
	}

	private KonectEdge peek = null;

	public KonectEdge peek() {
		if (this.peek != null) {
			return this.peek;
		} else {
			try {
				this.peek = this.readEdge();
				return this.peek;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public KonectEdge readEdge() throws IOException {
		if (peek != null) {
			KonectEdge temp = this.peek;
			this.peek = null;
			return temp;
		}
		String line = this.reader.readString();
		if (line == null) {
			return null;
		} else {
			return new KonectEdge(line);
		}
	}

	protected int index;
	protected HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

	public Node getNode(int index) {
		if (this.nodes.containsKey(index)) {
			return this.nodes.get(index);
		}
		Node n = this.gds.newNodeInstance(this.index++);
		this.nodes.put(index, n);
		return n;
	}

	public void close() throws IOException {
		this.reader.close();
	}
}
