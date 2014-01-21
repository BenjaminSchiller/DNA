package dna.metrics.motifs;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * per update computation / update of the directed 3-node motif counts. for
 * every update, all triple of nodes involved in new/changed motifs are listed.
 * 
 * 
 * @author benni
 * 
 */
public class DirectedMotifsU extends DirectedMotifs {

	public DirectedMotifsU() {
		super("DirectedMotifsU", ApplicationType.BeforeUpdate, MetricType.exact);
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
		// System.out.println("\n====================> " + u + "\n");
		if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {
			DirectedEdge e = null;
			boolean add = true;
			if (u instanceof EdgeAddition) {
				e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			} else {
				e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
				add = false;
			}
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();

			HashSet<DirectedNode> ab = this.getUnion(a, b);

			if (a.hasEdge(new DirectedEdge(b, a))) {
				this.processBothDirections(a, b, ab, add);
			} else {
				this.processSingleDirection(a, b, ab, add);
			}

		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
		}
		return true;
	}

	private void processSingleDirection(DirectedNode a, DirectedNode b,
			HashSet<DirectedNode> ab, boolean add) {
		for (DirectedNode c : ab) {
			boolean ca = c.hasEdge(new DirectedEdge(c, a));
			boolean ac = c.hasEdge(new DirectedEdge(a, c));
			boolean cb = c.hasEdge(new DirectedEdge(c, b));
			boolean bc = c.hasEdge(new DirectedEdge(b, c));

			if (ca && !ac && !cb && !bc) {
				this.changeMotif(DirectedMotifType.DM03, add);
			} else if (!ca && ac && !cb && !bc) {
				this.changeMotif(DirectedMotifType.DM01, add);
			} else if (!ca && !ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM02, add);
			} else if (!ca && !ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM03, add);
			} else if (ca && ac && !cb && !bc) {
				this.changeMotif(DirectedMotifType.DM05, add);
			} else if (!ca && !ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM06, add);
			} else if (ca && !ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM01,
						DirectedMotifType.DM04, add);
			} else if (!ca && ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM02,
						DirectedMotifType.DM04, add);
			} else if (!ca && ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM03,
						DirectedMotifType.DM04, add);
			} else if (ca && !ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM03,
						DirectedMotifType.DM07, add);
			} else if (ca && ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM05,
						DirectedMotifType.DM09, add);
			} else if (ca && ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM06,
						DirectedMotifType.DM10, add);
			} else if (ca && !ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM05,
						DirectedMotifType.DM10, add);
			} else if (!ca && ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM06,
						DirectedMotifType.DM08, add);
			} else if (ca && ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM11,
						DirectedMotifType.DM12, add);
			} else if (!ca && !ac && !cb && !bc) {
				System.err
						.println("not possible... at least one edge must exist");
			}
		}
	}

	private void processBothDirections(DirectedNode a, DirectedNode b,
			HashSet<DirectedNode> ab, boolean add) {
		for (DirectedNode c : ab) {
			boolean ca = c.hasEdge(new DirectedEdge(c, a));
			boolean ac = c.hasEdge(new DirectedEdge(a, c));
			boolean cb = c.hasEdge(new DirectedEdge(c, b));
			boolean bc = c.hasEdge(new DirectedEdge(b, c));

			if (ca && !ac && !cb && !bc) {
				this.changeMotif(DirectedMotifType.DM02,
						DirectedMotifType.DM06, add);
			} else if (!ca && ac && !cb && !bc) {
				this.changeMotif(DirectedMotifType.DM03,
						DirectedMotifType.DM05, add);
			} else if (!ca && !ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM03,
						DirectedMotifType.DM06, add);
			} else if (!ca && !ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM01,
						DirectedMotifType.DM05, add);
			} else if (ca && ac && !cb && !bc) {
				this.changeMotif(DirectedMotifType.DM06,
						DirectedMotifType.DM11, add);
			} else if (!ca && !ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM05,
						DirectedMotifType.DM11, add);
			} else if (ca && !ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM04,
						DirectedMotifType.DM08, add);
			} else if (!ca && ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM04,
						DirectedMotifType.DM09, add);
			} else if (!ca && ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM07,
						DirectedMotifType.DM10, add);
			} else if (ca && !ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM04,
						DirectedMotifType.DM10, add);
			} else if (ca && ac && cb && !bc) {
				this.changeMotif(DirectedMotifType.DM10,
						DirectedMotifType.DM12, add);
			} else if (ca && ac && !cb && bc) {
				this.changeMotif(DirectedMotifType.DM08,
						DirectedMotifType.DM12, add);
			} else if (ca && !ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM09,
						DirectedMotifType.DM12, add);
			} else if (!ca && ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM10,
						DirectedMotifType.DM12, add);
			} else if (ca && ac && cb && bc) {
				this.changeMotif(DirectedMotifType.DM12,
						DirectedMotifType.DM13, add);
			} else if (!ca && !ac && !cb && !bc) {
				System.err
						.println("not possible... at least one edge must exist");
			}
		}
	}

	private HashSet<DirectedNode> getUnion(DirectedNode a, DirectedNode b) {
		HashSet<DirectedNode> union = new HashSet<DirectedNode>(a.getDegree()
				+ b.getDegree());
		for (IElement e : a.getOutgoingEdges()) {
			union.add(((DirectedEdge) e).getDst());
		}
		for (IElement e : a.getIncomingEdges()) {
			union.add(((DirectedEdge) e).getSrc());
		}
		for (IElement e : b.getOutgoingEdges()) {
			union.add(((DirectedEdge) e).getDst());
		}
		for (IElement e : b.getIncomingEdges()) {
			union.add(((DirectedEdge) e).getSrc());
		}
		union.remove(a);
		union.remove(b);
		return union;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	private void changeMotif(DirectedMotifType t1, DirectedMotifType t2,
			boolean add) {
		if (add) {
			// System.out.println(t1 + " => " + t2);
			this.motifs.decr(DirectedMotifs.getIndex(t1));
			this.motifs.incr(DirectedMotifs.getIndex(t2));
		} else {
			// System.out.println(t2 + " => " + t1);
			this.motifs.decr(DirectedMotifs.getIndex(t2));
			this.motifs.incr(DirectedMotifs.getIndex(t1));
		}
	}

	private void changeMotif(DirectedMotifType type, boolean add) {
		if (add) {
			// System.out.println(type + " add");
			this.motifs.incr(DirectedMotifs.getIndex(type));
		} else {
			// System.out.println(type + " rm");
			this.motifs.decr(DirectedMotifs.getIndex(type));
		}
	}

}
