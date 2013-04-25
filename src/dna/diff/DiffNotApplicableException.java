package dna.diff;

import dna.graph.old.OldGraph;
import dna.metrics.Metric;

public class DiffNotApplicableException extends Exception {

	private static final long serialVersionUID = 8040477205471869212L;

	public DiffNotApplicableException(Metric m, Diff d) {
		this(DiffNotApplicableException.getMsg(m, d) + "\n" + m.getGraph()
				+ "\n" + d + "\n" + m);
	}

	public DiffNotApplicableException(OldGraph g, Diff d) {
		this(DiffNotApplicableException.getMsg(g, d) + "\n" + g + "\n" + d);
	}

	public DiffNotApplicableException(String msg) {
		super("Diff cannot be applied - " + msg);
	}

	private static String getMsg(OldGraph g, Diff d) {
		if (g.getNodes().length != d.getNodes()) {
			return "# of nodes G-" + g.getNodes().length + " != "
					+ d.getNodes() + "-D";
		}
		if (g.getTimestamp() != d.getFrom()) {
			return "timestamp G-" + g.getTimestamp() + " != " + d.getFrom()
					+ "-D";
		}
		return null;
	}

	private static String getMsg(Metric m, Diff d) {
		if (m.getNodes() != d.getNodes()) {
			return "# of nodes M-" + m.getNodes() + " != " + d.getNodes()
					+ "-D";
		}
		if (m.getTimestamp() != d.getFrom() && m.getTimestamp() != d.getTo()) {
			return "timestamp M-" + m.getTimestamp() + " != " + d.getFrom()
					+ "-D";
		}
		return null;
	}

}
