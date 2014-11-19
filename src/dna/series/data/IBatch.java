package dna.series.data;

/**
 * Interface for BatchData and AggregatedBatch.
 * 
 * @author Rwilmes
 * @date 10.11.2014
 */
public interface IBatch {

	// getters
	public long getTimestamp();

	// utility methods
	public boolean contains(String domain, String value);
}
