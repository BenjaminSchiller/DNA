package dna.profiler.benchmarking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import org.perfidix.element.BenchmarkMethod;
import org.perfidix.exceptions.AbstractPerfidixMethodException;
import org.perfidix.meter.AbstractMeter;
import org.perfidix.ouput.AbstractOutput;
import org.perfidix.result.BenchmarkResult;
import org.perfidix.result.ClassResult;
import org.perfidix.result.MethodResult;

import com.google.common.math.DoubleMath;

import dna.graph.datastructures.IDataStructure;
import dna.io.Writer;
import dna.profiler.ProfilerMeasurementData;
import dna.profiler.datatypes.benchmarkresults.BenchmarkingResult;

public class BenchmarkingVisitor extends AbstractOutput {
	private HashMap<String, Writer> fileWriters = new HashMap<String, Writer>();
	private HashMap<String, StringBuilder> fileWritersBufferData = new HashMap<String, StringBuilder>();
	private HashMap<String, String> lastWrittenOp = new HashMap<String, String>();
	private HashMap<String, BenchmarkingResult> collectedMeasurementData = new HashMap<String, BenchmarkingResult>();

	public static final String outputDir = "benchmarkResults";
	public static final String plotExtension = ".gnuplot";
	public static final String rawExtension = ".rawdata";
	public static final String aggregationFile = "aggregation" + rawExtension;

	private BenchmarkingConf conf;

	public BenchmarkingVisitor(BenchmarkingConf benchmarkingConf) {
		this.conf = benchmarkingConf;
	}

