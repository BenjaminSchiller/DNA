package dna.graph.generators.konect;

public class KonectEdge implements Comparable<KonectEdge> {

	public static final String separator = "\\s+";

	public int n1;

	public int n2;

	public double weight;

	public long timestamp;

	public KonectEdge(int from, int to, int weight, long timestamp) {
		this.n1 = from;
		this.n2 = to;
		this.weight = weight;
		this.timestamp = timestamp;
	}

	public KonectEdge(String line) {
		this(line, 0, 1);
	}

	public KonectEdge(String line, double offset, double factor) {
		String[] temp = line.split(separator);
		this.n1 = Integer.parseInt(temp[0]);
		this.n2 = Integer.parseInt(temp[1]);
		this.weight = (Double.parseDouble(temp[2]) + offset) * factor;
		this.timestamp = Long.parseLong(temp[3]);
	}

	@Override
	public int compareTo(KonectEdge o) {
		long temp = this.timestamp - o.timestamp;
		if (temp == 0) {
			return 0;
		} else if (temp < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	public String toString() {
		return this.timestamp + ": " + this.n1 + " -> " + this.n2 + " @ "
				+ this.weight;
	}
}
