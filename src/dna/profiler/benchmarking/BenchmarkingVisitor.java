package dna.profiler.benchmarking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import dna.graph.datastructures.IDataStructure;
import dna.io.Writer;
import dna.util.Config;
import dna.util.Execute;

public class BenchmarkingVisitor extends AbstractOutput {

	private HashMap<String, Writer> fileWriters = new HashMap<String, Writer>();
	private HashMap<String, StringBuilder> fileWritersBufferData = new HashMap<String, StringBuilder>();
	private HashMap<String, StringBuilder> fileWritersBufferHeader = new HashMap<String, StringBuilder>();
	private final String outputDir = "benchmarkResults";
	private HashMap<String, String> lastDS = new HashMap<String, String>();

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
					// System.out.println("  Method "
					// + ((BenchmarkMethod) methRes.getRelatedElement())
					// .getMethodToBench().getName()
					// + " on input " +
					// Arrays.asList(methRes.getInputParamSet()) +
					// " yielded\n   " + methRes.mean(meter));

					double perElement = methRes.mean(meter)
							/ BenchmarkingExperiments.operationSize;

					String methodName = ((BenchmarkMethod) methRes
							.getRelatedElement()).getMethodToBench().getName();
					String resString = meter.getName() + "_"
							+ clazz.getSimpleName() + "_" + methodName
							+ ", size " + String.format("%5d", inputSize)
							+ ": " + methRes.mean(meter) + ", per Element: "
							+ perElement;
					resultList.add(resString);
					try {
						writeResultForGnuplot(meter, clazz.getSimpleName(),
								methodName, inputSize, perElement);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		Collections.sort(resultList);

		for (String s : resultList) {
			System.out.println(s);
		}

		for (Entry<String, StringBuilder> e : fileWritersBufferData.entrySet()) {
			// Get the proper fileWriter
			Writer w = fileWriters.get(e.getKey());
			String header = fileWritersBufferHeader.get(e.getKey()).toString();
			try {
				w.writeln(header);
				w.writeln(e.getValue().toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		for (Entry<String, Writer> w : fileWriters.entrySet()) {
			try {
				String fileName = w.getKey();
				w.getValue().close();
				Execute.exec(Config.get("GNUPLOT_PATH") + " ." + File.separator
						+ fileName, true);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void writeGnuplotHeaderCommon(Writer w, String dirName,
			String fileName, AbstractMeter meter) throws IOException {
		w.writeln("set terminal png large");
		w.writeln("set output \"" + dirName + fileName + ".png\"");
		w.writeln("set xrange [0:]");
		w.writeln("set xlabel \"Initial list size\"");
		w.writeln("set ylabel \"Benchmark result [" + meter.getUnit() + "]\"");
	}

	private void writeGnuplotHeaderMultiple(Writer w, String dirName,
			String fileName, AbstractMeter meter) throws IOException {
		writeGnuplotHeaderCommon(w, dirName, fileName, meter);
		w.writeln("set title \"Benchmarking " + meter.getName() + "\"");
		w.writeln("set key below");
	}

	private void writeGnuplotHeaderSingle(Writer w, String dirName,
			String fileName, AbstractMeter meter, String ds) throws IOException {
		writeGnuplotHeaderCommon(w, dirName, fileName, meter);
		w.writeln("set title \"Benchmarking " + meter.getName() + " on " + ds
				+ "\"");
		w.writeln("plot '-' using 1:2 with lp");
	}

	private void writeResultForGnuplot(AbstractMeter meter, String ds,
			String operation, int size, double mean) throws IOException {
		String dirName = outputDir + "/" + meter.getName() + "/";

		// Write data for *single* plot
		String fileName = ds + "_" + operation;
		Writer w = fileWriters.get(dirName + fileName);
		if (w == null) {
			w = new Writer(dirName, fileName);
			writeGnuplotHeaderSingle(w, dirName, fileName, meter, ds);
			fileWriters.put(dirName + fileName, w);
		}
		w.writeln(" " + size + " " + mean);

		StringBuilder writeBufferData, writeBufferHeader;

		// Write common data
		fileName = operation;
		w = fileWriters.get(dirName + fileName);
		if (w == null) {
			w = new Writer(dirName, fileName);
			writeGnuplotHeaderMultiple(w, dirName, fileName, meter);
			fileWriters.put(dirName + fileName, w);

			writeBufferHeader = new StringBuilder();
			writeBufferHeader.append("plot ");

			fileWritersBufferData.put(dirName + fileName, new StringBuilder());
			fileWritersBufferHeader.put(dirName + fileName, writeBufferHeader);
		}

		String innerLastDS = lastDS.get(dirName + fileName);
		writeBufferData = fileWritersBufferData.get(dirName + fileName);
		writeBufferHeader = fileWritersBufferHeader.get(dirName + fileName);

		if (innerLastDS == null || !innerLastDS.equals(ds)) {
			if (innerLastDS != null) {
				writeBufferData.append("end\r\n");
				writeBufferHeader.append(", ");
			}
			writeBufferHeader.append("'-' using 1:2 title \"" + ds
					+ "\" with lp");
		}
		lastDS.put(dirName + fileName, ds);
		writeBufferData.append(" " + size + " " + mean + "\r\n");
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
