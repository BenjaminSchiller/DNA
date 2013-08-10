package genericsWithTest;

public class UndirectedNode extends Node {

	public UndirectedNode(int index) {
		super(index);
	}

	public UndirectedNode(String str) {
		super(str);
	}

	public abstract int getDegree();

	public void print() {
		System.out.println(this.toString());
		System.out.println("Edges: " + this.getEdges());
	}

	@Override
	public boolean hasEdge(Edge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEdge(Edge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEdge(Edge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Edge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub

	}

}
