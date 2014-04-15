package dna.graph.weights;

/**
 * 
 * 3-dimensional int weight holding three int values (x, y, and z).
 * 
 * @author benni
 * 
 */
public class Int3dWeight extends Weight {
	private int x;
	private int y;
	private int z;

	public Int3dWeight(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Int3dWeight() {
		this(0, 0, 0);
	}

	public Int3dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Integer.parseInt(temp[0]);
		this.y = Integer.parseInt(temp[1]);
		this.z = Integer.parseInt(temp[2]);
	}

	public Int3dWeight(WeightSelection ws) {
		this.x = IntWeight.getIntWeight(ws);
		this.y = IntWeight.getIntWeight(ws);
		this.z = IntWeight.getIntWeight(ws);
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public String asString() {
		return this.x + Weight.WeightSeparator + this.y
				+ Weight.WeightSeparator + this.z;
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.I3;
	}

	@Override
	public Object getWeight() {
		return new int[] { x, y, z };
	}

}
