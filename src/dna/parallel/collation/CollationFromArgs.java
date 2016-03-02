package dna.parallel.collation;

import dna.parallel.collation.clustering.UndirectedClusteringCoefficientNodeCutCollation;
import dna.parallel.collation.clustering.UndirectedClusteringCoefficientSeparatedCollation;
import dna.parallel.collation.clustering.UndirectedClusteringCoefficientOverlappingCollation;
import dna.parallel.util.Sleeper;

/**
 * 
 * This class provides an enumeration of all implemented collations. The static
 * parse method takes the collation type from this enumeration in addition to
 * other required parameters and returns an instance of a Collation class. This
 * allows the quick instantiation of collation objects from string arguments,
 * e.g., passed down from the cmd line.
 * 
 * @author benni
 *
 */
public class CollationFromArgs {
	public static enum CollationType {
		UndirectedClusteringCoefficientNodeCut, UndirectedClusteringCoefficientSeparated, UndirectedClusteringCoefficientOverlapping
	}

	/**
	 * 
	 * returns an instance of the collation specified by the type
	 * 
	 * @param collationType
	 *            type of the collation to create
	 * @param auxDir
	 *            dir where the auxiliary data is stored
	 * @param inputDir
	 *            dir where the computed data from workers is stored
	 * @param partitionCount
	 *            number of partitions
	 * @param run
	 *            index of the run that should be collated
	 * @param sleeper
	 *            instance of the sleeper class which specified how long to
	 *            sleep and weit for input data to appear
	 * @param args
	 *            additional arguments (specific for each type)
	 * @return instance of the specified collation
	 */
	@SuppressWarnings("rawtypes")
	public static Collation parse(CollationType collationType, String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper,
			String... args) {
		switch (collationType) {
		case UndirectedClusteringCoefficientNodeCut:
			return new UndirectedClusteringCoefficientNodeCutCollation(auxDir,
					inputDir, partitionCount, run, sleeper);
		case UndirectedClusteringCoefficientSeparated:
			return new UndirectedClusteringCoefficientSeparatedCollation(
					auxDir, inputDir, partitionCount, run, sleeper);
		case UndirectedClusteringCoefficientOverlapping:
			return new UndirectedClusteringCoefficientOverlappingCollation(
					auxDir, inputDir, partitionCount, run, sleeper);
		default:
			throw new IllegalArgumentException("invalid collation type: "
					+ collationType);
		}
	}
}
