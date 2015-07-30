package dna.graph.generators.konect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.io.Reader;

public class KonectReader {

	public static enum KonectEdgeType {
		ADD, ADD_REMOVE, MULTI, WEIGHTED
	}

	public static enum KonectGraphType {
		PROCESSED_EDGES, TIMESTAMP, TOTAL_EDGES, TOTAL_NODES
	}

	public static enum KonectBatchType {
		BATCH_SIZE, EDGE_GROWTH, NODE_GROWTH, PROCESSED_EDGES, TIMESTAMP, TIMESTAMPS
	}

	public String dir;
	public String filename;
	public String name;

	protected Reader reader;

	public GraphDataStructure gds;

	public KonectEdgeType edgeType;
	public String edgeParameter;
	public boolean removeZeroDegreeNodes;

	public static final String separator = ";";
	protected double offset;
	protected double factor;

	protected ArrayDeque<KonectEdge> revert;
	protected long durability;

	public KonectReader(String dir, String filename, String name,
			GraphDataStructure gds, KonectEdgeType edgeType,
			String edgeParameter) throws FileNotFoundException {
		this(dir, filename, name, gds, edgeType, edgeParameter, false);
	}

	public KonectReader(String dir, String filename, String name,
			GraphDataStructure gds, KonectEdgeType edgeType,
			String edgeParameter, boolean removeZeroDegreeNodes)
			throws FileNotFoundException {
		this.dir = dir;
		this.filename = filename;
		this.name = name;
		this.reader = new Reader(dir, filename);
		this.gds = gds;
		this.edgeType = edgeType;
		this.edgeParameter = edgeParameter;
		this.removeZeroDegreeNodes = removeZeroDegreeNodes;

		if (edgeType.equals(KonectEdgeType.WEIGHTED)
				&& edgeParameter.length() > 0 && !edgeParameter.equals("-")) {
			String[] temp = edgeParameter.split(separator);
			this.offset = Double.parseDouble(temp[0]);
			this.factor = Double.parseDouble(temp[1]);
		} else {
			this.offset = 0;
			this.factor = 1;
		}

		if (edgeType.equals(KonectEdgeType.MULTI) && edgeParameter != null
				&& edgeParameter.length() > 0 && !edgeParameter.equals("-")) {
			this.revert = new ArrayDeque<KonectEdge>();
			this.durability = Long.parseLong(edgeParameter);
		} else {
			this.revert = null;
		}
	}

	private KonectEdge peek = null;

	public KonectEdge peek() {
		if (true) {
			return this.peekNew();
		}
		return this.peekOld();
	}

	public KonectEdge readEdge() {
		if (true) {
			return this.readEdgeNew();
		}
		try {
			return this.readEdgeOld();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public KonectEdge peekNew() {
		if (this.peek == null) {
			try {
				String line = this.reader.readString();
				if (line != null) {
					KonectEdge edge = new KonectEdge(line, offset, factor);
					if (this.revert != null) {
						KonectEdge reversion = new KonectEdge(edge.n1, edge.n2,
								-1, edge.timestamp + durability);
						this.revert.addLast(reversion);
					}
					this.peek = edge;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (this.peek == null) {
			if (this.revert != null && !this.revert.isEmpty()) {
				return this.revert.peek();
			} else {
				return null;
			}
		}
		if (this.revert != null && !this.revert.isEmpty()) {
			if (this.revert.peek().timestamp < this.peek.timestamp) {
				return this.revert.peek();
			} else {
				return this.peek;
			}
		} else {
			return this.peek;
		}
	}

	public KonectEdge readEdgeNew() {
		if (this.peek == null) {
			try {
				String line = this.reader.readString();
				if (line != null) {
					KonectEdge edge = new KonectEdge(line, offset, factor);
					if (this.revert != null) {
						KonectEdge reversion = new KonectEdge(edge.n1, edge.n2,
								-1, edge.timestamp + durability);
						this.revert.addLast(reversion);
					}
					this.peek = edge;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (peek == null) {
			if ((revert == null || revert.isEmpty())) {
				return null;
			} else {
				return revert.pop();
			}
		} else {
			if ((revert == null || revert.isEmpty())
					|| peek.timestamp < revert.peek().timestamp) {
				KonectEdge temp = peek;
				peek = null;
				return temp;
			} else {
				return revert.pop();
			}
		}

	}

	public KonectEdge peekOld() {
		if (this.peek != null) {
			return this.peek;
		} else {
			try {
				this.peek = this.readEdgeOld();
				return this.peek;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public KonectEdge readEdgeOld() throws IOException {
		if (peek != null) {
			KonectEdge temp = this.peek;
			this.peek = null;
			return temp;
		}
		String line = this.reader.readString();
		if (line == null) {
			return null;
		} else {
			KonectEdge edge = new KonectEdge(line, offset, factor);
			if (this.revert != null) {
				KonectEdge reversion = new KonectEdge(edge.n1, edge.n2, -1,
						edge.timestamp);
				this.revert.addLast(reversion);
			}
			return edge;
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
