package dna.metrics.connectivity;

public class WeakComponent {

	protected int index;
	protected int size;

	public WeakComponent(int index) {
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
