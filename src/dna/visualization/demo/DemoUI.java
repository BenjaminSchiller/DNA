package dna.visualization.demo;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import dna.graph.generators.GraphGenerator;
import dna.graph.generators.util.ReadableFileGraph;
import dna.visualization.demo.Demo.BG;
import dna.visualization.demo.Demo.CFG;
import dna.visualization.demo.Demo.GDS;
import dna.visualization.demo.Demo.GG;
import dna.visualization.demo.Demo.METRIC;

public class DemoUI extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JComboBox gg = new JComboBox();
	private JComboBox gds = new JComboBox();
	private JComboBox bg = new JComboBox();
	private JComboBox batches = new JComboBox();
	private JComboBox runs = new JComboBox();
	private JComboBox time = new JComboBox();
	private JComboBox cfg = new JComboBox();

	private JCheckBox[] metrics;

	private JButton generateButton = new JButton("Generate");
	private JButton visualizeButton = new JButton("Visualize");
	private JButton plotButton = new JButton("Plot");
	private JButton texButton = new JButton("TeX");
	private JButton liveButton = new JButton("Live");

	private int lines = 0;

	private Container cp = getContentPane();

	private File file = null;
	private File dir = null;

	public static DemoUI currentUI;

	public DemoUI() {
		this.fillLists();
		this.addListeners();

		this.addLine("Graph Generator", this.gg);
		this.addLine("GDS", this.gds);
		this.addLine("Batch Generator", this.bg);
		this.addLine("Batches", this.batches);
		this.addLine("Runs", this.runs);

		for (int i = 0; i < this.metrics.length; i += 2) {
			Component c1, c2 = null;
			c1 = this.metrics[i];
			if (i < this.metrics.length) {
				c2 = this.metrics[i + 1];
			}
			this.addLineX(c1, c2);
		}

		this.addLine(generateButton);
		this.addLine(plotButton);
		this.addLine(texButton);

		this.addLineX(this.cfg, visualizeButton);
		this.addLineX(this.time, liveButton);

		cp.setLayout(new GridLayout(this.lines, 2));

		DemoUI.currentUI = this;
	}

	private void addLine() {
		this.addLine(null, null);
	}

	private void addLine(Component comp) {
		this.addLineX(null, comp);
	}

	private void addLine(String name, Component comp) {
		Component c1 = null, c2 = null;
		if (name != null) {
			c1 = new JLabel("  = " + name);
		}
		if (comp != null) {
			c2 = comp;
		}
		this.addLineX(c2, c1);
	}

	private void addLineX(Component c1, Component c2) {
		if (c1 == null) {
			this.cp.add(new JLabel(""));
		} else {
			this.cp.add(c1);
		}
		if (c2 == null) {
			this.cp.add(new JLabel(""));
		} else {
			this.cp.add(c2);
		}
		this.lines++;
	}

	private void addListeners() {
		this.generateButton.addActionListener(this);
		this.visualizeButton.addActionListener(this);
		this.plotButton.addActionListener(this);
		this.texButton.addActionListener(this);
		this.liveButton.addActionListener(this);

		this.gg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JComboBox comboBox = (JComboBox) event.getSource();
				Object selected = comboBox.getSelectedItem();
				if (selected.toString().equals(GG.FILE.toString())) {
					System.out.println("FILE FILE FILE FILE!!!!!");
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new java.io.File(
							"/Users/benni/Downloads/"));
					fc.setDialogTitle("select file for graph (*.graph)");
					int returnVal = fc.showOpenDialog(comboBox);
					DemoUI.currentUI.file = fc.getSelectedFile();
					System.out.println("to string: " + fc.getSelectedFile());
					System.out.println("parentFile absolute path: "
							+ fc.getSelectedFile().getParentFile()
									.getAbsolutePath());
					System.out.println("parent: "
							+ fc.getSelectedFile().getParent());
					System.out.println("absolutePath: "
							+ fc.getSelectedFile().getAbsolutePath());
					System.out.println("name: "
							+ fc.getSelectedFile().getName());
				}
			}
		});

		this.bg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JComboBox comboBox = (JComboBox) event.getSource();
				Object selected = comboBox.getSelectedItem();
				if (selected.toString().equals(BG.DIR.toString())) {
					System.out.println("DIR DIR DIR DIR!!!!!");
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new java.io.File(
							"/Users/benni/Downloads/"));
					fc.setDialogTitle("select dir for batches (*.batch)");
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = fc.showOpenDialog(comboBox);
					DemoUI.currentUI.dir = fc.getSelectedFile();
					System.out.println("selected dir: " + fc.getSelectedFile());
				}
			}
		});
	}

	private void fillLists() {
		for (GG gg : GG.values()) {
			this.gg.addItem(gg);
		}
		for (GDS gds : GDS.values()) {
			this.gds.addItem(gds);
		}
		for (BG bg : BG.values()) {
			this.bg.addItem(bg);
		}
		for (int batches : Demo.B) {
			this.batches.addItem(batches);
		}
		for (int runs : Demo.R) {
			this.runs.addItem(runs);
		}
		for (int time : Demo.T) {
			this.time.addItem(time);
		}
		for (CFG cfg : CFG.values()) {
			this.cfg.addItem(cfg);
		}
		this.metrics = new JCheckBox[Demo.METRIC.values().length];
		int i = 0;
		for (METRIC m : METRIC.values()) {
			this.metrics[i++] = new JCheckBox(m.toString());
		}
	}

	public static void main(String[] args) {

		if (false) {
			try {
				GraphGenerator gg = new ReadableFileGraph(
						"/Users/benni/Downloads/bb_1CEX_C109S_7_BATCH/",
						"0.graph", dna.graph.datastructures.GDS.undirected());
				gg.generate();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		int width = 600;
		int height = 550;

		DemoUI mainFrame = new DemoUI();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainFrame);
		frame.setSize(width, height);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GG gg = GG.valueOf(this.gg.getSelectedItem().toString());
		GDS gds = GDS.valueOf(this.gds.getSelectedItem().toString());
		BG bg = BG.valueOf(this.bg.getSelectedItem().toString());
		int batches = Integer.parseInt(this.batches.getSelectedItem()
				.toString());
		int runs = Integer.parseInt(this.runs.getSelectedItem().toString());
		int time = Integer.parseInt(this.time.getSelectedItem().toString());
		ArrayList<METRIC> metrics = new ArrayList<METRIC>();
		for (JCheckBox m : this.metrics) {
			if (m.isSelected()) {
				metrics.add(METRIC.valueOf(m.getText()));
			}
		}
		Demo demo = new Demo(gg, gds, bg, metrics, batches, runs, time);
		demo.file = this.file;
		demo.dir = this.dir;
		demo.cfg = CFG.valueOf(this.cfg.getSelectedItem().toString());

		if (e.getSource() == this.generateButton) {
			DemoExecution.generate(demo);
		} else if (e.getSource() == this.visualizeButton) {
			DemoExecution.visualize(demo);
		} else if (e.getSource() == this.plotButton) {
			DemoExecution.plot(demo);
		} else if (e.getSource() == this.texButton) {
			DemoExecution.tex(demo);
		} else if (e.getSource() == this.liveButton) {
			DemoExecution.live(demo);
		}
	}
}
