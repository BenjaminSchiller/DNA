package dna.visualization.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import dna.util.Config;
import dna.util.Log;

/**
 * The GraphPanel class is used as a JPanel which contains a text-panel and a
 * graph-visualization panel underneath.
 **/
public class GraphPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// font
	protected final static Font font = new Font("Verdana", Font.PLAIN, 14);

	// name & graph
	protected final String name;
	protected final Graph graph;

	// panels
	protected final JPanel textPanel;
	protected final JLabel textLabel;
	protected final Layout layouter;

	// constructor
	public GraphPanel(final Graph graph, final String name) {
		this.name = name;
		this.graph = graph;

		// init textpanel
		this.textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// set text panel
		textLabel = new JLabel();
		textLabel.setFont(font);
		textLabel.setText("Initialization");
		textLabel.setBackground(new Color(230, 230, 230));
		textPanel.add(textLabel);

		// dummy panel
		JPanel dummy = new JPanel();
		textPanel.add(dummy);

		// screenshot button
		JButton screenshot = new JButton("Screenshot");
		screenshot.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		screenshot
				.setToolTipText("Captures a screenshot and saves it to '/images/'");
		textPanel.add(screenshot);
		screenshot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String screenshotsDir = Config.get("GRAPH_VIS_SCREENSHOT_DIR");
				String screenshotsSuffix = Config
						.get("GRAPH_VIS_SCREENSHOT_SUFFIX");
				// create dir
				File f = new File(screenshotsDir);
				if (!f.exists() && !f.isFile())
					f.mkdirs();

				// get date format
				DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

				String filename = name + "-" + df.format(new Date());
				String path = screenshotsDir + filename;

				// get name
				File f2 = new File(path + screenshotsSuffix);
				int id = 0;
				while (f2.exists()) {
					id++;
					f2 = new File(path + "_" + id + screenshotsSuffix);
				}

				// create screenshot
				graph.addAttribute(GraphVisualization.screenshotsKey,
						f2.getAbsolutePath());
				Log.info("GraphVis - saving screenshot to '" + f2.getPath()
						+ "'");
			}
		});

		// create viewer and show graph
		Viewer v = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// create and configure layouter
		boolean useLayouter3dMode = Config.getBoolean("GRAPH_VIS_LAYOUT_3D");
		Layout layouter = new SpringBox(useLayouter3dMode);
		if (Config.getBoolean("GRAPH_VIS_LAYOUT_LINLOG"))
			layouter = new LinLog(useLayouter3dMode);

		layouter.setForce(Config.getDouble("GRAPH_VIS_LAYOUT_FORCE"));
		v.enableAutoLayout(layouter);
		this.layouter = layouter;

		// get view
		View view = v.addDefaultView(false);
		JPanel graphView = (JPanel) view;

		// main panel
		this.setLayout(new BorderLayout());
		this.add(textPanel, BorderLayout.PAGE_START);
		this.add(graphView, BorderLayout.CENTER);
	}

	/** Sets the text-label to the input text. **/
	public void setText(String text) {
		textLabel.setText(text);
	}

	/** Returns the embedded graphstream.graph. **/
	public Graph getGraph() {
		return this.graph;
	}

	/** Returns the layouter of the embedded graphstream graph. **/
	public Layout getLayouter() {
		return this.layouter;
	}
}
