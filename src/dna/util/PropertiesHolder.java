package dna.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public abstract class PropertiesHolder {
	public static Properties initWithFile(String file) throws IOException {
		return initWithFiles(new String[] { file });
	}

	public static Properties initWithFolder(String folder) throws IOException {
		return initWithFolders(new String[] { folder });
	}	
	
	public static Properties initFromFolder(String folderName) throws IOException {
		Vector<String> v = new Vector<String>();
		File folder = new File(folderName);
		File[] list = folder.listFiles();
		v.add(folder.getAbsolutePath());
		for (int j = 0; j < list.length; j++) {
			if (list[j].isDirectory() && !list[j].getName().startsWith(".")) {
				v.add(list[j].getAbsolutePath());
			}
		}
		return initWithFolders(ArrayUtils.toStringArray(v));		
	}	
	
	public static Properties initWithFolders(String[] folders) throws IOException {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < folders.length; i++) {
			File folder = new File(folders[i]);
			File[] list = folder.listFiles();
			for (int j = 0; j < list.length; j++) {
				if (list[j].isFile()
						&& list[j].getAbsolutePath().endsWith(".properties")) {
					v.add(list[j].getAbsolutePath());
				}
			}
		}
		return initWithFiles(ArrayUtils.toStringArray(v));
	}	
	
	public static Properties initWithFiles(String[] file) throws IOException {
		Properties res = new Properties();
		for (int i = 0; i < file.length; i++) {
			res.putAll(addFile(file[i]));
		}
		return res;
	}	
	
	public static Properties addFile(String file) throws IOException {
		Properties temp = new java.util.Properties();
		FileInputStream in = new FileInputStream(file);
		temp.load(in);
		return temp;
	}
	
}
