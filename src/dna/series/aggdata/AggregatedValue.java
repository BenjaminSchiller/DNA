package dna.series.aggdata;

/**
 * AggregatedValue is a class containing the aggregated values of a list of values.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedValue extends AggregatedData {

	// class variables
	private String name;
	private String type = "AggregatedValue";
	private double value;
	private double[] values; // AggregatedValue array structure:  { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
	
	// constructors
	public AggregatedValue(String name) {
		this.name = name;
	}
	
	public AggregatedValue(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public AggregatedValue(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
	// class methods
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public double[] getValues() {
		return this.values;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}

}
