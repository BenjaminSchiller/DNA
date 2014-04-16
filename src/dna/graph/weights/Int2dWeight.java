package dna.graph.weights;

/**
 * 
 * 2-dimensional int weight holding two int values (x and y).
 * 
 * @author benni
 * 
 */
public class Int2dWeight extends Weight {

	private int x;
	private int y;

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

	public Int2dWeight(WeightSelection ws) {
		this.x = IntWeight.getIntWeight(ws);
		this.y = IntWeight.getIntWeight(ws);
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

	@Override
	public String asString() {
		return this.x + Weight.WeightSeparator + this.y;
	}

	@Override
	public Object getWeight() {
		return new int[] { x, y };
	}

}
