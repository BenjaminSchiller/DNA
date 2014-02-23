package dna.profiler.benchmarking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

import org.perfidix.meter.AbstractMeter;

import dna.graph.tests.GlobalTestParameters;
import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;
import dna.util.Execute;

public class BenchmarkingActions {
	public static void aggregateInDirectory(String meterName)
			throws IOException {
		String line;
		String[] lineContent;
		ArrayList<String> listOfFiles;

		Hashtable<String, ArrayList<String>> filesToAggregate = new Hashtable<String, ArrayList<String>>();
		HashMap<String, String> unitsPerMeter = new HashMap<String, String>();

		String dirName = BenchmarkingVisitor.outputDir + "/" + meterName + "/";

		Reader fReader = new Reader(dirName,
				BenchmarkingVisitor.aggregationFile);
		while ((line = fReader.readString()) != null) {
			lineContent = line.split(";");
			unitsPerMeter.put(lineContent[0], lineContent[1]);

			listOfFiles = filesToAggregate
					.get(lineContent[0]);
			if (listOfFiles == null)
				listOfFiles = new ArrayList<String>();
			listOfFiles.add(lineContent[2]);
			filesToAggregate.put(lineContent[0], listOfFiles);
		}

		for (String operation : filesToAggregate.keySet()) {
			String unit = unitsPerMeter.get(operation);
			Writer w = new Writer(dirName, operation
					+ BenchmarkingVisitor.plotExtension);
			BenchmarkingVisitor.writeGnuplotHeaderMultiple(w, dirName,
					operation, meterName, unit);
			w.write("plot ");
			boolean appendComma = false;
			listOfFiles = filesToAggregate.get(operation);
			Collections.sort(listOfFiles);
			for (String singleRawDataSet : listOfFiles) {
				if (appendComma)
					w.write(", ");
				String ds = singleRawDataSet.replace("_" + operation, "")
						.replace(BenchmarkingVisitor.rawExtension, "");
				w.write("'" + dirName + singleRawDataSet
						+ "' using 1:2 title '" + ds + "' with lp");
				appendComma = true;
			}
			w.close();
		}
	}

	public static void plotAllPlots(File folder) throws IOException,
			InterruptedException {
		for (File inF : folder.listFiles()) {
			if (inF.isDirectory()) {
				plotAllPlots(inF);
			} else if (inF.getName()
					.endsWith(BenchmarkingVisitor.plotExtension)) {
				Execute.exec(Config.get("GNUPLOT_PATH") + " ." + File.separator
						+ inF, true);
			}
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Config.overwrite("GNUPLOT_PATH",
				"C:\\Program Files (x86)\\Cygwin\\bin\\gnuplot.exe");

		if (args[0].equals("getDS")) {
			BenchmarkingConf conf = new BenchmarkingConf();
			for ( AbstractMeter m: conf.getMeters() ) {
				File f = new File(BenchmarkingVisitor.outputDir + "/" + m.getName() + "/" + BenchmarkingVisitor.aggregationFile);
				f.delete();
			}
			
			for (Class c : GlobalTestParameters.dataStructures) {
				System.out.println(c.getName());
			}
		} else if (args[0].equals("plot")) {
			// Write aggregation files
			String folder = BenchmarkingVisitor.outputDir;
			File f = new File(folder);
			for (File inF : f.listFiles()) {
				if (inF.isDirectory()) {
					aggregateInDirectory(inF.getName());
				}
			}

			plotAllPlots(new File(folder));
		}
	}
}
