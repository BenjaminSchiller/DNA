package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.distr.BinnedDistr;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.BinnedLongDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.Distr.DistrType;
import dna.series.data.distr.DoubleDistr;
import dna.series.data.distr.IntDistr;
import dna.series.data.distr.LongDistr;

public class DistributionList extends List<Distr<?>> {
	public DistributionList() {
		super();
	}

	public DistributionList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (Distr<?> d : this.getList()) {
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
			case DOUBLE:
				((DoubleDistr) d).write(dir, filename);
				break;
			case INT:
				((IntDistr) d).write(dir, filename);
				break;
			case LONG:
				((LongDistr) d).write(dir, filename);
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
			Class<? extends Distr<?>> c;
			switch (type) {
			case BINNED_DOUBLE:
				c = BinnedDoubleDistr.class;
				break;
			case BINNED_INT:
				c = BinnedIntDistr.class;
				break;
			case BINNED_LONG:
				c = BinnedLongDistr.class;
				break;
			case DOUBLE:
				c = DoubleDistr.class;
				break;
			case INT:
				c = IntDistr.class;
				break;
			case LONG:
				c = LongDistr.class;
				break;
			default:
				c = null;
				break;
			}

			if (type.equals(DistrType.DOUBLE) || type.equals(DistrType.INT)
					|| type.equals(DistrType.LONG)) {
				list.add(Distr.read(dir, dist,
						Files.getDistributionNameFromFilename(dist, type),
						readValues, c));
			} else {
				list.add(BinnedDistr.read(dir, dist,
						Files.getDistributionNameFromFilename(dist, type),
						readValues, c));
			}
		}

		return list;
	}
}
