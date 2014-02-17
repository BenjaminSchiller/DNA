package dna.profiler;

public class ProfilerGranularity {
	public static final int disabled = 0x00000001;
	public static final int aggregationOnly = 0x00000002;

	public static final int eachMetric = 0x00000004;
	public static final int eachBatchGeneration = 0x0000008;
	public static final int eachUpdateType = 0x00000010;
	public static final int eachBatch = 0x00000020;
	public static final int eachRun = 0x00000040;
	public static final int eachSeries = 0x00000080;

	public static final int all = 0x00000400;

	private int granularitySetting;

	public ProfilerGranularity(int granularitySetting) {
		this.granularitySetting = granularitySetting;
	}

	private boolean writeFor(int innerKey) {
		if ((granularitySetting & all) != 0)
			return true;
		if ((granularitySetting & disabled) != 0)
			return false;
		return (granularitySetting & innerKey) != 0;
	}

	public boolean writeAfterMetric() {
		return writeFor(eachMetric);
	}

	public boolean writeAfterBatch() {
		return writeFor(eachBatch);
	}

	public boolean writeAfterRun() {
		return writeFor(eachRun);
	}

	public boolean writeAfterSeries() {
		return writeFor(eachSeries);
	}

	public boolean writeAfterUpdate() {
		return writeFor(eachUpdateType);
	}

	public boolean disabled() {
		return writeFor(disabled);
	}

	public boolean forceAll() {
		return writeFor(all);
	}

}
