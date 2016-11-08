package dna.labels.labeler.list;

import java.io.IOException;
import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.io.Reader;
import dna.labels.Label;
import dna.labels.labeler.Labeler;
import dna.labels.labeler.util.IntervalLabeler;
import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

/**
 * A ListLabeler is labeler who labels batches based on a list of labels,
 * contained in a file.
 * 
 * @author Rwilmes
 * 
 */
public abstract class ListLabeler extends Labeler {

	// labeler-list
	protected ArrayList<IntervalLabeler> labeler;

	public ListLabeler(String name, String dir, String filename) {
		super(name);

		try {
			this.labeler = parseLabelerFromList(name, dir, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Parses a list of labelers from file. **/
	protected ArrayList<IntervalLabeler> parseLabelerFromList(String name,
			String dir, String filename) throws IOException {
		ArrayList<IntervalLabeler> labeler = new ArrayList<IntervalLabeler>();

		Reader r = new Reader(dir, filename);
		String line = r.readString();
		while (line != null) {
			labeler.add(parseLabelerFromString(name, line));
			line = r.readString();
		}

		r.close();
		return labeler;
	}

	/**
	 * Parses a labeler from line. Should be overriden by each
	 * ListLabeler-implementation to realise desired behaviour.
	 **/
	protected abstract IntervalLabeler parseLabelerFromString(String name,
			String line);

	@Override
	public boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics) {
		return true;
	}

	@Override
	public ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics) {
		ArrayList<Label> list = new ArrayList<Label>();
		for (Labeler labeler : this.labeler) {
			for (Label l : labeler.computeLabels(g, batch, batchData, metrics)) {
				if (!list.contains(l))
					list.add(l);
			}
		}

		return list;
	}

}
