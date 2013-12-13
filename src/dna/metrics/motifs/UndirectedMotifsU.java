package dna.metrics.motifs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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

	private HashSet<UndirectedMotif> preMotifs;

	private HashMap<Integer, UndirectedMotif> fullMotifs;

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

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof EdgeAddition) {
			this.motifs.incrDenominator();
			UndirectedEdge e = (((UndirectedEdge) ((EdgeAddition) u).getEdge()));

			Log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Log.debug(">>> add edge " + e);
			Log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			LinkedList<UndirectedMotif> newMotifs = new LinkedList<UndirectedMotif>();

			// add edge to pre motifs (creating a new motif)
			for (UndirectedMotif preMotif : this.preMotifs) {
				try {
					if (preMotif.getNodesHashCode() == UndirectedMotif
							.getHashCode(6, 4, 0, -2)) {
						Log.debug("checking: " + e + " @ " + preMotif);
					}
					UndirectedMotif newMotif = preMotif.addEdge(e);
					newMotifs.add(newMotif);
					this.motifs.incr(newMotif.getIndex());
					this.motifs.incrDenominator();
				} catch (UndirectedMotifInvalidEdgeAdditionException e1) {
					// e1.printStackTrace();
				}
			}

			// add edge to full motifs (changing the motif)
			for (UndirectedMotif fullMotif : this.fullMotifs.values()) {
				this.motifs.decr(fullMotif.getIndex());
				try {
					fullMotif.addEdge(e);
				} catch (UndirectedMotifInvalidEdgeAdditionException e1) {
					// e1.printStackTrace();
				}
				this.motifs.incr(fullMotif.getIndex());
			}

			// check for merge of two PRE1 motifs (== edges)
			if (true) {
				for (UndirectedMotif pre1 : this.preMotifs) {
					if (!pre1.has2Nodes()) {
						continue;
					}
					if (!e.isConnectedTo(pre1.getA())
							&& !e.isConnectedTo(pre1.getB())) {
						continue;
					}
					UndirectedNode a, c, b, d;
					a = pre1.getA();
					c = pre1.getB();
					if (!e.isConnectedTo(pre1.getA())) {
						a = pre1.getB();
						c = pre1.getA();
					}
					b = e.getDifferingNode(a);
					for (UndirectedMotif pre2 : this.preMotifs) {
						if (pre1.equals(pre2)) {
							continue;
						}
						if (!pre2.has2Nodes()) {
							continue;
						}
						if (!e.isConnectedTo(pre2.getA())
								&& !e.isConnectedTo(pre2.getB())) {
							continue;
						}
						if (pre1.getA().equals(pre2.getA())
								|| pre1.getA().equals(pre2.getB())
								|| pre1.getB().equals(pre2.getA())
								|| pre1.getB().equals(pre2.getB())) {
							continue;
						}
						d = pre2.getB();
						if (!e.isConnectedTo(pre2.getA())) {
							d = pre2.getA();
						}
						UndirectedMotif merge = new UndirectedMotif(a, b, c, d,
								UndirectedMotifType.UM1);
						if (!this.fullMotifs.containsKey(merge
								.getNodesHashCode())) {
							newMotifs.add(merge);
							this.motifs.incr(merge.getIndex());
							this.motifs.incrDenominator();
							Log.debug("adding merge: " + merge
									+ " (of " + pre1 + " AND " + pre2 + ") @ "
									+ pre1.equals(pre2));
						} else {
							// Log.debug("not adding merge "
							// + merge
							// + " because "
							// + this.fullMotifs.get(merge
							// .getNodesHashCode()));
						}
					}
				}
			}

			// add new motifs
			Log.debug("********************** adding...");
			for (UndirectedMotif newMotif : newMotifs) {
				if (newMotif.has4Nodes()) {
					if (this.fullMotifs
							.containsKey(newMotif.getNodesHashCode())) {
						UndirectedMotif similar = this.fullMotifs.get(newMotif
								.getNodesHashCode());
						if (newMotif.getEdgeCount() > similar.getEdgeCount()) {
							this.fullMotifs.put(newMotif.getNodesHashCode(),
									newMotif);
							Log.debug("full-replace: " + newMotif
									+ "(" + similar + ")");
							this.motifs.decr(similar.getIndex());
						} else {
							Log.debug("full-exists: " + newMotif + "("
									+ similar + ")");
							this.motifs.decr(newMotif.getIndex());
						}
					} else {
						this.fullMotifs.put(newMotif.getNodesHashCode(),
								newMotif);
						Log.debug("full: " + newMotif);
					}
				} else {
					this.preMotifs.add(newMotif);
					Log.debug("pre: " + newMotif);
				}
			}
			Log.debug("**********************");

			UndirectedMotif pre1Motif = new UndirectedMotif(e.getNode1(),
					e.getNode2());
			this.preMotifs.add(pre1Motif);
			this.motifs.incr(pre1Motif.getIndex());
			this.motifs.incrDenominator();
			Log.debug("edge: " + pre1Motif);
			Log.debug("**********************");

			Log.debug("====> " + this.preMotifs.size());
			for (UndirectedMotif m : this.preMotifs) {
				Log.debug("P: " + m);
			}
			Log.debug("====> " + this.fullMotifs.size());
			for (UndirectedMotif m : this.fullMotifs.values()) {
				Log.debug("F: " + m);
			}
			Log.debug("**********************");

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
		this.preMotifs = new HashSet<UndirectedMotif>();
		this.fullMotifs = new HashMap<Integer, UndirectedMotif>();
	}
}
