package dna.io.network.netflow.darpa;

import java.io.FileNotFoundException;

import dna.io.network.netflow.DefaultNetflowReader;

public class DarpaNetflowReader extends DefaultNetflowReader {

	public DarpaNetflowReader(String dir, String filename)
			throws FileNotFoundException {
		super(dir, filename, -21600);
	}

}