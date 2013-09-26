package dna.metrics.apsp;

public class Triplet<K, T, V> {

	public final K k;
	public final T t;
	public final V v;

	public Triplet(K k, T t, V v) {
		this.k = k;
		this.t = t;
		this.v = v;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof Triplet)) {
			return false;
		}
		Triplet<K, T, V> other_ = (Triplet<K, T, V>) other;
		return other_.k == this.k && other_.t == this.t && other_.v == this.v;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((k == null) ? 0 : k.hashCode());
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
		return result;
	}

	// Queue<DirectedNode>[] qLevel = new LinkedList[this.g.getNodes()
	// .size()];
	// for (int i = 0; i < qLevel.length; i++) {
	// qLevel[i] = new LinkedList<DirectedNode>();
	// }
	//
	// HashSet<DirectedNode> uncertain = new HashSet<DirectedNode>();
	// uncertain.add(dst);
	//
	// qLevel[height.get(dst)].add(dst);
	//
	// for (int i = 0; i < qLevel.length; i++) {
	// while (!qLevel[i].isEmpty()) {
	// DirectedNode w = qLevel[i].poll();
	//
	// DirectedNode min = w;
	// int dist = Integer.MAX_VALUE;
	//
	// for (DirectedEdge edge : w.getIncomingEdges()) {
	// if (height.get(edge.getSrc()) <= dist
	// && edge.getSrc() != parent.get(w)) {
	// min = edge.getSrc();
	// dist = height.get(edge.getSrc());
	// }
	// }
	//
	// if (dist == Integer.MAX_VALUE) {
	// HashSet<DirectedNode> remove = new HashSet<>();
	// for (int j = 0; j < qLevel.length; j++) {
	// remove.addAll(qLevel[j]);
	// qLevel[j].clear();
	// }
	// for (DirectedNode directedNode : remove) {
	// height.put(directedNode, Integer.MAX_VALUE);
	// }
	// break;
	// }
	// if (uncertain.contains(w)) {
	//
	// if (i == dist) {
	// // settle
	// parent.put(w, min);
	// if (!uncertain.contains(w)) {
	// height.put(w, dist);
	// }
	//
	// } else {
	// // make changed
	// qLevel[dist].add(w);
	// uncertain.remove(w);
	//
	// for (DirectedEdge edge : w.getOutgoingEdges()) {
	// if (parent.get(edge.getDst()) == w) {
	// parent.remove(edge.getDst());
	// qLevel[height.get(edge.getDst())].add(edge
	// .getDst());
	// uncertain.add(edge.getDst());
	// }
	// }
	// }
	// } else {
	// // settle
	// parent.put(w, min);
	// if (!uncertain.contains(w)) {
	// height.put(w, dist);
	// }
	// }
	// }
	// }

}
