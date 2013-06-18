package dna.series.aggdata;

/**
 * AggregatedValue is a class containing the aggregated values of a list of values.
 * Array structure as follows: values = { avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedValue extends AggregatedData {

	// constructors
	public AggregatedValue(String name) {
		super(name);
	}
	
	public AggregatedValue(String name, double[] values) {
		super(name, values);
	}

}
