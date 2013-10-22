package dna.profiler.complexity;


/**
 * Complexity that is combined of two other complexities
 * 
 * @author Nico
 * 
 */
public class AddedComplexity extends Complexity {

	private Complexity first;
	private Complexity second;

	public AddedComplexity(Complexity first, Complexity second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public void setCounter(int c) {
		this.first.setCounter(c);
		this.second.setCounter(c);
	}

	@Override
	public int getComplexityCounter() {
		return this.first.getComplexityCounter()
				+ this.second.getComplexityCounter();
	}

	@Override
	public String getComplexity() {
		return this.first.getComplexity() + " + " + this.second.getComplexity();
	}

	@Override
	public ComplexityMap getComplexityMap() {
		ComplexityMap res = first.getComplexityMap();
		ComplexityMap resSecond = second.getComplexityMap();
		res.add(resSecond);
		return res;
	}

	@Override
	public ComplexityMap getWeightedComplexityMap() {
		ComplexityMap res = first.getWeightedComplexityMap();
		ComplexityMap resSecond = second
				.getWeightedComplexityMap();
		res.add(resSecond);
		return res;
	}
}
