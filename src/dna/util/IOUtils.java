package dna.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class IOUtils {
	/**
	 * 
	 * @param dir
	 *            directory to read
	 * @param filter
	 *            optional FilenameFilter to filter the files in $dir
	 * @return list of all the files in $dir that match the optional $filter,
	 *         the list is sorted using Arrays.sort(...)
	 */
	public static String[] getFilenames(String dir, FilenameFilter filter) {
		File[] files;
		if (filter == null) {
			files = (new File(dir)).listFiles();
		} else {
			files = (new File(dir)).listFiles(filter);
		}
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filenames[i] = files[i].getName();
		}
		Arrays.sort(filenames);
		return filenames;
	}

}
