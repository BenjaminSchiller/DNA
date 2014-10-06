package dna.metrics.richClub;

import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterNR;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class RichClubConnectivityByDegreeU extends RichClubConnectivityByDegree
		implements IBeforeNA, IBeforeNR, IAfterNR, IBeforeEA, IAfterEA,
		IBeforeER, IAfterER {

	public RichClubConnectivityByDegreeU() {
		super("RichClubConnectivityByDegreeU");
	}

	private DegreeRichClubs rcs;

	@Override
	public boolean init() {
		this.rcs = this.compute();
		return this.rcs != null;
	}

	/**
	 * NA
	 */

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		this.rcs.getClubByDegree(0).addNode((Node) na.getNode());
		this.nodeCount[this.nodeCount.length - 1]++;
		return true;
	}

	/**
	 * NR
	 */

	// TODO implement node removal

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * EA
	 */

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		Node n1 = ea.getEdge().getN1();
		Node n2 = ea.getEdge().getN2();

		DegreeRichClub rc1 = this.rcs.getClubByDegree(n1.getDegree());
		DegreeRichClub rc2 = this.rcs.getClubByDegree(n2.getDegree());

		rc1.removeNode(n1);
		rc2.removeNode(n2);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		Node n1 = ea.getEdge().getN1();
		Node n2 = ea.getEdge().getN2();

		DegreeRichClub rc1 = this.rcs.getClubByDegree(n1.getDegree());
		DegreeRichClub rc2 = this.rcs.getClubByDegree(n2.getDegree());

		rc1.addNode(n1);
		rc2.addNode(n2);

		this.fill(this.rcs);

		return true;
	}

	/**
	 * ER
	 */

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		Node n1 = er.getEdge().getN1();
		Node n2 = er.getEdge().getN2();

		DegreeRichClub rc1 = this.rcs.getClubByDegree(n1.getDegree());
		DegreeRichClub rc2 = this.rcs.getClubByDegree(n2.getDegree());

		rc1.removeNode(n1);
		rc2.removeNode(n2);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		Node n1 = er.getEdge().getN1();
		Node n2 = er.getEdge().getN2();

		DegreeRichClub rc1 = this.rcs.getClubByDegree(n1.getDegree());
		DegreeRichClub rc2 = this.rcs.getClubByDegree(n2.getDegree());

		rc1.addNode(n1);
		rc2.addNode(n2);

		this.rcs.removeEmpty();

		this.fill(this.rcs);

		return true;
	}
}
