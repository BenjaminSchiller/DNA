package dna.parallel.collation;

import dna.parallel.auxData.AuxData;
import dna.series.data.BatchData;

@SuppressWarnings("rawtypes")
public class CollationData {
	public BatchData[] bd;
	public AuxData aux;

	public CollationData(BatchData[] bd, AuxData aux) {
		this.bd = bd;
		this.aux = aux;
	}
}
