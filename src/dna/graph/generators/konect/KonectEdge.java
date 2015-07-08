package dna.graph.generators.konect;

public class KonectEdge implements Comparable<KonectEdge> {

	public static final String separator = "\\s+";

	public int n1;

	public int n2;

	public int weight;

	public long timestamp;

	public KonectEdge(int from, int to, int weight, long timestamp) {
		this.n1 = from;
		this.n2 = to;
		this.weight = weight;
		this.timestamp = timestamp;
	}

	public KonectEdge(String line) {
		String[] temp = line.split(separator);
		this.n1 = Integer.parseInt(temp[0]);
		this.n2 = Integer.parseInt(temp[1]);
		this.weight = Integer.parseInt(temp[2]);
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
