package dna.metrics.motifs;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.motifs.UndirectedMotif.UndirectedMotifType;
import dna.metrics.motifs.exceptions.UndirectedMotifInvalidEdgeAdditionException;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedMotifsU extends UndirectedMotifs {

	private HashSet<UndirectedMotif> allMotifs;

	private HashMap<Integer, UndirectedMotif> allMotifs2;

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

			System.out.println("add edge " + e);
			this.g.printAll();

			HashSet<UndirectedMotif> addMotifs = new HashSet<UndirectedMotif>();

			for (UndirectedMotif m_ : this.allMotifs) {
				try {
					int before = m_.getIndex();

					if (m_.getType() == UndirectedMotifType.PRE1
							&& (e.isConnectedTo(m_.getA()) || e
									.isConnectedTo(m_.getB()))) {
						UndirectedNode a = m_.getA();
						UndirectedNode c = m_.getB();
						if (!e.isConnectedTo(a)) {
							a = m_.getB();
							c = m_.getA();
						}

						UndirectedNode b = e.getDifferingNode(a);

						for (UndirectedMotif m__ : this.allMotifs) {
							if (m__.getType() == UndirectedMotifType.PRE1
									&& !m__.equals(m_)) {
								if (b.equals(m__.getA())
										|| b.equals(m__.getB())) {
									UndirectedNode d = m__.getA();
									if (b.equals(m__.getA())) {
										d = m__.getB();
									}
									addMotifs.add(new UndirectedMotif(a, b, c,
											d, UndirectedMotifType.UM1));
									System.out.println("MERGING two motifs: "
											+ m_ + "\n&&&\n" + m__);
								}
							}
						}
					}

					// String s1 = m_.asString();
					m_.addEdge(e);
					// String s2 = m_.asString();
					if (m_.getIndex() != before) {
						this.motifs.decr(before);
						this.motifs.incr(m_.getIndex());
						// System.out.println(UndirectedMotif
						// .getTransformationString(s1, s2, true));
					} else {
						// System.out.println(s1);
					}
				} catch (UndirectedMotifInvalidEdgeAdditionException e1) {
					// e1.printStackTrace();
				}
			}

			for (UndirectedMotif m : addMotifs) {
				this.allMotifs.add(m);
				this.motifs.incr(m.getIndex());
				this.motifs.incrDenominator();
			}

			UndirectedMotif m_new = new UndirectedMotif(e.getNode1(),
					e.getNode2());
			this.allMotifs.add(m_new);
			System.out.println("new: " + m_new);
			this.motifs.incr(m_new.getIndex());
			this.motifs.incrDenominator();

			System.out.println("***********************");
			for (UndirectedMotif mm : this.allMotifs) {
				System.out.println(mm);
			}
			System.out.println("***********************");

			return true;
		} else if (u instanceof EdgeRemoval) {
			this.motifs.decrDenominator();
			return true;
		} else if (u instanceof NodeRemoval) {
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
		this.allMotifs = new HashSet<UndirectedMotif>();
		this.allMotifs2 = new HashMap<Integer, UndirectedMotif>();
	}

}
