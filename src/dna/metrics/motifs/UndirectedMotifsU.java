package dna.metrics.motifs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.motifs.UndirectedMotif.UndirectedMotifType;
import dna.metrics.motifs.exceptions.UndirectedMotifInvalidEdgeAdditionException;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.Log;

public class UndirectedMotifsU extends UndirectedMotifs {

	// private HashSet<UndirectedMotif> preMotifs;

	// private HashMap<Integer, UndirectedMotif> fullMotifs;

	private HashMap<Integer, HashSet<UndirectedMotif>> nodePreMotifs;

	private HashMap<Integer, HashMap<Integer, UndirectedMotif>> nodeFullMotifs;

	protected HashSet<UndirectedMotif> getNodePreMotifs(int index) {
		HashSet<UndirectedMotif> m = this.nodePreMotifs.get(index);
		if (m == null) {
			m = new HashSet<UndirectedMotif>();
			this.nodePreMotifs.put(index, m);
		}
		return m;
	}

	protected HashMap<Integer, UndirectedMotif> getNodeFullMotifs(int index) {
		HashMap<Integer, UndirectedMotif> m = this.nodeFullMotifs.get(index);
		if (m == null) {
			m = new HashMap<Integer, UndirectedMotif>();
			this.nodeFullMotifs.put(index, m);
		}
		return m;
	}

	protected void addMotif(UndirectedMotif motif) {
		if (motif.has4Nodes()) {
			// this.fullMotifs.put(motif.getNodesHashCode(), motif);
			this.getNodeFullMotifs(motif.getA().getIndex()).put(
					motif.getNodesHashCode(), motif);
			this.getNodeFullMotifs(motif.getB().getIndex()).put(
					motif.getNodesHashCode(), motif);
			this.getNodeFullMotifs(motif.getC().getIndex()).put(
					motif.getNodesHashCode(), motif);
			this.getNodeFullMotifs(motif.getD().getIndex()).put(
					motif.getNodesHashCode(), motif);
		} else {
			// this.preMotifs.add(motif);
			this.getNodePreMotifs(motif.getA().getIndex()).add(motif);
			this.getNodePreMotifs(motif.getB().getIndex()).add(motif);
			if (motif.has3Nodes()) {
				this.getNodePreMotifs(motif.getC().getIndex()).add(motif);
			}
		}
	}

