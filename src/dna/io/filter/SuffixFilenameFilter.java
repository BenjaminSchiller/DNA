package dna.io.filter;

import java.io.File;
import java.io.FilenameFilter;

public class SuffixFilenameFilter implements FilenameFilter {

	private String[] suffices;

	public SuffixFilenameFilter(String suffix) {
		this.suffices = new String[] { suffix };
	}

	public SuffixFilenameFilter(String[] suffices) {
		this.suffices = suffices;
	}

	@Override
	public boolean accept(File dir, String name) {
		for (String suffix : this.suffices) {
			if (name.endsWith(suffix))
				return true;
		}
		return false;
	}

}
