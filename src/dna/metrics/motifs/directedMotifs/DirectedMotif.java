package dna.metrics.motifs.directedMotifs;

import java.util.Arrays;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeAdditionException;

public class DirectedMotif {
	public static enum DirectedMotifType {
		DM01, DM02, DM03, DM04, DM05, DM06, DM07, DM08, DM09, DM10, DM11, DM12, DM13
	}

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

	public int getIndex() {
		switch (this.type) {
		case DM01:
			return 1;
		case DM02:
			return 2;
		case DM03:
			return 3;
		case DM04:
			return 4;
		case DM05:
			return 5;
		case DM06:
			return 6;
		case DM07:
			return 7;
		case DM08:
			return 8;
		case DM09:
			return 9;
		case DM10:
			return 10;
		case DM11:
			return 11;
		case DM12:
			return 12;
		case DM13:
			return 13;
		default:
			return 0;

		}
	}

	public int getEdgeCount() {
		switch (this.type) {
		case DM01:
			return 2;
		case DM02:
			return 2;
		case DM03:
			return 2;
		case DM04:
			return 3;
		case DM05:
			return 3;
		case DM06:
			return 3;
		case DM07:
			return 3;
		case DM08:
			return 4;
		case DM09:
			return 4;
		case DM10:
			return 4;
		case DM11:
			return 4;
		case DM12:
			return 5;
		case DM13:
			return 6;
		default:
			return 0;

		}
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
		case DM11:
			if (this.connects(e, b, c)) {
				this.type = DirectedMotifType.DM12;
				return true;
			} else if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM12;
				this.changeBC();
				return true;
			}
		case DM12:
			if (this.connects(e, c, b)) {
				this.type = DirectedMotifType.DM13;
				return true;
			}
		case DM13:
		default:
		}
		throw new DirectedMotifInvalidEdgeAdditionException(this, e);
	}

	public boolean removeEdge(DirectedEdge e) {
		switch (this.type) {
		case DM01:
			// TODO implement
			return false;
		case DM02:
			// TODO implement
			return false;
		case DM03:
			// TODO implement
			return false;
		case DM04:
			// TODO implement
			return false;
		case DM05:
			// TODO implement
			return false;
		case DM06:
			// TODO implement
			return false;
		case DM07:
			// TODO implement
			return false;
		case DM08:
			// TODO implement
			return false;
		case DM09:
			// TODO implement
			return false;
		case DM10:
			// TODO implement
			return false;
		case DM11:
			// TODO implement
			return false;
		case DM12:
			// TODO implement
			return false;
		case DM13:
			// TODO implement
			return false;
		default:
			return false;
		}
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

}
