package dna.graph.weightsNew;

/**
 * 
 * 3-dimensional int weight holding three int values (x, y, and z).
 * 
 * @author benni
 * 
 */
public class Int3dWeight extends Weight {
	private int x;

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	private int y;

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	private int z;

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Int3dWeight(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Int3dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Integer.parseInt(temp[0]);
		this.y = Integer.parseInt(temp[1]);
		this.z = Integer.parseInt(temp[2]);
	}

	@Override
	protected String asString_() {
		return this.x + Weight.WeightSeparator + this.y
				+ Weight.WeightSeparator + this.z;
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.I3;
	}

}
