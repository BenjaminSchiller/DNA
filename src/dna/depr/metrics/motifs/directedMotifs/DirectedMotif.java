package dna.depr.metrics.motifs.directedMotifs;

import java.util.Arrays;

import dna.depr.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeAdditionException;
import dna.depr.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeRemovalException;
import dna.depr.metrics.motifs.directedMotifs.exceptions.DirectedMotifSplittingException;
import dna.depr.metrics.motifs.directedMotifs.exceptions.InvalidDirectedMotifException;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.motifs.DirectedMotifs.DirectedMotifType;

/**
 * 
 * representation of a directed 3-node motif used in some metrics. baseides
 * determining the motif of three given nodes, an existing motif can be altered
 * by adding or removing an edge to it.
 * 
 * @author benni
 * 
 */
@Deprecated
public class DirectedMotif {
	private DirectedNode a;

	private DirectedNode b;

	private DirectedNode c;

	private DirectedMotifType type;

	public DirectedNode getA() {
		return a;
	}

	public DirectedNode getB() {
		return b;
	}

	public DirectedNode getC() {
		return c;
	}

	public DirectedMotifType getType() {
		return type;
	}

	public boolean contains(DirectedNode n) {
		return this.a.equals(n) || this.b.equals(n) || this.c.equals(n);
	}

