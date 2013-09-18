package dna.profiler.complexity;

import dna.profiler.complexity.ComplexityClass;

/**
 * Complexity that is combined of two other complexities
 * @author Nico
 *
 */
public class AddedComplexity extends ComplexityClass {

	private ComplexityClass first;
	private ComplexityClass second;

	public AddedComplexity(ComplexityClass first,
			ComplexityClass second) {
		this.first = first;
		this.second = second;
	}

}
