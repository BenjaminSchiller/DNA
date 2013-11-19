package dna.metrics.connectedComponents;


public class Component {

	private int index;
	private int size;

	public Component(int index) {
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
