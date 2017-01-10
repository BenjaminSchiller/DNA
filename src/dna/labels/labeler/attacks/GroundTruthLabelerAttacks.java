package dna.labels.labeler.attacks;

import java.io.IOException;

import dna.labels.labeler.list.ListLabeler;
import dna.labels.labeler.util.IntervalLabeler;

public class GroundTruthLabelerAttacks extends ListLabeler {

	protected int edgeLifeTimeSeconds;
	protected boolean init = false;

	public GroundTruthLabelerAttacks(String name, String dir, String filename, int edgeLifeTimeSeconds) {
		super(name, dir, filename);
		this.edgeLifeTimeSeconds = edgeLifeTimeSeconds;
		this.init = true;

		try {
			this.labeler = parseLabelerFromList(name, dir, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected IntervalLabeler parseLabelerFromString(String name, String line) {
		if (this.init) {
			String[] splits = line.split("\t");
			long timestamp = Long.parseLong(splits[0]);
			String value = splits[3];
			return new IntervalLabeler(this.getName(), "0-1", value, timestamp, timestamp + this.edgeLifeTimeSeconds);
		} else {
			return new IntervalLabeler("", 0, 0);
		}
	}

}