	@Override
	public void visitBenchmark(BenchmarkResult res) {
		ArrayList<String> resultList = new ArrayList<String>();
		for (final AbstractMeter meter : res.getRegisteredMeters()) {
			for (final ClassResult classRes : res.getIncludedResults()) {
				ArrayList<MethodResult> methResults = new ArrayList<MethodResult>(
						classRes.getIncludedResults());

				Collections.sort(methResults,
						new BenchmarkingVisitor.MethResultComparator());

				for (final MethodResult methRes : methResults) {
					Object[] paramSet = methRes.getInputParamSet();
					Class<? extends IDataStructure> clazz = (Class<? extends IDataStructure>) paramSet[0];
					int inputSize = (int) paramSet[1];

					double perElement = methRes.mean(meter)
							/ conf.getOperationSize();

					String methodName = ((BenchmarkMethod) methRes
							.getRelatedElement()).getMethodToBench().getName();
					String resString = meter.getName() + "_"
							+ clazz.getSimpleName() + "_" + methodName
							+ ", size " + String.format("%5d", inputSize)
							+ ": " + methRes.mean(meter) + ", per Element: "
							+ perElement;
					resultList.add(resString);
					try {
						Collection<Double> results = methRes
								.getResultSet(meter);
						double maxElement = Collections.max(results);
						Collection<Double> resultsNormalized = new ArrayList<Double>(
								results.size() - 1);
						for (Double singleRes : results) {
							if (singleRes == maxElement) {
								maxElement = -1;
								continue;
							}
							resultsNormalized.add(singleRes
									/ conf.getOperationSize());
						}

						String keyForEntry = "";
						switch (meter.getClass().getSimpleName()) {
						case "MemMeter":
							keyForEntry = "MEMORYBENCHMARK";
							break;
						case "TimeMeter":
							keyForEntry = "RUNTIMEBENCHMARK";
							break;
						default:
							throw new RuntimeException("Got unknown meter "
									+ meter.getClass().getSimpleName());
						}
						keyForEntry += "_" + clazz.getSimpleName() + "_"
								+ methodName;
						keyForEntry = keyForEntry.toUpperCase();

						BenchmarkingResult entry = this
								.getResultEntry(keyForEntry);
						resultsNormalized = entry.addToMap(inputSize,
								resultsNormalized);
						collectedMeasurementData.put(keyForEntry, entry);

						writeResultForGnuplot(meter, clazz.getSimpleName(),
								methodName, inputSize, resultsNormalized);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		Collections.sort(resultList);
		try {
			writeEntriesToProfilerFiles();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		for (String s : resultList) {
			System.out.println(s);
		}

		for (Entry<String, StringBuilder> e : fileWritersBufferData.entrySet()) {
			// Get the proper fileWriter
			Writer w = fileWriters.get(e.getKey());
			try {
				w.writeln(e.getValue().toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		for (Entry<String, Writer> w : fileWriters.entrySet()) {
			try {
				w.getValue().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void writeEntriesToProfilerFiles() throws IOException {
		String dirName = ProfilerMeasurementData.folderName + "benchmarks/";

		for (Entry<String, BenchmarkingResult> e : collectedMeasurementData
				.entrySet()) {
			String key = e.getKey();
			String[] parts = key.split("_");
			String fileName = parts[1] + ".properties";
			Writer w = fileWriters.get(dirName + fileName);
			if (w == null) {
				w = new Writer(dirName, fileName);
				fileWriters.put(dirName + fileName, w);
			}
			w.writeln(e.getValue().toString());
		}
	}

	private BenchmarkingResult getResultEntry(String key) {
		BenchmarkingResult entry;
		entry = collectedMeasurementData.get(key);
		if (entry == null) {
			entry = (BenchmarkingResult) ProfilerMeasurementData.get(key);
		}
		if (entry == null) {
			entry = new BenchmarkingResult(key);
		}
		return entry;
	}

	public static void writeGnuplotHeaderCommon(Writer w, String dirName,
			String fileName, String meterUnit) throws IOException {
		w.writeln("set terminal png large");
		w.writeln("set output \"" + dirName + fileName + ".png\"");
		w.writeln("set xrange [0:]");
		w.writeln("set xlabel \"Initial list size\"");
		w.writeln("set ylabel \"Benchmark result [" + meterUnit + "]\"");
	}

	public static void writeGnuplotHeaderMultiple(Writer w, String dirName,
			String fileName, String meterName, String meterUnit)
			throws IOException {
		writeGnuplotHeaderCommon(w, dirName, fileName, meterUnit);
		w.writeln("set title \"Benchmarking " + meterName + "\"");
		w.writeln("set key below");
	}

	private void writeGnuplotHeaderSingle(Writer w, String dirName,
			String fileName, String operation, AbstractMeter meter, String ds)
			throws IOException {
		writeGnuplotHeaderCommon(w, dirName, fileName, meter.getUnit());
		w.writeln("set title \"Benchmarking " + meter.getName() + " on " + ds
				+ " " + operation + "\"");
		w.writeln("plot '"
				+ dirName
				+ fileName
				+ rawExtension
				+ "' using 1:2 notitle with lp linetype 1, \"\" using 1:2:3:4 title '"
				+ ds + "' with errorbars linetype 1");
	}

	private void writeResultForGnuplot(AbstractMeter meter, String ds,
			String operation, int size, Collection<Double> values)
			throws IOException {
		String dirName = outputDir + "/" + meter.getName() + "/";

		// Write data for *single* plot
		String fileName = ds + "_" + operation;
		String extension = plotExtension;
		Writer w = fileWriters.get(dirName + fileName + extension);
		if (w == null) {
			w = new Writer(dirName, fileName + extension);
			writeGnuplotHeaderSingle(w, dirName, fileName, operation, meter, ds);
			fileWriters.put(dirName + fileName + extension, w);
		}

		extension = rawExtension;
		w = fileWriters.get(dirName + fileName + extension);
		if (w == null) {
			w = new Writer(dirName, fileName + extension);
			fileWriters.put(dirName + fileName + extension, w);
		}

		double min = Collections.min(values);
		double max = Collections.max(values);
		double mean = DoubleMath.mean(values);

		w.writeln(" " + size + " " + mean + " " + min + " " + max);

		// Check for the aggregation file
		extension = plotExtension;
		String innerLastWrittenOp = lastWrittenOp.get(dirName + operation
				+ extension);

		if (innerLastWrittenOp == null || !innerLastWrittenOp.equals(operation)) {
			w = fileWriters.get(dirName + aggregationFile);
			if (w == null) {
				w = new Writer(dirName, aggregationFile, true);
				fileWriters.put(dirName + aggregationFile, w);
			}
			w.writeln(operation + ";" + meter.getUnit() + ";" + fileName
					+ rawExtension);
		}
		lastWrittenOp.put(dirName + operation + extension, operation);
	}

	@Override
	public boolean listenToResultSet(BenchmarkMethod meth, AbstractMeter meter,
			double data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean listenToException(AbstractPerfidixMethodException exec) {
		final StringBuilder builder = new StringBuilder();
		if (exec.getMethod() != null) {
			builder.append("Class: ")
					.append(exec.getMethod().getDeclaringClass()
							.getSimpleName()).append("#")
					.append(exec.getMethod().getName()).append("\n");
		}
		builder.append("Annotation: ").append(
				exec.getRelatedAnno().getSimpleName());
		builder.append("\nException: ").append(exec.getClass().getSimpleName())
				.append("/").append(exec.getExec().toString());
		System.out.println(builder.toString());
		exec.getExec().printStackTrace(System.out);
		return true;
	}

	public class MethResultComparator implements Comparator<MethodResult> {

		@Override
		public int compare(MethodResult o1, MethodResult o2) {
			Object[] pso1 = o1.getInputParamSet();
			Object[] pso2 = o2.getInputParamSet();
			if (pso1[0] != pso2[0])
				return ((Class) pso1[0]).getSimpleName().compareTo(
						((Class) pso2[0]).getSimpleName());
			if ((int) pso1[1] > (int) pso2[1])
				return +1;
			if ((int) pso1[1] < (int) pso2[1])
				return -1;
			return 0;
		}

	}

}