	public UndirectedMotifsU() {
		super("UndirectedMotifsU", ApplicationType.AfterUpdate,
				MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	protected void addToFullMotifs(Iterable<UndirectedMotif> motifs,
			UndirectedEdge e) {
		this.addToFullMotifs(motifs, e, null);
	}

	protected void addToFullMotifs(Iterable<UndirectedMotif> motifs,
			UndirectedEdge e, UndirectedNode exclude) {
		for (UndirectedMotif m : motifs) {
			if (exclude != null && m.contains(exclude)) {
				continue;
			}
			this.motifs.decr(m.getIndex());
			try {
				m.addEdge(e);
				this.addMotif(m);
			} catch (UndirectedMotifInvalidEdgeAdditionException e1) {
				// e1.printStackTrace();
			}
			this.motifs.incr(m.getIndex());
		}
	}

	protected void addToPreMotifs(Iterable<UndirectedMotif> motifs,
			UndirectedEdge e, LinkedList<UndirectedMotif> newMotifs) {
		this.addToPreMotifs(motifs, e, newMotifs, null);
	}

	protected void addToPreMotifs(Iterable<UndirectedMotif> motifs,
			UndirectedEdge e, LinkedList<UndirectedMotif> newMotifs,
			UndirectedNode exclude) {
		for (UndirectedMotif m : motifs) {
			if (exclude != null && m.contains(exclude)) {
				continue;
			}
			try {
				UndirectedMotif newMotif = m.addEdge(e);
				newMotifs.add(newMotif);
				this.motifs.incr(newMotif.getIndex());
				this.motifs.incrDenominator();
			} catch (UndirectedMotifInvalidEdgeAdditionException e1) {
				// e1.printStackTrace();
			}
		}
	}

	protected void addMerge(UndirectedEdge e,
			LinkedList<UndirectedMotif> newMotifs) {
		UndirectedNode a = e.getNode1();
		UndirectedNode b = e.getNode2();

		for (IElement en1 : a.getEdges()) {
			UndirectedEdge e1 = (UndirectedEdge) en1;
			UndirectedNode c = e1.getDifferingNode(a);
			if (c.equals(b)) {
				continue;
			}

			for (IElement en2 : b.getEdges()) {
				UndirectedEdge e2 = (UndirectedEdge) en2;
				UndirectedNode d = e2.getDifferingNode(b);
				if (d.equals(a) || d.equals(c)) {
					continue;
				}

				UndirectedMotif m = new UndirectedMotif(a, b, c, d,
						UndirectedMotifType.UM1);
				Log.debug("adding merge: " + m);
				newMotifs.add(m);
				this.motifs.incr(m.getIndex());
				this.motifs.incrDenominator();
			}
		}
	}

	protected void addNewMotifs(LinkedList<UndirectedMotif> newMotifs,
			UndirectedEdge e) {
		Log.debug("********************** adding...");
		for (UndirectedMotif newMotif : newMotifs) {
			if (newMotif.has4Nodes()) {
				if (this.getNodeFullMotifs(e.getNode1().getIndex())
						.containsKey(newMotif.getNodesHashCode())) {
					// if
					// (this.fullMotifs.containsKey(newMotif.getNodesHashCode()))
					// {
					// UndirectedMotif similar = this.fullMotifs.get(newMotif
					// .getNodesHashCode());
					UndirectedMotif similar = this.getNodeFullMotifs(
							e.getNode1().getIndex()).get(
							newMotif.getNodesHashCode());
					if (newMotif.getEdgeCount() > similar.getEdgeCount()) {
						// this.fullMotifs.put(newMotif.getNodesHashCode(),
						// newMotif);
						// this.addMotif(newMotif, node1, motifsNode1,
						// node2,
						// motifsNode2);
						this.addMotif(newMotif);

						Log.debug("full-replace: " + newMotif + "(" + similar
								+ ")");
						this.motifs.decr(similar.getIndex());
					} else {
						Log.debug("full-exists: " + newMotif + "(" + similar
								+ ")");
						this.motifs.decr(newMotif.getIndex());
					}
				} else {
					// this.fullMotifs.put(newMotif.getNodesHashCode(),
					// newMotif);
					// this.addMotif(newMotif, node1, motifsNode1, node2,
					// motifsNode2);
					this.addMotif(newMotif);
					Log.debug("full: " + newMotif);
				}
			} else {
				// this.preMotifs.add(newMotif);
				// this.addMotif(newMotif, node1, motifsNode1, node2,
				// motifsNode2);
				this.addMotif(newMotif);
				Log.debug("pre: " + newMotif);
			}
		}
		Log.debug("**********************");

		UndirectedMotif edgeMotif = new UndirectedMotif(e.getNode1(),
				e.getNode2());
		// this.preMotifs.add(pre1Motif);
		// this.addMotif(edgeMotif, node1, motifsNode1, node2, motifsNode2);
		this.addMotif(edgeMotif);

		this.motifs.incr(edgeMotif.getIndex());
		this.motifs.incrDenominator();

		Log.debug("edge: " + edgeMotif);
		Log.debug("**********************");
	}

	protected void debug() {
		// Log.debug("====> " + this.preMotifs.size());
		// for (UndirectedMotif m : this.preMotifs) {
		// Log.debug("P: " + m);
		// }
		// Log.debug("====> " + this.fullMotifs.size());
		// for (UndirectedMotif m : this.fullMotifs.values()) {
		// Log.debug("F: " + m);
		// }
		int count = 0;
		System.out.println("*********************");
		for (int index : this.nodeFullMotifs.keySet()) {
			HashMap<Integer, UndirectedMotif> motifs = this.nodeFullMotifs
					.get(index);
			System.out.println(index + ":");
			count += motifs.size();
			for (UndirectedMotif m : motifs.values()) {
				System.out.println("  " + m + " ("
						+ m.contains((UndirectedNode) this.g.getNode(index))
						+ ")");
			}
		}
		for (int index : this.nodePreMotifs.keySet()) {
			HashSet<UndirectedMotif> motifs = this.nodePreMotifs.get(index);
			System.out.println(index + ":");
			count += motifs.size();
			for (UndirectedMotif m : motifs) {
				System.out.println("  " + m + " ("
						+ m.contains((UndirectedNode) this.g.getNode(index))
						+ ")");
			}
		}
		int countPre1 = 0;
		int countPre2 = 0;
		int countPre3 = 0;
		// for (UndirectedMotif pre : this.preMotifs) {
		// if (pre.getType().equals(UndirectedMotifType.PRE1)) {
		// countPre1++;
		// } else if (pre.getType().equals(UndirectedMotifType.PRE2)) {
		// countPre2++;
		// } else if (pre.getType().equals(UndirectedMotifType.PRE3)) {
		// countPre3++;
		// }
		// }

		// System.out
		// .println("2*pre1 + 3*pre2 + 3*pre3 + 4*full = 2*"
		// + countPre1
		// + " + 3*"
		// + countPre2
		// + " + 3*"
		// + countPre3
		// + " + 4*"
		// + this.fullMotifs.size()
		// + " = "
		// + (2 * countPre1 + 3 * countPre2 + 3 * countPre3 + 4 *
		// this.fullMotifs
		// .size()) + " =?= " + count);
		Log.debug("**********************");
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof EdgeAddition) {
			this.motifs.incrDenominator();
			UndirectedEdge e = (((UndirectedEdge) ((EdgeAddition) u).getEdge()));

			Log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Log.debug(">>> add edge " + e);
			Log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			LinkedList<UndirectedMotif> newMotifs = new LinkedList<UndirectedMotif>();

			boolean stupid = false;

			// add edge to pre motifs (creating a new motif)
			if (stupid) {
				// this.addToPreMotifs(this.preMotifs, e, newMotifs);
			} else {
				this.addToPreMotifs(
						this.getNodePreMotifs(e.getNode1().getIndex()), e,
						newMotifs);
				this.addToPreMotifs(
						this.getNodePreMotifs(e.getNode2().getIndex()), e,
						newMotifs, e.getNode1());
			}

			// add edge to full motifs (changing the motif)
			if (stupid) {
				// this.addToFullMotifs(this.fullMotifs.values(), e);
			} else {
				this.addToFullMotifs(
						this.getNodeFullMotifs(e.getNode1().getIndex())
								.values(), e);
				this.addToFullMotifs(
						this.getNodeFullMotifs(e.getNode2().getIndex())
								.values(), e, e.getNode1());
			}

			// check for merge of two PRE1 motifs (== edges)
			this.addMerge(e, newMotifs);

			// add new motifs
			this.addNewMotifs(newMotifs, e);

			// DEBUG
			// this.debug();

			return true;
		} else if (u instanceof EdgeRemoval) {
			// TODO implement edge removal
			this.motifs.decrDenominator();
			return true;
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
			UndirectedNode n = (((UndirectedNode) ((NodeRemoval) u).getNode()));
			this.motifs.decrDenominator(n.getDegree());
			return true;
		}

		return true;
	}

	@Override
	public boolean compute() {
		this.motifs.setDenominator(0);
		return true;
	}

	public void init_() {
		super.init_();
		// this.preMotifs = new HashSet<UndirectedMotif>();
		// this.fullMotifs = new HashMap<Integer, UndirectedMotif>();
		this.nodePreMotifs = new HashMap<Integer, HashSet<UndirectedMotif>>();
		this.nodeFullMotifs = new HashMap<Integer, HashMap<Integer, UndirectedMotif>>();
	}
}