	public DirectedMotif(DirectedNode a, DirectedNode b, DirectedNode c,
			DirectedMotifType type) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.type = type;
	}

	private boolean connects(DirectedEdge e, DirectedNode src, DirectedNode dst) {
		return e.getSrc().equals(src) && e.getDst().equals(dst);
	}

	public boolean addEdge(DirectedEdge e)
			throws DirectedMotifInvalidEdgeAdditionException {
		switch (this.type) {
		case DM01:
			if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM04;
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM04;
				this.changeBC();
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM05;
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM05;
				this.changeBC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM02:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM06;
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM06;
				this.changeBC();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM04;
				this.changeAC();
				this.changeAB();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM04;
				this.changeAC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM03:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM05;
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM04;
				this.changeAB();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM06;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM07;
				this.changeBC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM04:
			if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM09;
				this.changeAC();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM10;
				this.changeAB();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM08;
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM05:
			if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM09;
				this.changeAC();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM11;
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM10;
				this.changeBC();
				this.changeAB();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM06:
			if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM11;
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM10;
				this.changeAC();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM08;
				this.changeAC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM07:
			if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM10;
				this.changeAB();
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM10;
				this.changeAC();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM10;
				this.changeBC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM08:
			if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM12;
				this.changeAB();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM12;
				this.changeAB();
				this.changeAC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM09:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM12;
				this.changeAB();
				this.changeBC();
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM12;
				this.changeAC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM10:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM12;
				this.changeAB();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM12;
				this.changeAC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM11:
			if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM12;
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM12;
				this.changeBC();
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM12:
			if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM13;
				return true;
			}
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		case DM13:
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		default:
			throw new DirectedMotifInvalidEdgeAdditionException(this, e);
		}
	}

	public boolean removeEdge(DirectedEdge e)
			throws DirectedMotifSplittingException,
			DirectedMotifInvalidEdgeRemovalException {
		switch (this.type) {
		case DM01:
			throw new DirectedMotifSplittingException(this, e);
		case DM02:
			throw new DirectedMotifSplittingException(this, e);
		case DM03:
			throw new DirectedMotifSplittingException(this, e);
		case DM04:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM02;
				this.changeAC();
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM03;
				this.changeAB();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM01;
				return true;
			}
		case DM05:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM03;
				return true;
			} else if (this.connects(e, a, c)) {
				throw new DirectedMotifSplittingException(this, e);
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM01;
				return true;
			}
		case DM06:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM02;
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM03;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, a)) {
				throw new DirectedMotifSplittingException(this, e);
			}
		case DM07:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM03;
				this.changeAC();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM03;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM03;
				this.changeAB();
				return true;
			}
		case DM08:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM06;
				this.changeAC();
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM06;
				this.changeBC();
				this.changeAC();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM04;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM04;
				return true;
			}
		case DM09:
			if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM05;
				this.changeAC();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM04;
				this.changeAC();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM05;
				this.changeBC();
				this.changeAC();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM04;
				this.changeAC();
				this.changeAB();
				return true;
			}
		case DM10:
			if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM05;
				this.changeBC();
				this.changeAC();
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM06;
				this.changeAC();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM07;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM04;
				this.changeAB();
				return true;
			}
		case DM11:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM06;
				this.changeBC();
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM06;
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM05;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM05;
				return true;
			}
		case DM12:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM08;
				this.changeAB();
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM10;
				this.changeAC();
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM10;
				this.changeAB();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM11;
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM09;
				this.changeAC();
				return true;
			}
		case DM13:
			if (this.connects(e, a, b)) {
				this.type = DirectedMotifType.DM12;
				this.changeAC();
				return true;
			} else if (this.connects(e, a, c)) {
				this.type = DirectedMotifType.DM12;
				this.changeBC();
				this.changeAC();
				return true;
			} else if (this.connects(e, b, a)) {
				this.type = DirectedMotifType.DM12;
				this.changeBC();
				this.changeAB();
				return true;
			} else if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM12;
				this.changeBC();
				return true;
			} else if (this.connects(e, c, a)) {
				this.type = DirectedMotifType.DM12;
				this.changeAB();
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM12;
				return true;
			}
		default:
		}
		throw new DirectedMotifInvalidEdgeRemovalException(this, e);
	}

	private void changeAB() {
		DirectedNode temp = this.a;
		this.a = this.b;
		this.b = temp;
	}

	private void changeAC() {
		DirectedNode temp = this.a;
		this.a = this.c;
		this.c = temp;
	}

	private void changeBC() {
		DirectedNode temp = this.b;
		this.b = this.c;
		this.c = temp;
	}

	public String toString() {
		return this.type + "." + this.a.getIndex() + "." + this.b.getIndex()
				+ "." + this.c.getIndex();
	}

	protected int[] getIndices() {
		int[] v = new int[] { a.getIndex(), b.getIndex(), c.getIndex() };
		Arrays.sort(v);
		return v;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DirectedMotif)) {
			return false;
		}
		int[] v1 = this.getIndices();
		DirectedMotif dm = (DirectedMotif) obj;
		int[] v2 = ((DirectedMotif) dm).getIndices();
		return v1[0] == v2[0] && v1[1] == v2[1] && v1[2] == v2[2];
	}

	public int hashCode() {
		return getHashCode(a, b, c);
	}

	public static int getHashCode(DirectedNode a, DirectedNode b, DirectedNode c) {
		return getHashCode(a.getIndex(), b.getIndex(), c.getIndex());
	}

	public static int getHashCode(int a, int b, int c) {
		int[] v = new int[] { a, b, c };
		Arrays.sort(v);
		return (v[0] + "." + v[1] + "." + v[2]).hashCode();
	}

	public String asString() {
		switch (this.type) {
		case DM01:
			return "(1) " + this.a.getIndex() + "    \n" + "   / \\   \n"
					+ "  /   \\  \n" + " X     X \n" + " " + this.b.getIndex()
					+ " --- " + this.c.getIndex() + " ";
		case DM02:
			return "(2) " + this.a.getIndex() + "    \n" + "   X X   \n"
					+ "  /   \\  \n" + " /     \\ \n" + " " + this.b.getIndex()
					+ " --- " + this.c.getIndex() + " ";
		case DM03:
			return "(3) " + this.a.getIndex() + "    \n" + "   X \\   \n"
					+ "  /   \\  \n" + " /     X \n" + " " + this.b.getIndex()
					+ " --- " + this.c.getIndex() + " ";
		case DM04:
			return "(4) " + this.a.getIndex() + "    \n" + "   / \\   \n"
					+ "  /   \\  \n" + " X     X \n" + " " + this.b.getIndex()
					+ " --X " + this.c.getIndex() + " ";
		case DM05:
			return "(5) " + this.a.getIndex() + "    \n" + "   X \\   \n"
					+ "  /   \\  \n" + " X     X \n" + " " + this.b.getIndex()
					+ " --- " + this.c.getIndex() + " ";
		case DM06:
			return "(6) " + this.a.getIndex() + "    \n" + "   X X   \n"
					+ "  /   \\  \n" + " X     \\ \n" + " " + this.b.getIndex()
					+ " --- " + this.c.getIndex() + " ";
		case DM07:
			return "(7) " + this.a.getIndex() + "    \n" + "   / X   \n"
					+ "  /   \\  \n" + " X     \\ \n" + " " + this.b.getIndex()
					+ " --X " + this.c.getIndex() + " ";
		case DM08:
			return "(8) " + this.a.getIndex() + "    \n" + "   / \\   \n"
					+ "  /   \\  \n" + " X     X \n" + " " + this.b.getIndex()
					+ " X-X " + this.c.getIndex() + " ";
		case DM09:
			return "(9) " + this.a.getIndex() + "    \n" + "   X X   \n"
					+ "  /   \\  \n" + " /     \\ \n" + " " + this.b.getIndex()
					+ " X-X " + this.c.getIndex() + " ";
		case DM10:
			return "(10) " + this.a.getIndex() + "    \n" + "    X \\   \n"
					+ "   /   \\  \n" + "  /     X \n" + "  "
					+ this.b.getIndex() + " X-X " + this.c.getIndex() + " ";
		case DM11:
			return "(11) " + this.a.getIndex() + "    \n" + "    X X   \n"
					+ "   /   \\  \n" + "  X     X \n" + "  "
					+ this.b.getIndex() + " --- " + this.c.getIndex() + " ";
		case DM12:
			return "(12) " + this.a.getIndex() + "    \n" + "    X X   \n"
					+ "   /   \\  \n" + "  X     X \n" + "  "
					+ this.b.getIndex() + " --X " + this.c.getIndex() + " ";
		case DM13:
			return "(13) " + this.a.getIndex() + "    \n" + "    X X   \n"
					+ "   /   \\  \n" + "  X     X \n" + "  "
					+ this.b.getIndex() + " X-X " + this.c.getIndex() + " ";
		default:
			return null;
		}
	}

	public String asStringFrom(String str) {
		String[] temp1 = str.split("\n");
		String[] temp2 = this.asString().split("\n");
		return temp1[0] + "     " + temp2[0] + "\n" + temp1[1] + "     "
				+ temp2[1] + "\n" + temp1[2] + " ==> " + temp2[2] + "\n"
				+ temp1[3] + "     " + temp2[3] + "\n" + temp1[4] + "     "
				+ temp2[4];
	}

	public static DirectedMotif getMotif(DirectedNode a, DirectedNode b,
			DirectedNode c) throws InvalidDirectedMotifException {
		if (a.getIndex() == b.getIndex() || a.getIndex() == c.getIndex()
				|| b.getIndex() == c.getIndex()) {
			throw new InvalidDirectedMotifException(a, b, c);
		}

		boolean ab = a.hasEdge(new DirectedEdge(a, b));
		boolean ac = a.hasEdge(new DirectedEdge(a, c));
		boolean ba = b.hasEdge(new DirectedEdge(b, a));
		boolean bc = b.hasEdge(new DirectedEdge(b, c));
		boolean ca = c.hasEdge(new DirectedEdge(c, a));
		boolean cb = c.hasEdge(new DirectedEdge(c, b));

		// 1
		if (ab && ac && !ba && !bc && !ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM01);
		}
		if (!ab && !ac && ba && bc && !ca && !cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM01);
		}
		if (!ab && !ac && !ba && !bc && ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM01);
		}
		// 2
		if (!ab && !ac && ba && !bc && ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM02);
		}
		if (ab && !ac && !ba && !bc && !ca && cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM02);
		}
		if (!ab && ac && !ba && bc && !ca && !cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM02);
		}
		// 3
		if (ab && !ac && !ba && bc && !ca && !cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM03);
		}
		if (!ab && ac && !ba && !bc && !ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM03);
		}
		if (!ab && ac && ba && !bc && !ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM03);
		}
		if (!ab && !ac && !ba && bc && ca && !cb) {
			return new DirectedMotif(c, b, a, DirectedMotifType.DM03);
		}
		if (ab && !ac && !ba && !bc && ca && !cb) {
			return new DirectedMotif(a, c, b, DirectedMotifType.DM03);
		}
		if (!ab && !ac && ba && !bc && !ca && cb) {
			return new DirectedMotif(b, c, a, DirectedMotifType.DM03);
		}
		// 4
		if (ab && ac && !ba && bc && !ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM04);
		}
		if (ab && ac && !ba && !bc && !ca && cb) {
			return new DirectedMotif(a, c, b, DirectedMotifType.DM04);
		}
		if (!ab && ac && ba && bc && !ca && !cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM04);
		}
		if (!ab && !ac && ba && bc && ca && !cb) {
			return new DirectedMotif(b, c, a, DirectedMotifType.DM04);
		}
		if (ab && !ac && !ba && !bc && ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM04);
		}
		if (!ab && !ac && ba && !bc && ca && cb) {
			return new DirectedMotif(c, b, a, DirectedMotifType.DM04);
		}
		// 5
		if (ab && ac && ba && !bc && !ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM05);
		}
		if (ab && ac && !ba && !bc && ca && !cb) {
			return new DirectedMotif(a, c, b, DirectedMotifType.DM05);
		}
		if (ab && !ac && ba && bc && !ca && !cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM05);
		}
		if (!ab && !ac && ba && bc && !ca && cb) {
			return new DirectedMotif(b, c, a, DirectedMotifType.DM05);
		}
		if (!ab && ac && !ba && !bc && ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM05);
		}
		if (!ab && !ac && !ba && bc && ca && cb) {
			return new DirectedMotif(c, b, a, DirectedMotifType.DM05);
		}
		// 6
		if (ab && !ac && ba && !bc && ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM06);
		}
		if (ab && !ac && ba && !bc && !ca && cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM06);
		}
		if (!ab && ac && ba && !bc && ca && !cb) {
			return new DirectedMotif(a, c, b, DirectedMotifType.DM06);
		}
		if (!ab && ac && !ba && bc && ca && !cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM06);
		}
		if (ab && !ac && !ba && bc && !ca && cb) {
			return new DirectedMotif(b, c, a, DirectedMotifType.DM06);
		}
		if (!ab && ac && !ba && bc && !ca && cb) {
			return new DirectedMotif(c, b, a, DirectedMotifType.DM06);
		}
		// 7
		if (ab && !ac && !ba && bc && ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM07);
		}
		if (!ab && ac && ba && !bc && !ca && cb) {
			return new DirectedMotif(a, c, b, DirectedMotifType.DM07);
		}
		// 8
		if (ab && ac && !ba && bc && !ca && cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM08);
		}
		if (!ab && ac && ba && bc && ca && !cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM08);
		}
		if (ab && !ac && ba && !bc && ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM08);
		}
		// 9
		if (!ab && !ac && ba && bc && ca && cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM09);
		}
		if (ab && ac && !ba && !bc && ca && cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM09);
		}
		if (ab && ac && ba && bc && !ca && !cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM09);
		}
		// 10
		if (ab && !ac && !ba && bc && ca && cb) {
			return new DirectedMotif(a, c, b, DirectedMotifType.DM10);
		}
		if (!ab && ac && ba && bc && !ca && cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM10);
		}
		if (!ab && ac && ba && !bc && ca && cb) {
			return new DirectedMotif(b, c, a, DirectedMotifType.DM10);
		}
		if (ab && ac && !ba && bc && ca && !cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM10);
		}
		if (ab && !ac && ba && bc && ca && !cb) {
			return new DirectedMotif(c, b, a, DirectedMotifType.DM10);
		}
		if (ab && ac && ba && !bc && !ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM10);
		}
		// 11
		if (ab && ac && ba && !bc && ca && !cb) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM11);
		}
		if (ab && !ac && ba && bc && !ca && cb) {
			return new DirectedMotif(b, a, c, DirectedMotifType.DM11);
		}
		if (!ab && ac && !ba && bc && ca && cb) {
			return new DirectedMotif(c, a, b, DirectedMotifType.DM11);
		}

		int sum = (ab ? 1 : 0) + (ac ? 1 : 0) + (ba ? 1 : 0) + (bc ? 1 : 0)
				+ (ca ? 1 : 0) + (cb ? 1 : 0);

		// 12
		if (sum == 5) {
			if (!ab) {
				return new DirectedMotif(c, b, a, DirectedMotifType.DM12);
			} else if (!ac) {
				return new DirectedMotif(b, c, a, DirectedMotifType.DM12);
			} else if (!ba) {
				return new DirectedMotif(c, a, b, DirectedMotifType.DM12);
			} else if (!bc) {
				return new DirectedMotif(a, c, b, DirectedMotifType.DM12);
			} else if (!ca) {
				return new DirectedMotif(b, a, c, DirectedMotifType.DM12);
			} else if (!cb) {
				return new DirectedMotif(a, b, c, DirectedMotifType.DM12);
			}
		}
		// 13
		if (sum == 6) {
			return new DirectedMotif(a, b, c, DirectedMotifType.DM13);
		}

		throw new InvalidDirectedMotifException(a, b, c);
	}

	public static DirectedMotifType getType(DirectedNode a, DirectedNode b,
			DirectedNode c) throws InvalidDirectedMotifException {
		if (a.getIndex() == b.getIndex() || a.getIndex() == c.getIndex()
				|| b.getIndex() == c.getIndex()) {
			throw new InvalidDirectedMotifException(a, b, c);
		}

		boolean ab = a.hasEdge(new DirectedEdge(a, b));
		boolean ac = a.hasEdge(new DirectedEdge(a, c));
		boolean ba = b.hasEdge(new DirectedEdge(b, a));
		boolean bc = b.hasEdge(new DirectedEdge(b, c));
		boolean ca = c.hasEdge(new DirectedEdge(c, a));
		boolean cb = c.hasEdge(new DirectedEdge(c, b));

		// 1
		if (ab && ac && !ba && !bc && !ca && !cb) {
			return DirectedMotifType.DM01;
		}
		if (!ab && !ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM01;
		}
		if (!ab && !ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM01;
		}
		// 2
		if (!ab && !ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM02;
		}
		if (ab && !ac && !ba && !bc && !ca && cb) {
			return DirectedMotifType.DM02;
		}
		if (!ab && ac && !ba && bc && !ca && !cb) {
			return DirectedMotifType.DM02;
		}
		// 3
		if (ab && !ac && !ba && bc && !ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && ac && !ba && !bc && !ca && cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && ac && ba && !bc && !ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && !ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (ab && !ac && !ba && !bc && ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && !ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM03;
		}
		// 4
		if (ab && ac && !ba && bc && !ca && !cb) {
			return DirectedMotifType.DM04;
		}
		if (ab && ac && !ba && !bc && !ca && cb) {
			return DirectedMotifType.DM04;
		}
		if (!ab && ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM04;
		}
		if (!ab && !ac && ba && bc && ca && !cb) {
			return DirectedMotifType.DM04;
		}
		if (ab && !ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM04;
		}
		if (!ab && !ac && ba && !bc && ca && cb) {
			return DirectedMotifType.DM04;
		}
		// 5
		if (ab && ac && ba && !bc && !ca && !cb) {
			return DirectedMotifType.DM05;
		}
		if (ab && ac && !ba && !bc && ca && !cb) {
			return DirectedMotifType.DM05;
		}
		if (ab && !ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM05;
		}
		if (!ab && !ac && ba && bc && !ca && cb) {
			return DirectedMotifType.DM05;
		}
		if (!ab && ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM05;
		}
		if (!ab && !ac && !ba && bc && ca && cb) {
			return DirectedMotifType.DM05;
		}
		// 6
		if (ab && !ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM06;
		}
		if (ab && !ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM06;
		}
		if (!ab && ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM06;
		}
		if (!ab && ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM06;
		}
		if (ab && !ac && !ba && bc && !ca && cb) {
			return DirectedMotifType.DM06;
		}
		if (!ab && ac && !ba && bc && !ca && cb) {
			return DirectedMotifType.DM06;
		}
		// 7
		if (ab && !ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM07;
		}
		if (!ab && ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM07;
		}
		// 8
		if (ab && ac && !ba && bc && !ca && cb) {
			return DirectedMotifType.DM08;
		}
		if (!ab && ac && ba && bc && ca && !cb) {
			return DirectedMotifType.DM08;
		}
		if (ab && !ac && ba && !bc && ca && cb) {
			return DirectedMotifType.DM08;
		}
		// 9
		if (!ab && !ac && ba && bc && ca && cb) {
			return DirectedMotifType.DM09;
		}
		if (ab && ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM09;
		}
		if (ab && ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM09;
		}
		// 10
		if (ab && !ac && !ba && bc && ca && cb) {
			return DirectedMotifType.DM10;
		}
		if (!ab && ac && ba && bc && !ca && cb) {
			return DirectedMotifType.DM10;
		}
		if (!ab && ac && ba && !bc && ca && cb) {
			return DirectedMotifType.DM10;
		}
		if (ab && ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM10;
		}
		if (ab && !ac && ba && bc && ca && !cb) {
			return DirectedMotifType.DM10;
		}
		if (ab && ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM10;
		}
		// 11
		if (!ab && ac && !ba && bc && ca && cb) {
			return DirectedMotifType.DM11;
		}
		if (ab && !ac && ba && bc && !ca && cb) {
			return DirectedMotifType.DM11;
		}
		if (ab && ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM11;
		}

		int sum = (ab ? 1 : 0) + (ac ? 1 : 0) + (ba ? 1 : 0) + (bc ? 1 : 0)
				+ (ca ? 1 : 0) + (cb ? 1 : 0);

		// 12
		if (sum == 5) {
			return DirectedMotifType.DM12;
		}
		// 13
		if (sum == 6) {
			return DirectedMotifType.DM13;
		}

		throw new InvalidDirectedMotifException(a, b, c);
	}

}
