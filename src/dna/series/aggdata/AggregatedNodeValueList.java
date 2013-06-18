package dna.series.aggdata;


/**
 * AggregatedNodeValueList is a class containing the values of an aggregated NodeValueList.
 * Array structure as follows: values = { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedNodeValueList extends AggregatedData {

	// constructors
	public AggregatedNodeValueList(String name) {
		super(name);
	}
	
	public AggregatedNodeValueList(String name, double[] values) {
		super(name, values);
	}

}
