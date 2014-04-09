package dna.graph.weightsNew;

/**
 * 
 * 2-dimensional int weight holding two int values (x and y).
 * 
 * @author benni
 * 
 */
public class Int2dWeight extends Weight {

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

	public Int2dWeight(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Int2dWeight() {
		this(0, 0);
	}

	public Int2dWeight(String str) {
		String[] temp = str.split(Weight.WeightSeparator);
		this.x = Integer.parseInt(temp[0]);
		this.y = Integer.parseInt(temp[1]);
	}

	@Override
	protected String asString_() {
		return this.x + Weight.WeightSeparator + this.y;
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.I2;
	}

}
