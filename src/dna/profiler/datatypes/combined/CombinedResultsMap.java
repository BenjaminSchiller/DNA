package dna.profiler.datatypes.combined;

import java.text.DecimalFormat;

import dna.profiler.datatypes.ComparableEntryMap;

public class CombinedResultsMap extends ComparableEntryMap {
	private double pos;

	public CombinedResultsMap(double pos) {
		this.pos = pos;
	}

	@Override
	public void add(ComparableEntryMap resSecond) {
	}

	private double getPos() {
		return pos;
	}
	
	public String toString() {
		DecimalFormat f = new DecimalFormat("###,##0.00");
		return f.format(this.getPos());
	}	

	@Override
	public int compareTo(ComparableEntryMap o) {
		double otherValue = ((CombinedResultsMap) o).getPos();
		if (Math.abs(otherValue - this.pos) < 0.01)
			return 0;
		return Double.compare(this.pos, otherValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(pos);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CombinedResultsMap other = (CombinedResultsMap) obj;
		if (Double.doubleToLongBits(pos) != Double.doubleToLongBits(other.pos)) {
			return false;
		}
		return true;
	}

	@Override
	public ComparableEntryMap clone() {
		return new CombinedResultsMap(pos);
	}

	@Override
	public void multiplyBy(double factor) {
	}

}