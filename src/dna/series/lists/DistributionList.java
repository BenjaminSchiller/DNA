package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.distr2.BinnedDistr;
import dna.series.data.distr2.BinnedDoubleDistr;
import dna.series.data.distr2.BinnedIntDistr;
import dna.series.data.distr2.BinnedLongDistr;
import dna.series.data.distr2.Distr;
import dna.series.data.distr2.Distr.DistrType;
import dna.series.data.distr2.QualityDistr;
import dna.series.data.distr2.QualityDoubleDistr;
import dna.series.data.distr2.QualityIntDistr;
import dna.series.data.distr2.QualityLongDistr;

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

		for (String dist : distributions) {
			DistrType type = Files.getDistributionTypeFromFilename(dist);
			String name = Files.getDistributionNameFromFilename(dist, type);
			Distr<?, ?> readDistribution;

			switch (type) {
			case BINNED_DOUBLE:
				readDistribution = BinnedDistr.read(dir, dist, name,
						readValues, BinnedDoubleDistr.class);
				break;
			case BINNED_INT:
				readDistribution = BinnedDistr.read(dir, dist, name,
						readValues, BinnedIntDistr.class);
				break;
			case BINNED_LONG:
				readDistribution = BinnedDistr.read(dir, dist, name,
						readValues, BinnedLongDistr.class);
				break;
			case QUALITY_DOUBLE:
				readDistribution = QualityDistr.read(dir, dist, name,
						readValues, QualityDoubleDistr.class);
				break;
			case QUALITY_INT:
				readDistribution = QualityDistr.read(dir, dist, name,
						readValues, QualityIntDistr.class);
				break;
			case QUALITY_LONG:
				readDistribution = QualityDistr.read(dir, dist, name,
						readValues, QualityLongDistr.class);
				break;
			default:
				readDistribution = null;
				break;
			}

			list.add(readDistribution);
		}

		return list;
	}
}
