package dna.io.filter;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;

public class LineCountFilter implements FilenameFilter {

	private int minLineCount;
	private int maxLineCount;

	private String prefix;
	private String suffix;

	public LineCountFilter(int minLineCount, int maxLineCount, String prefix,
			String suffix) {
		this.minLineCount = minLineCount;
		this.maxLineCount = maxLineCount;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public LineCountFilter(int minLineCount, int maxLineCount) {
		this(minLineCount, maxLineCount, null, null);
	}

	@Override
	public boolean accept(File dir, String name) {
		if (prefix != null && !name.startsWith(this.prefix)) {
			return false;
		}

		if (suffix != null && !name.endsWith(suffix)) {
			return false;
		}

		if (minLineCount == Integer.MIN_VALUE
				&& maxLineCount == Integer.MAX_VALUE) {
			return true;
		}

		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					new File(dir, name)));
			lnr.skip(Long.MAX_VALUE);
			int lineCount = lnr.getLineNumber() + 1;
			lnr.close();

			if (minLineCount != Integer.MIN_VALUE && lineCount < minLineCount) {
				return false;
			}
			if (maxLineCount != Integer.MAX_VALUE && lineCount > maxLineCount) {
				return false;
			}

			// System.out.println(dir + name + " => " + lineCount + " lines");
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
