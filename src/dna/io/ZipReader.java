package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import dna.util.Config;
import dna.util.Log;

public class ZipReader extends Reader {

	private FileSystem zipFile;
	protected static FileSystem readFileSystem;

	public ZipReader(FileSystem fs, String dir, String filename)
			throws IOException, FileNotFoundException {
		// super(dir, filename);
		this.zipFile = fs;

		// if directory does not exist, create it
		Path innerDir = fs.getPath(dir);
		if (!Files.exists(innerDir))
			throw new FileNotFoundException(innerDir.toString());

		// if file does not exist, create it
		Path innerFile = fs.getPath(dir + filename);
		if (!Files.exists(innerFile))
			throw new FileNotFoundException("FileSystem: " + fs.toString()
					+ "  Innerdir:" + innerFile.toString());

		// create buffered reader
		this.reader = Files
				.newBufferedReader(innerFile, StandardCharsets.UTF_8);
	}

	public void closeFileSystem() throws IOException {
		this.zipFile.close();
	}

	public void closeAll() throws IOException {
		this.reader.close();
		this.zipFile.close();
	}

	/** Creates a zip filesystem for a batch in the specified directory. **/
	public static FileSystem getBatchFileSystem(String fsDir, String suffix,
			long timestamp) throws IOException {
		return getFileSystem(fsDir,
				dna.io.filesystem.Files.getBatchFilename(timestamp) + suffix);
	}

	/** Creates a zip filesystem for a run in the specified directory. **/
	public static FileSystem getRunFileSystem(String fsDir, int run)
			throws IOException {
		return getFileSystem(fsDir, dna.io.filesystem.Files.getRunFilename(run));
	}

	/**
	 * Creates an aggregation filesystem for an aggregation in the specified
	 * directory.
	 **/
	public static FileSystem getAggregationFileSystem(String fsDir)
			throws IOException {
		return getFileSystem(fsDir,
				Config.get("RUN_AGGREGATION") + Config.get("SUFFIX_ZIP_FILE"));
	}

	/** Creates a zip filesystem for a specified directory and filename. **/
	public static FileSystem getFileSystem(String fsDir, String filename)
			throws IOException {
		// chick if dir exists
		Path fileSystemDir = Paths.get(fsDir);
		if (!Files.exists(fileSystemDir))
			throw new FileNotFoundException(fileSystemDir.toString());

		// chick if file exists
		Path fsFile = Paths.get(fsDir + filename);
		if (!Files.exists(fsFile))
			throw new FileNotFoundException(fsFile.toString());

		// init filesystem settings
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		URI fsFileUri = URI.create("jar:" + fsFile.toUri().toString());

		// create filesystem
		return FileSystems.newFileSystem(fsFileUri, env);
	}

	/** Sets the ReadFileSystem. **/
	public static void setReadFilesystem(FileSystem fs) throws IOException {
		ZipReader.readFileSystem = fs;
	}

	/** Closes the current ReadFileSystem and sets it to null afterwards. **/
	public static void closeReadFilesystem() throws IOException {
		if (ZipReader.readFileSystem != null) {
			ZipReader.readFileSystem.close();
			ZipReader.readFileSystem = null;
		} else {
			Log.warn("attempting to close null readFileSystem");
		}
	}

	/** Returns if there is currently a read-filesystem. **/
	public static boolean isZipOpen() {
		if (ZipReader.readFileSystem != null)
			return true;
		else
			return false;
	}

	/** Converts the dir to a path inside the current read-filesystem. **/
	public static Path getPath(String dir) {
		return ZipReader.readFileSystem.getPath(dir);
	}

}
