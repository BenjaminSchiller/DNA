package dna.labels;

import dna.series.Series;
import dna.updates.batch.Batch;

public class LabellerNotApplicableException extends Exception {

	private static final long serialVersionUID = 3273344957590426563L;

	public LabellerNotApplicableException(Labeller l, Series s) {
		super("labeller " + l.getName() + " is not applicable to series "
				+ s.getName());
	}

	public LabellerNotApplicableException(Labeller l, Batch b) {
		super("labeller " + l.getName() + " is not applicable to batch "
				+ b.toString());
	}

}
