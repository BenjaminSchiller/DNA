package dna.series.data.lists;

public class LongList {
	protected long[] values;

	public LongList(long[] values) {
		this.values = values;
	}

	public LongList(int length) {
		this(new long[length]);
	}

	public int size() {
		return this.values.length;
	}

	public long[] getValues() {
		return this.values;
	}

	public long getValue(int index) {
		try {
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			return Long.MIN_VALUE;
		}
	}

	public void setValue(int index, long value) {
		try {
			this.values[index] = value;
		} catch (ArrayIndexOutOfBoundsException e) {
			long[] temp = new long[index + 1];
			System.arraycopy(this.values, 0, temp, 0, this.values.length);
			temp[index] = value;
			this.values = temp;
		}
	}

	public long div(int index, long value) {
		try {
			this.values[index] /= value;
		} catch (ArrayIndexOutOfBoundsException e) {
			long[] temp = new long[index + 1];
			System.arraycopy(this.values, 0, temp, 0, this.values.length);
			this.values = temp;
		}
		return this.values[index];
	}

	public long mult(int index, long value) {
		try {
			this.values[index] *= value;
		} catch (ArrayIndexOutOfBoundsException e) {
			long[] temp = new long[index + 1];
			System.arraycopy(this.values, 0, temp, 0, this.values.length);
			this.values = temp;
		}
		return this.values[index];
	}

	public long add(int index, long count) {
		try {
			this.values[index] += count;
		} catch (ArrayIndexOutOfBoundsException e) {
			long[] temp = new long[index + 1];
			System.arraycopy(this.values, 0, temp, 0, this.values.length);
			temp[index] = count;
			this.values = temp;
		}
		return this.values[index];
	}

	public long sub(int index, long count) {
		return this.add(index, -count);
	}

	public long incr(int index) {
		return this.add(index, 1);
	}

	public long decr(int index) {
		return this.sub(index, 1);
	}
}
