package genericsWithTest;

public class Node implements Element {
	private int index;
	
	public Node getDummy() {
		return new Node();
	}
	
	public int getIndex() {
		return index;
	}
}
