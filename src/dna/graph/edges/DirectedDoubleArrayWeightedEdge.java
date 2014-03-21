package dna.graph.edges;

import java.util.Arrays;
import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.util.Config;

public class DirectedDoubleArrayWeightedEdge extends DirectedEdge implements
		IWeightedEdge<double[]> {
	private double[] weight;

	public DirectedDoubleArrayWeightedEdge(DirectedNode src, DirectedNode dst,
			double[] weight) {
		super(src, dst);
		this.weight = weight;
	}

	public DirectedDoubleArrayWeightedEdge(String str, Graph g) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			String[] v = str.split(Config.get("NODE_WEIGHT_DELIMITER"))[1]
					.split(Config.get("NODE_WEIGHT_SEPARATOR"));
			this.weight = new double[v.length];
			for (int i = 0; i < v.length; i++) {
				this.weight[i] = Double.parseDouble(v[i]);
			}
		} else {
			this.weight = new double[0];
		}
	}

	public DirectedDoubleArrayWeightedEdge(String str, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g, addedNodes);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			String[] v = str.split(Config.get("NODE_WEIGHT_DELIMITER"))[1]
					.split(Config.get("NODE_WEIGHT_SEPARATOR"));
			this.weight = new double[v.length];
			for (int i = 0; i < v.length; i++) {
				this.weight[i] = Double.parseDouble(v[i]);
			}
		} else {
			this.weight = new double[0];
		}
	}

	public DirectedDoubleArrayWeightedEdge(DirectedNode src, DirectedNode dst) {
		this(src, dst, new double[0]);
	}

	@Override
	public void setWeight(double[] newWeight) {
		this.weight = newWeight;
	}

	@Override
	public double[] getWeight() {
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
