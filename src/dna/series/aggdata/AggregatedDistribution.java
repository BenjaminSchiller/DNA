package dna.series.aggdata;

import java.io.IOException;

import dna.io.Writer;
import dna.io.etc.Keywords;

/**
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
 * Array structure as follows: values = { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 *
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedDistribution extends AggregatedData {

	// constructors
	public AggregatedDistribution(String name) {
		super(name);
	}
	
	public AggregatedDistribution(String name, double[] values) {
		super(name, values);
	}

}
