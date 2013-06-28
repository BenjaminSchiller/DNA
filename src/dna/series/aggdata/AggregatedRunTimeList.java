package dna.series.aggdata;

/**
 * An AggregatedRunTimeList object contains aggregated values of a RunTimeList.
 * 
 * @author Rwilmes
 * @date 27.06.2013
 */
public class AggregatedRunTimeList extends AggregatedData {

	// member variables
	private AggregatedValue[] values;
	
	// constructors
	public AggregatedRunTimeList(String name) {
		super(name);
	}
	
	public AggregatedRunTimeList(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}
	
	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}
	
	
}
