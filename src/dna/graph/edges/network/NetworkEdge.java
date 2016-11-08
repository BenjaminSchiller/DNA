package dna.graph.edges.network;

import java.util.ArrayList;

/**
 * Class which represents one edge in a network-graph. <br>
 * 
 * Contains mapped index of the source and destination nodes and the recent
 * timestamp.
 * 
 * @author Rwilmes
 * 
 */
public class NetworkEdge extends UpdateEvent {

	protected int src;
	protected int dst;
	protected double[] edgeWeights;

	public NetworkEdge(int src, int dst, long time, double[] edgeWeights) {
		super(time);
		this.src = src;
		this.dst = dst;
		this.edgeWeights = edgeWeights;
	}

	public int getSrc() {
		return src;
	}

	public int getDst() {
		return dst;
	}

	public double[] getEdgeWeights() {
		return edgeWeights;
	}

	public void setEdgeWeights(double[] edgeWeights) {
		this.edgeWeights = edgeWeights;
	}

	public boolean sameEdge(NetworkEdge e) {
		return ((src == e.getSrc()) && (dst == e.getDst()));
	}

	public String toString() {
		return "NetworkEdge: " + src + "\t=>\t" + dst + "\tw=" + edgeWeights
				+ "\t@\t" + time;
	}

	/** Returns if the same edge is contained in the given list. **/
	public boolean containedIn(ArrayList<NetworkEdge> list) {
		boolean added = false;
		NetworkEdge ne = null;
		for (int j = 0; j < list.size(); j++) {
			ne = list.get(j);
			if (ne.getSrc() == src && ne.getDst() == dst) {
				added = true;
				break;
			}
		}

		return added;
	}

	public boolean isZero() {
		return (this.edgeWeights[0] == 0);
	}
}
