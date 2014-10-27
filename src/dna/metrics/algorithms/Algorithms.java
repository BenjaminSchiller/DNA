package dna.metrics.algorithms;

import java.util.ArrayList;

import dna.metrics.IMetric;

public class Algorithms {
	public IMetric[] metrics;

	public IRecomputation[] recomputation;
	public IDynamicAlgorithm[] dynamicAlgorithm;

	public IBeforeBatch[] beforeBatch;

	public IBeforeNA[] beforeUpdateNA;
	public IBeforeNR[] beforeUpdateNR;
	public IBeforeNW[] beforeUpdateNW;
	public IBeforeEA[] beforeUpdateEA;
	public IBeforeER[] beforeUpdateER;
	public IBeforeEW[] beforeUpdateEW;

	public IAfterNA[] afterUpdateNA;
	public IAfterNR[] afterUpdateNR;
	public IAfterNW[] afterUpdateNW;
	public IAfterEA[] afterUpdateEA;
	public IAfterER[] afterUpdateER;
	public IAfterEW[] afterUpdateEW;

	public IAfterBatch[] afterBatch;

	public Algorithms(IMetric[] metrics) {
		this.metrics = metrics;

		this.recomputation = (IRecomputation[]) this.get(IRecomputation.class)
				.toArray(new IRecomputation[this.count(IRecomputation.class)]);
		this.dynamicAlgorithm = (IDynamicAlgorithm[]) this.get(
				IDynamicAlgorithm.class).toArray(
				new IDynamicAlgorithm[this.count(IDynamicAlgorithm.class)]);

		this.beforeBatch = (IBeforeBatch[]) this.get(IBeforeBatch.class)
				.toArray(new IBeforeBatch[this.count(IBeforeBatch.class)]);

		this.beforeUpdateNA = (IBeforeNA[]) this.get(IBeforeNA.class).toArray(
				new IBeforeNA[this.count(IBeforeNA.class)]);
		this.beforeUpdateNR = (IBeforeNR[]) this.get(IBeforeNR.class).toArray(
				new IBeforeNR[this.count(IBeforeNR.class)]);
		this.beforeUpdateNW = (IBeforeNW[]) this.get(IBeforeNW.class).toArray(
				new IBeforeNW[this.count(IBeforeNW.class)]);
		this.beforeUpdateEA = (IBeforeEA[]) this.get(IBeforeEA.class).toArray(
				new IBeforeEA[this.count(IBeforeEA.class)]);
		this.beforeUpdateER = (IBeforeER[]) this.get(IBeforeER.class).toArray(
				new IBeforeER[this.count(IBeforeER.class)]);
		this.beforeUpdateEW = (IBeforeEW[]) this.get(IBeforeEW.class).toArray(
				new IBeforeEW[this.count(IBeforeEW.class)]);

		this.afterUpdateNA = (IAfterNA[]) this.get(IAfterNA.class).toArray(
				new IAfterNA[this.count(IAfterNA.class)]);
		this.afterUpdateNR = (IAfterNR[]) this.get(IAfterNR.class).toArray(
				new IAfterNR[this.count(IAfterNR.class)]);
		this.afterUpdateNW = (IAfterNW[]) this.get(IAfterNW.class).toArray(
				new IAfterNW[this.count(IAfterNW.class)]);
		this.afterUpdateEA = (IAfterEA[]) this.get(IAfterEA.class).toArray(
				new IAfterEA[this.count(IAfterEA.class)]);
		this.afterUpdateER = (IAfterER[]) this.get(IAfterER.class).toArray(
				new IAfterER[this.count(IAfterER.class)]);
		this.afterUpdateEW = (IAfterEW[]) this.get(IAfterEW.class).toArray(
				new IAfterEW[this.count(IAfterEW.class)]);

		this.afterBatch = (IAfterBatch[]) this.get(IAfterBatch.class).toArray(
				new IAfterBatch[this.count(IAfterBatch.class)]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int count(Class test) {
		int counter = 0;
		for (IMetric m : this.metrics) {
			if (test.isAssignableFrom(m.getClass())) {
				counter++;
			}
		}
		return counter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<IMetric> get(Class test) {
		ArrayList<IMetric> list = new ArrayList<IMetric>();
		for (IMetric m : this.metrics) {
			if (test.isAssignableFrom(m.getClass())) {
				list.add(m);
			}
		}
		return list;
	}
}
