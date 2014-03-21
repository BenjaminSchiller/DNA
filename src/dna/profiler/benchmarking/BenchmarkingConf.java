package dna.profiler.benchmarking;

import java.util.HashSet;
import java.util.Set;

import org.perfidix.AbstractConfig;
import org.perfidix.element.KindOfArrangement;
import org.perfidix.meter.AbstractMeter;
import org.perfidix.meter.MemMeter;
import org.perfidix.meter.Memory;
import org.perfidix.meter.Time;
import org.perfidix.meter.TimeMeter;
import org.perfidix.ouput.AbstractOutput;

import dna.util.Config;

public class BenchmarkingConf extends AbstractConfig {

	private final static int RUNS = Config.getInt("BENCHMARKING_RUNS");
	private final static Set<AbstractMeter> METERS = new HashSet<AbstractMeter>();
	private final static Set<AbstractOutput> OUTPUT = new HashSet<AbstractOutput>();

	private final static KindOfArrangement ARRAN = KindOfArrangement.SequentialMethodArrangement;
	private final static double GCPROB = 1.0d;

	static {
		METERS.add(new TimeMeter(Time.NanoSeconds));
		METERS.add(new MemMeter(Memory.Byte));
	}

	private int[] inputSizes;
	private int operationSize;

	/**
	 * Public constructor.
	 */
	public BenchmarkingConf() {
		super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

		String inputSizesString = Config.get("BENCHMARKING_INPUTSIZES");
		String[] splitted = inputSizesString.split(";");

		this.inputSizes = new int[splitted.length];
		for (int i = 0; i < splitted.length; i++) {
			inputSizes[i] = Integer.parseInt(splitted[i]);
		}

		this.operationSize = 50;
	}

	public int[] getInputSizes() {
		return this.inputSizes;
	}

	public int getOperationSize() {
		return this.operationSize;
	}

}