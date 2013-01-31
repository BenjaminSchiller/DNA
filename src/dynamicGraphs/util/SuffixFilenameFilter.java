package dynamicGraphs.util;

import java.io.File;
import java.io.FilenameFilter;

public class SuffixFilenameFilter implements FilenameFilter {

	private String suffix;

	public SuffixFilenameFilter(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(this.suffix);
	}

}
