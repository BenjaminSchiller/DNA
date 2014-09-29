package dna.metricsNew.richClub;

import java.util.HashMap;

import dna.graph.nodes.Node;

public class DegreeRichClubs {
	public HashMap<Integer, DegreeRichClub> clubs;

	public DegreeRichClub first;

	public HashMap<Node, DegreeRichClub> nodeClubs;

	public DegreeRichClubs() {
		this.clubs = new HashMap<Integer, DegreeRichClub>();
		this.nodeClubs = new HashMap<Node, DegreeRichClub>();

		this.first = new DegreeRichClub(this, 0);
		this.clubs.put(0, this.first);
	}

	public void addNext() {
		DegreeRichClub rc = new DegreeRichClub(this, this.first.degree + 1);
		this.clubs.put(rc.degree, rc);

		rc.next = this.first;
		this.first.previous = rc;

		this.first = rc;
	}

	public void addUntilDegree(int degree) {
		while (this.first.degree < degree) {
			this.addNext();
		}
	}

	public DegreeRichClub getClubByDegree(int degree) {
		if (this.first.degree < degree) {
			this.addUntilDegree(degree);
			return this.first;
		} else if (this.first.degree == degree) {
			return this.first;
		} else {
			return this.clubs.get(degree);
		}
	}

	public void removeFirst() {
		this.clubs.remove(this.first.degree);
		this.first.next.previous = null;
		this.first = this.first.next;
	}

	public void removeEmpty() {
		while (this.first.size() == 0) {
			this.removeFirst();
		}
	}
}
