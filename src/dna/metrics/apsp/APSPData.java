package dna.metrics.apsp;

import java.util.ArrayList;

import dna.graph.directed.DirectedNode;
import dna.util.ArrayUtils;

public class APSPData {

	int[] count;
	ArrayList<DirectedNode>[] list;

	public APSPData(int d) {
		this.count = new int[2 * d];
		this.list = new ArrayList[2 * d];
		for (int i = 0; i < list.length; i++) {
			this.list[i] = new ArrayList<DirectedNode>();
		}
	}

	public void increaseCount(int i) {
		this.count = ArrayUtils.incr(count, i);
	}

	public void addList(int i, DirectedNode n) {
		this.list[i].add(n);
	}

}
