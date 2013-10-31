package dna.metrics.connectedComponents;


public class UndirectedComponent {

	private int index;
	private int size;

	public UndirectedComponent(int index) {
		this.index = index;
		this.size = 0;
	}

	public int getIndex() {
		return this.index;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void decreaseSize(int size) {
		this.size -= size;
	}

	public void increaseSize(int size) {
		this.size += size;
	}

}
