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

public class BenchmarkingConf extends AbstractConfig {

    private final static int RUNS = 100;
    private final static Set<AbstractMeter> METERS = new HashSet<AbstractMeter>();
    private final static Set<AbstractOutput> OUTPUT = new HashSet<AbstractOutput>();

    private final static KindOfArrangement ARRAN = KindOfArrangement.SequentialMethodArrangement;
    private final static double GCPROB = 1.0d;

    static {
        METERS.add(new TimeMeter(Time.NanoSeconds));
        METERS.add(new MemMeter(Memory.Byte));

        // OUTPUT.add(new TabularSummaryOutput());
    }

    /**
     * Public constructor.
     */
    public BenchmarkingConf () {
        super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

    }

}