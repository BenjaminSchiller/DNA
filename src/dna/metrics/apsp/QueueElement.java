package dna.metrics.apsp;

public class QueueElement<E> implements Comparable<E> {

	public final int distance;
	public final E e;

	public QueueElement(E e, Integer d) {
		this.e = e;
		this.distance = d;
	}

	@Override
	public int compareTo(E o) {
		QueueElement<E> other_ = (QueueElement<E>) o;
		Integer diff = this.distance - other_.distance;
		return diff.intValue();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof QueueElement)) {
			return false;
		}
		QueueElement<E> other_ = (QueueElement<E>) other;
		return other_.e == this.e;
	}
}
