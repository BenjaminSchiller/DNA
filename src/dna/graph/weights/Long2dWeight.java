package dna.graph.weights;

/**
 * 
 * 2-dimensional long weight holding two long values (x and y).
 * 
 * @author benni
 * 
 */
public class Long2dWeight extends Weight {

	private long x;
	private long y;

	public Long2dWeight(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public Long2dWeight() {
		this(0, 0);
	}

	public Long2dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Long.parseLong(temp[0]);
		this.y = Long.parseLong(temp[1]);
	}

	public Long2dWeight(WeightSelection ws) {
		this.x = LongWeight.getLongWeight(ws);
		this.y = LongWeight.getLongWeight(ws);
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

	@Override
	public String asString() {
		return this.x + Weight.WeightSeparator + this.y;
	}

	@Override
	public Object getWeight() {
		return new long[] { this.x, this.y };
	}

}
