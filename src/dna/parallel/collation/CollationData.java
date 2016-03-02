package dna.parallel.collation;

import dna.parallel.auxData.AuxData;
import dna.series.data.BatchData;

/**
 * 
 * This class holds all data required for the processing of a single collation
 * step, i.e., the batchData object holding the results comuted by the workers
 * and the auxiliaryData generated during partitioning.
 * 
 * @author benni
 *
 */
@SuppressWarnings("rawtypes")
public class CollationData {
	public BatchData[] bd;
	public AuxData aux;

	public CollationData(BatchData[] bd, AuxData aux) {
		this.bd = bd;
		this.aux = aux;
	}
}
