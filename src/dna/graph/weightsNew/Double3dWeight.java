package dna.graph.weightsNew;

/**
 * 
 * 3-dimensional double weight holding three double values (x, y, and z).
 * 
 * @author benni
 * 
 */
public class Double3dWeight extends Weight {

	private double x;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	private double y;

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	private double z;

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Double3dWeight(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Double3dWeight() {
		this(0, 0, 0);
	}

	public Double3dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Double.parseDouble(temp[0]);
		this.y = Double.parseDouble(temp[1]);
		this.z = Double.parseDouble(temp[2]);
	}

	@Override
	protected String asString_() {
		return this.x + Weight.WeightSeparator + this.y
				+ Weight.WeightSeparator + this.z;
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.D3;
	}

}
