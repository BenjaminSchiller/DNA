package genericsWithTest;

public class Edge implements Element {
	private int index;
	
	public Edge getDummy() {
		return new Edge();
	}
	
	public int getIndex() {
		return index;
	}
}
