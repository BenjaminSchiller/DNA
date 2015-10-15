package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.BinnedLongDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.QualityDoubleDistr;
import dna.series.data.distr.QualityIntDistr;
import dna.series.data.distr.QualityLongDistr;
import dna.series.data.distr.Distr.DistrType;

public class DistributionList extends List<Distr<?, ?>> {
	public DistributionList() {
		super();
	}

	public DistributionList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (Distr<?, ?> d : this.getList()) {
			DistrType type = d.getDistrType();
			String filename = Files.getDistributionFilename(d.getName(), type);
			switch (type) {
			case BINNED_DOUBLE:
				((BinnedDoubleDistr) d).write(dir, filename);
				break;
			case BINNED_INT:
				((BinnedIntDistr) d).write(dir, filename);
				break;
			case BINNED_LONG:
				((BinnedLongDistr) d).write(dir, filename);
				break;
			case QUALITY_DOUBLE:
				((QualityDoubleDistr) d).write(dir, filename);
				break;
			case QUALITY_INT:
				((QualityIntDistr) d).write(dir, filename);
				break;
			case QUALITY_LONG:
				((QualityLongDistr) d).write(dir, filename);
				break;
			default:
				d.write(dir, filename);
				break;
			}
		}
	}

	public static DistributionList read(String dir, boolean readValues)
			throws IOException {
		String[] distributions = Files.getDistributions(dir);
		if (distributions == null)
			return new DistributionList(0);

		DistributionList list = new DistributionList(distributions.length);

		for (String dist : distributions)
			list.add(Distr.read(dir, dist, readValues));

		return list;
	}

}
