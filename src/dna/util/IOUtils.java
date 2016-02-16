package dna.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

	/**
	 * Returns an InputStream to the file at the given path inside the given
	 * jar.
	 **/
	public static InputStream getInputStreamFromJar(JarFile jar, String path)
			throws IOException {
		// check if entry at path exists, if yes return input stream
		JarEntry entry = jar.getJarEntry(path);
		if (entry != null)
			return jar.getInputStream(entry);

		// if not, build relative path with first directory missing
		String[] splits = path.split("/");
		String relativePath = "";

		if (splits.length == 1)
			relativePath = splits[0];
		else {
			// build relative path: skip first entry -> start with i = 1
			for (int i = 1; i < splits.length; i++) {
				relativePath += splits[i];
				if (i < splits.length - 1)
					relativePath += "/";
			}
		}

		entry = jar.getJarEntry(relativePath);
		if (entry != null)
			return jar.getInputStream(jar.getJarEntry(relativePath));
		else {
			Log.warn("no file inside .jar found at " + path + " or "
					+ relativePath);
			return null;
		}
	}

	/** Returns if the current program is run from jar or binary. **/
	public static boolean isRunFromJar() {
		try {
			Path pPath = Paths.get(Config.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			if (pPath.getFileName().toString().endsWith(".jar"))
				return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return false;
	}

	/** Instanciates a new JarFile object pointing at the current jar file. **/
	public static JarFile getJarFile() throws URISyntaxException, IOException {
		Path p = Paths.get(Config.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI());

		return new JarFile(p.toFile(), false);
	}

}
