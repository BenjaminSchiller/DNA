package dna.labels.labeler;

import dna.series.Series;
import dna.updates.batch.Batch;

public class LabelerNotApplicableException extends Exception {

	private static final long serialVersionUID = 3273344957590426563L;

	public LabelerNotApplicableException(Labeler l, Series s) {
		super("labeller " + l.getName() + " is not applicable to series "
				+ s.getName());
	}

	public LabelerNotApplicableException(Labeler l, Batch b) {
		super("labeller " + l.getName() + " is not applicable to batch "
				+ b.toString());
	}

}
