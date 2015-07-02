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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import dna.util.Log;

public class GraphPanel extends JPanel {

	// statics
	protected final static Font font = new Font("Verdana", Font.PLAIN, 14);

	// panels
	protected final JPanel textPanel;
	protected final JLabel textLabel;
	protected final Layout layouter;
	protected final String name;

	protected static final String screenshotsKey = "ui.screenshot";
	protected static final String screenshotsDir = "images/";
	protected static final String screenshotsSuffix = ".png";

	public static double layouterForce = 1.0;
	public static boolean useLinLogLayout = false;
	public static boolean useLayouter3dMode = false;

	public GraphPanel(final Graph graph, final String name) {
		System.out.println("creating graphpanel! name: '" + name + "'");
		this.name = name;

		// init textpanel
		this.textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textPanel.setBackground(new Color(230, 230, 230));

		// set text panel
		textLabel = new JLabel();
		textLabel.setFont(font);
		textLabel.setText("Initialization");
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
				graph.addAttribute(screenshotsKey, f2.getAbsolutePath());
				Log.info("GraphVis - saving screenshot to '" + f2.getPath()
						+ "'");
			}
		});

		// create viewer and show graph
		Viewer v = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		Layout layouter = new SpringBox(useLayouter3dMode);
		if (useLinLogLayout)
			layouter = new LinLog(useLayouter3dMode);

		layouter.setForce(layouterForce);
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
}
