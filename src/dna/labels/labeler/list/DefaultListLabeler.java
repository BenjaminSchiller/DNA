package dna.labels.labeler.list;

import dna.labels.labeler.util.IntervalLabeler;

/**
 * Default implementation of the ListLabeler.<br>
 * <br>
 * 
 * Parses Labels of the following format:
 * 
 * $type$ \t $timestamp_from$ \t $timestamp_to$<br>
 * <br>
 * 
 * Note: If timestamp_to is not present or can not be parsed the timestamp_from
 * will be used as upper bound.
 * 
 * @author Rwilmes
 * 
 */
public class DefaultListLabeler extends ListLabeler {

	public DefaultListLabeler(String dir, String filename) {
		this("DefaultListLabeler", dir, filename);
	}

	public DefaultListLabeler(String name, String dir, String filename) {
		super(name, dir, filename);
	}

	/**
	 * Parses a Labeler from line. Format should be:<br>
	 * <br>
	 * 
	 * $type$ \t $timestamp_from$ \t $timestamp_to$
	 */
	@Override
	protected IntervalLabeler parseLabelerFromString(String name, String line) {
		String[] splits = line.split("\t");
		String type = splits[0];
		long from = Long.parseLong(splits[1]);
		long to = -1;
		if (splits.length > 2) {
			try {
				to = Long.parseLong(splits[2]);
			} catch (NumberFormatException e) {
			}
		}

		System.out.println("type: " + type + "\t" + from + "\t" + to);

		if (to < 0)
			return new IntervalLabeler(name, type, "true", from, from);
		else
			return new IntervalLabeler(name, type, "true", from, to);
	}
}
