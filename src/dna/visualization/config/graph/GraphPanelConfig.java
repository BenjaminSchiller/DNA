package dna.visualization.config.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

import dna.util.Config;
import dna.util.IOUtils;
import dna.util.Log;
import dna.visualization.config.JSON.JSONObject;
import dna.visualization.config.JSON.JSONTokener;
import dna.visualization.config.graph.rules.RulesConfig;

/**
 * Configuration object holding all values used to configure a GraphPanel.
 * 
 * @author Rwilmes
 * 
 */
public class GraphPanelConfig {

	protected RulesConfig rules;

	public GraphPanelConfig(RulesConfig rules) {
		this.rules = rules;
	}

	public RulesConfig getRules() {
		return rules;
	}

	public void read(String dir, String filename) {
		// TODO!
	}

	public void write(String dir, String filename) {
		// TODO!
	}

	/** Creates a main display config object from a given json object. **/
	public static GraphPanelConfig getFromJSONObject(JSONObject o) {
		System.out.println("graphPanelConfig: " + o);
		RulesConfig rules = null;

		for (String s : JSONObject.getNames(o)) {
			switch (s) {
			case "RulesConfig":
				rules = RulesConfig.getFromJSONObject(o
						.getJSONObject("RulesConfig"));
				break;
			}
		}

		return new GraphPanelConfig(rules);
	}

	public static GraphPanelConfig getDefaultConfig() {
		return readConfig(Config.get("GRAPH_VIS_CONFIG_DEFAULT_PATH"));
	}

	public static GraphPanelConfig readConfig(String path) {
		Log.info("Reading GraphPanel-config: '" + path + "'");
		GraphPanelConfig config = null;
		InputStream is = null;
		JarFile jar = null;

		File file = new File(path);

		if (file.exists()) {
			try {
				is = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			if (IOUtils.isRunFromJar()) {
				Log.info("'" + path
						+ "-> ' not found. Attempting to read from .jar");
				try {
					jar = IOUtils.getExecutionJarFile();
					is = IOUtils.getInputStreamFromJar(jar, path, true);
				} catch (URISyntaxException | IOException e) {
					e.printStackTrace();
				}
			} else {
				Log.info("\t-> '" + path + "' not found!");
			}
		}

		if (is != null) {
			JSONTokener tk = new JSONTokener(is);
			JSONObject jsonConfig = new JSONObject(tk);
			config = GraphPanelConfig.getFromJSONObject(jsonConfig
					.getJSONObject("GraphPanelConfig"));

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			is = null;
		}

		if (jar != null) {
			try {
				jar.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jar = null;
		}

		return config;
	}
}
