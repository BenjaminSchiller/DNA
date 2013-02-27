package dna.series;

/**
 * 
 * Exception used to indicate that given object cannot be aggregated by the
 * Aggregation class (inconsistencies, different number of values, etc.)
 * 
 * @author benni
 * 
 */
public class AggregationException extends Exception {

	private static final long serialVersionUID = -8857056466565331874L;

	public AggregationException(String msg) {
		super(msg);
	}

}
