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

	public static final GraphPanelConfig defaultGraphPanelConfig = GraphPanelConfig
			.getDefaultConfig();

	protected RulesConfig rules;

	protected int width;
	protected int height;
	protected boolean fullscreen;

	protected boolean statPanelEnabled;
	protected boolean textPanelEnabled;

	protected String timestampFormat;
	protected boolean renderHQ;
	protected boolean renderAA;

	protected double zoomSpeed;
	protected double scrollSpeed;
	protected boolean toolTipsEnabled;

	protected boolean directedEdgeArrowsEnabled;

	protected double nodeSize;
	protected String nodeColor;
	protected double edgeSize;

	protected ProjectionConfig projectionConfig;

	protected CaptureConfig captureConfig;

	protected GraphLayouter layouter;
	protected double autoLayoutForce;

	public enum GraphLayouter {
		none, auto, linlog
	}

	public GraphPanelConfig(int width, int height, boolean fullscreen,
			boolean statPanelEnabled, boolean textPanelEnabled,
			String timestampFormat, boolean renderHQ, boolean renderAA,
			double zoomSpeed, double scrollSpeed, boolean toolTipsEnabled,
			boolean directedEdgeArrowsEnabled, double nodeSize,
			String nodeColor, double edgeSize, GraphLayouter layouter,
			double autoLayoutForce, ProjectionConfig projectionConfig,
			CaptureConfig captureConfig, RulesConfig rules) {
		this.width = width;
		this.height = height;
		this.fullscreen = fullscreen;
		this.statPanelEnabled = statPanelEnabled;
		this.textPanelEnabled = textPanelEnabled;
		this.timestampFormat = timestampFormat;
		this.renderHQ = renderHQ;
		this.renderAA = renderAA;
		this.zoomSpeed = zoomSpeed;
		this.scrollSpeed = scrollSpeed;
		this.toolTipsEnabled = toolTipsEnabled;
		this.directedEdgeArrowsEnabled = directedEdgeArrowsEnabled;
		this.nodeSize = nodeSize;
		this.nodeColor = nodeColor;
		this.edgeSize = edgeSize;
		this.layouter = layouter;
		this.autoLayoutForce = autoLayoutForce;
		this.projectionConfig = projectionConfig;
		this.captureConfig = captureConfig;
		this.rules = rules;
	}

	public void read(String dir, String filename) {
		// TODO!
	}

	public void write(String dir, String filename) {
		// TODO!
	}

	/** Creates a main display config object from a given json object. **/
	public static GraphPanelConfig getFromJSONObject(JSONObject o) {
		GraphPanelConfig def = GraphPanelConfig.defaultGraphPanelConfig;

		// init values
		RulesConfig rules = null;
		CaptureConfig captureConfig = null;
		ProjectionConfig projectionConfig = null;
		int width = 1024;
		int height = 768;
		boolean fullscreen = false;
		boolean statPanelEnabled = true;
		boolean textPanelEnabled = true;
		String timestampFormat = null;
		boolean renderHQ = false;
		boolean renderAA = false;
		double zoomSpeed = 0.03;
		double scrollSpeed = 0.02;
		boolean toolTipsEnabled = true;
		boolean directedEdgeArrowsEnabled = true;

		double nodeSize = 10.0;
		String nodeColor = null;
		double edgeSize = 0.2;

		GraphLayouter layouter = GraphLayouter.auto;
		double autoLayoutForce = 1.0;

		if (def != null) {
			// read default values
			rules = def.getRules();
			captureConfig = def.getCaptureConfig();
			projectionConfig = def.getProjectionConfig();
			width = def.getWidth();
			height = def.getHeight();
			fullscreen = def.isFullscreen();
			statPanelEnabled = def.isStatPanelEnabled();
			textPanelEnabled = def.isTextPanelEnabled();
			timestampFormat = def.getTimestampFormat();
			renderHQ = def.isRenderHQ();
			renderAA = def.isRenderAA();
			zoomSpeed = def.getZoomSpeed();
			scrollSpeed = def.getScrollSpeed();
			toolTipsEnabled = def.isToolTipsEnabled();
			directedEdgeArrowsEnabled = def.isDirectedEdgeArrowsEnabled();
			nodeSize = def.getNodeSize();
			nodeColor = def.getNodeColor();
			edgeSize = def.getEdgeSize();
			layouter = def.getLayouter();
			autoLayoutForce = def.getAutoLayoutForce();
		}

		if (JSONObject.getNames(o) != null) {
			for (String s : JSONObject.getNames(o)) {
				switch (s) {
				case "3dProjectionConfig":
					projectionConfig = ProjectionConfig.getFromJSONObject(o
							.getJSONObject(s));
					break;
				case "AutoLayoutForce":
					autoLayoutForce = o.getDouble(s);
					break;
				case "CaptureConfig":
					captureConfig = CaptureConfig.getFromJSONObject(o
							.getJSONObject(s));
					break;
				case "DirectedEdgeArrowsEnabled":
					directedEdgeArrowsEnabled = o.getBoolean(s);
					break;
				case "EdgeSize":
					edgeSize = o.getDouble(s);
					break;
				case "Fullscreen":
					fullscreen = o.getBoolean(s);
					break;
				case "Height":
					height = o.getInt(s);
					break;
				case "Layouter":
					layouter = GraphLayouter.valueOf(o.getString(s));
					break;
				case "NodeColor":
					nodeColor = o.getString(s);
					break;
				case "NodeSize":
					nodeSize = o.getDouble(s);
					break;
				case "RenderAA":
					renderAA = o.getBoolean(s);
					break;
				case "RenderHQ":
					renderHQ = o.getBoolean(s);
					break;
				case "RulesConfig":
					rules = RulesConfig.getFromJSONObject(o
							.getJSONObject("RulesConfig"));
					break;
				case "ScrollSpeed":
					scrollSpeed = o.getDouble(s);
					break;
				case "StatPanelEnabled":
					statPanelEnabled = o.getBoolean(s);
					break;
				case "TextPanelEnabled":
					textPanelEnabled = o.getBoolean(s);
					break;
				case "TimestampFormat":
					timestampFormat = o.getString(s);
					break;
				case "ToolTipsEnabled":
					toolTipsEnabled = o.getBoolean(s);
					break;
				case "Width":
					width = o.getInt(s);
					break;
				case "ZoomSpeed":
					zoomSpeed = o.getDouble(s);
					break;
				}
			}
		}

		return new GraphPanelConfig(width, height, fullscreen,
				statPanelEnabled, textPanelEnabled, timestampFormat, renderHQ,
				renderAA, zoomSpeed, scrollSpeed, toolTipsEnabled,
				directedEdgeArrowsEnabled, nodeSize, nodeColor, edgeSize,
				layouter, autoLayoutForce, projectionConfig, captureConfig,
				rules);
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

	public static GraphPanelConfig getDefaultgraphpanelconfig() {
		return defaultGraphPanelConfig;
	}

	public double getNodeSize() {
		return nodeSize;
	}

	public String getNodeColor() {
		return nodeColor;
	}

	public double getEdgeSize() {
		return edgeSize;
	}

	public RulesConfig getRules() {
		return rules;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public boolean isStatPanelEnabled() {
		return statPanelEnabled;
	}

	public boolean isTextPanelEnabled() {
		return textPanelEnabled;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

	public boolean isRenderHQ() {
		return renderHQ;
	}

	public boolean isRenderAA() {
		return renderAA;
	}

	public double getZoomSpeed() {
		return zoomSpeed;
	}

	public double getScrollSpeed() {
		return scrollSpeed;
	}

	public boolean isToolTipsEnabled() {
		return toolTipsEnabled;
	}

	public boolean isDirectedEdgeArrowsEnabled() {
		return directedEdgeArrowsEnabled;
	}

	public CaptureConfig getCaptureConfig() {
		return captureConfig;
	}

	public GraphLayouter getLayouter() {
		return layouter;
	}

	public double getAutoLayoutForce() {
		return autoLayoutForce;
	}

	public ProjectionConfig getProjectionConfig() {
		return projectionConfig;
	}

	public void setRules(RulesConfig rules) {
		this.rules = rules;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public void setStatPanelEnabled(boolean statPanelEnabled) {
		this.statPanelEnabled = statPanelEnabled;
	}

	public void setTextPanelEnabled(boolean textPanelEnabled) {
		this.textPanelEnabled = textPanelEnabled;
	}

	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	public void setRenderHQ(boolean renderHQ) {
		this.renderHQ = renderHQ;
	}

	public void setRenderAA(boolean renderAA) {
		this.renderAA = renderAA;
	}

	public void setZoomSpeed(double zoomSpeed) {
		this.zoomSpeed = zoomSpeed;
	}

	public void setScrollSpeed(double scrollSpeed) {
		this.scrollSpeed = scrollSpeed;
	}

	public void setToolTipsEnabled(boolean toolTipsEnabled) {
		this.toolTipsEnabled = toolTipsEnabled;
	}

	public void setDirectedEdgeArrowsEnabled(boolean directedEdgeArrowsEnabled) {
		this.directedEdgeArrowsEnabled = directedEdgeArrowsEnabled;
	}

	public void setNodeSize(double nodeSize) {
		this.nodeSize = nodeSize;
	}

	public void setNodeColor(String nodeColor) {
		this.nodeColor = nodeColor;
	}

	public void setEdgeSize(double edgeSize) {
		this.edgeSize = edgeSize;
	}

	public void setProjectionConfig(ProjectionConfig projectionConfig) {
		this.projectionConfig = projectionConfig;
	}

	public void setCaptureConfig(CaptureConfig captureConfig) {
		this.captureConfig = captureConfig;
	}

	public void setLayouter(GraphLayouter layouter) {
		this.layouter = layouter;
	}

	public void setAutoLayoutForce(double autoLayoutForce) {
		this.autoLayoutForce = autoLayoutForce;
	}
}
