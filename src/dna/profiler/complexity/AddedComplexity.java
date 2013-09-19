package dna.profiler.complexity;


/**
 * Complexity that is combined of two other complexities
 * @author Nico
 *
 */
public class AddedComplexity extends Complexity {

	private Complexity first;
	private Complexity second;

	public AddedComplexity(Complexity first,
			Complexity second) {
		this.first = first;
		this.second = second;
	}

}
