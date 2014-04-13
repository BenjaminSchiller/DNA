package dna.graph.weightsNew;

/**
 * 
 * 2-dimensional double weight holding two double values (x and y).
 * 
 * @author benni
 * 
 */
public class Double2dWeight extends Weight {

	private double x;
	private double y;

	public Double2dWeight(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Double2dWeight() {
		this(0, 0);
	}

	public Double2dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Double.parseDouble(temp[0]);
		this.y = Double.parseDouble(temp[1]);
	}
	
	public Double2dWeight(WeightSelection ws) {
		this.x = DoubleWeight.getDoubleWeight(ws);
		this.y = DoubleWeight.getDoubleWeight(ws);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String asString() {
		return this.x + Weight.WeightSeparator + this.y;
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.D2;
	}

}
