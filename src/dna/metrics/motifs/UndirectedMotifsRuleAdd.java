package dna.metrics.motifs;

import dna.metrics.motifs.UndirectedMotifs.UndirectedMotifType;
import dna.series.data.DistributionLong;

public class UndirectedMotifsRuleAdd implements UndirectedMotifsRule {

	public UndirectedMotifType m;
	public int i;

	public UndirectedMotifsRuleAdd(UndirectedMotifType m) {
		this.m = m;
		this.i = UndirectedMotifs.getIndex(this.m);
	}

	@Override
	public void execute(DistributionLong motifs, boolean add) {
		if (add) {
			motifs.incr(this.i);
		} else {
			motifs.decr(this.i);
		}
	}

	public String toString() {
		return "+(" + this.i + ")";
	}

}
