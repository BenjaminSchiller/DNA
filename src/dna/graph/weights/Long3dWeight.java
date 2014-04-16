package dna.graph.weights;

/**
 * 
 * 3-dimensional long weight holding three long values (x, y, and z).
 * 
 * @author benni
 * 
 */
public class Long3dWeight extends Weight {

	private long x;
	private long y;
	private long z;

	public Long3dWeight(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Long3dWeight() {
		this(0, 0, 0);
	}

	public Long3dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Long.parseLong(temp[0]);
		this.y = Long.parseLong(temp[1]);
		this.z = Long.parseLong(temp[2]);
	}

	public Long3dWeight(WeightSelection ws) {
		this.x = LongWeight.getLongWeight(ws);
		this.y = LongWeight.getLongWeight(ws);
		this.z = LongWeight.getLongWeight(ws);
	}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

	public long getZ() {
		return z;
	}

	public void setZ(long z) {
		this.z = z;
	}

	@Override
	public String asString() {
		return this.x + Weight.WeightSeparator + this.y
				+ Weight.WeightSeparator + this.z;
	}

	@Override
	public Object getWeight() {
		return new long[] { this.x, this.y, this.z };
	}

}
