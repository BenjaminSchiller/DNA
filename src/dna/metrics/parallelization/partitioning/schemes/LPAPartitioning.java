package dna.metrics.parallelization.partitioning.schemes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * 
 * Partitioning of a graph using the community detection LPA (Label Propagation
 * Algorithm) by Liu and Murata. The label propagation is executed to assign a
 * label (community identifier) to each node. The propagation is stopped in case
 * is does not terminate after 10,000 rounds (maxRounds parameter).
 * 
 * If the number of communities is less than of equal to the target number of
 * partitions, each community is assigned to a partition. This can lead to to
 * very unbalanced partition sized. In the worst case, all nodes are assigned to
 * a single community which results in a single partition containing all nodes.
 * 
 * If there are more communities than target partitions, the communities are
 * sorted by descending size and the first partitionCount communities assigned
 * to a single partition each. Then, the remaining communities are sorted by
 * ascending size and assigned to the partitions in round-robin manner. During
 * the round-robin assignment, partitions are skipped in case they contain more
 * than the average partition size (|V| / partitionCount).
 * 
 * @author benni
 *
 */
public class LPAPartitioning extends PartitioningScheme {

	public LPAPartitioning(PartitioningType partitioningType, int partitionCount) {
		super("LPAPartitioning", partitioningType, partitionCount);
	}

	public static final int maxRounds = 10000;

	protected int changes;
	protected int[] currentLabels;
	protected int[] newLabels;
	protected int rounds;

	public int[] getLabels() {
		return this.currentLabels;
	}

	public int getRounds() {
		return this.rounds;
	}

	@Override
	public List<List<Node>> getPartitioning(Graph g) {
		currentLabels = new int[g.getMaxNodeIndex() + 1];
		newLabels = new int[g.getMaxNodeIndex() + 1];
		for (int i = 0; i < currentLabels.length; i++) {
			currentLabels[i] = i;
		}

		rounds = 0;
		changes = -1;
		while (changes != 0 && rounds < maxRounds) {
			this.nextRound(g);
			rounds++;
		}

		HashMap<Integer, List<Node>> map = new HashMap<Integer, List<Node>>();
		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			int label = currentLabels[n.getIndex()];
			if (!map.containsKey(label)) {
				map.put(label, new ArrayList<Node>());
			}
			map.get(label).add(n);
		}

		ArrayList<List<Node>> communities = new ArrayList<List<Node>>(
				map.values());
		Collections.sort(communities, new Comparator<List<Node>>() {
			public int compare(List<Node> a1, List<Node> a2) {
				return a2.size() - a1.size();
			}
		});
		// System.out.println("--------");
		// int ii = 0;
		// for (List<Node> community : communities) {
		// System.out.println(ii++ + ": " + community.size());
		// }
		// System.out.println("--------");

		ArrayList<List<Node>> partitions = new ArrayList<List<Node>>(
				this.partitionCount);
		for (int i = 0; i < this.partitionCount; i++) {
			partitions.add(new ArrayList<Node>());
		}

		if (communities.size() <= partitions.size()) {
			int index = 0;
			for (List<Node> community : communities) {
				partitions.get(index++).addAll(community);
			}
		} else {
			for (int i = 0; i < partitions.size(); i++) {
				partitions.get(i).addAll(communities.get(i));
			}
			for (int i = 0; i < partitions.size(); i++) {
				communities.remove(0);
			}
			Collections.sort(communities, new Comparator<List<Node>>() {
				public int compare(List<Node> a1, List<Node> a2) {
					return a1.size() - a2.size();
				}
			});

			int avg = (int) Math.floor((double) g.getNodeCount()
					/ (double) this.partitionCount);
			int index = 0;
			for (List<Node> community : communities) {
				while (partitions.get(index).size() >= avg) {
					index = (index + 1) % partitions.size();
				}
				// System.out.println(index + " => " + community.size());
				partitions.get(index).addAll(community);
				index = (index + 1) % partitions.size();
			}

		}

		return partitions;
	}

	protected void nextRound(Graph g) {
		changes = 0;
		for (IElement n_ : g.getNodes()) {
			changes += this.computeLabel(g, (Node) n_) ? 1 : 0;
		}
		currentLabels = newLabels;
		newLabels = new int[g.getMaxNodeIndex() + 1];
	}

	protected boolean computeLabel(Graph g, Node n) {
		if (n.getDegree() == 0) {
			newLabels[n.getIndex()] = currentLabels[n.getIndex()];
			return false;
		}
		HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (IElement e_ : n.getEdges()) {
			Edge e = (Edge) e_;
			Node n2 = e.getDifferingNode(n);
			int l = currentLabels[n2.getIndex()];
			if (counts.containsKey(l)) {
				counts.put(l, counts.get(l) + 1);
			} else {
				counts.put(l, 1);
			}
		}
		int maxCount = 0;
		for (Integer c : counts.values()) {
			if (c > maxCount) {
				maxCount = c;
			}
		}
		ArrayList<Integer> labels = new ArrayList<Integer>();
		for (Entry<Integer, Integer> entry : counts.entrySet()) {
			if (entry.getValue() == maxCount) {
				labels.add(entry.getKey());
			}
		}
		newLabels[n.getIndex()] = labels.get(Rand.rand.nextInt(labels.size()));
		boolean changed = newLabels[n.getIndex()] != currentLabels[n.getIndex()];

		// NodeWeight nwc = new NodeWeight((IWeightedNode) n, new IntWeight(
		// newLabels[n.getIndex()]));
		// nwc.apply(g);

		return changed;
	}
}
