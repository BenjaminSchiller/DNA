package Graph;

public abstract class Edge extends Element implements IElement {
	private int index;
	protected double weight;
	
	public int getIndex() {
		return index;
	}
	
	public void setWeight(double w) {
		this.weight = w;
	}
}
