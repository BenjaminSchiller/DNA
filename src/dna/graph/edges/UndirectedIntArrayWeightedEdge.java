package dna.graph.edges;

import java.util.Arrays;
import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.Config;

public class UndirectedIntArrayWeightedEdge extends UndirectedEdge implements
		IWeightedEdge<int[]> {
	private int[] weight;

	public UndirectedIntArrayWeightedEdge(UndirectedNode node1,
			UndirectedNode node2, int[] weight) {
		super(node1, node2);
		this.weight = weight;
	}

	public UndirectedIntArrayWeightedEdge(String str, Graph g) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			String[] v = str.split(Config.get("NODE_WEIGHT_DELIMITER"))[1]
					.split(Config.get("NODE_WEIGHT_SEPARATOR"));
			this.weight = new int[v.length];
			for (int i = 0; i < v.length; i++) {
				this.weight[i] = Integer.parseInt(v[i]);
			}
		} else {
			this.weight = new int[0];
		}
	}

	public UndirectedIntArrayWeightedEdge(String str, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g, addedNodes);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			String[] v = str.split(Config.get("NODE_WEIGHT_DELIMITER"))[1]
					.split(Config.get("NODE_WEIGHT_SEPARATOR"));
			this.weight = new int[v.length];
			for (int i = 0; i < v.length; i++) {
				this.weight[i] = Integer.parseInt(v[i]);
			}
		} else {
			this.weight = new int[0];
		}
	}

	public UndirectedIntArrayWeightedEdge(UndirectedNode node1,
			UndirectedNode node2) {
		this(node1, node2, new int[0]);
	}

	@Override
	public void setWeight(int[] newWeight) {
		this.weight = newWeight;
	}

	@Override
	public int[] getWeight() {
		return this.weight;
	}

	public String getStringRepresentation() {
		if (this.weight.length == 0) {
			return super.getStringRepresentation();
		}
		StringBuffer buff = new StringBuffer(super.getStringRepresentation());
		buff.append(Config.get("NODE_WEIGHT_DELIMITER"));
		for (int i = 0; i < this.weight.length; i++) {
			if (i > 0) {
				buff.append(Config.get("NODE_WEIGHT_SEPARATOR"));
			}
			buff.append(this.weight[i]);
		}
		return buff.toString();
	}

	public String toString() {
		return super.toString() + " " + Arrays.toString(this.weight);
	}

}
