package dna.visualization.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;

import dna.series.data.BatchData;
import dna.series.lists.ValueList;

public class StatsDisplay extends Composite {

	public final static String dir = "data/test15/run.0/";

	// groups
	public final Group general;
	public final Group genRuntimes;
	public final Group metRuntimes;

	// labels
	public final Label directoryLabel;
	public final Label directoryValue;
	public final Label timestampLabel;
	public final Label timestampValue;
	public final Label nodesLabel;
	public final Label nodesValue;
	public final Label edgesLabel;
	public final Label edgesValue;

	// general runtimes
	public final Label batchGeneration;
	public final Label batchGenerationValue;
	public final Label total;
	public final Label totalValue;
	public final Label sum;
	public final Label sumValue;
	public final Label overhead;
	public final Label overheadValue;
	public final Label metrics;
	public final Label metricsValue;
	public final Label graphUpdate;
	public final Label graphUpdateValue;

	// metric runtimes
	public final Label closedTriCCUpdate;
	public final Label closedTriCCUpdateValue;
	public final Label closedTriCCRecomp;
	public final Label closedTriCCRecompValue;
	public final Label degreeDistRecomp;
	public final Label degreeDistRecompValue;
	public final Label degreeDistUpdate;
	public final Label degreeDistUpdateValue;
	public final Label openTriCCUpdate;
	public final Label openTriCCUpdateValue;
	public final Label openTriCCRecomp;
	public final Label openTriCCRecompValue;

	// progressbar
	public final ProgressBar progressBar;
	public final Label progressLabel;

	// speedbar
	public final Scale speed;
	public final Label speedLabel;

	/**
	 * Initializes the standard statistics module with it's default labels.
	 * 
	 * @param arg0
	 *            Parent composite
	 * @param arg1
	 *            SWT style
	 */
	public StatsDisplay(Composite arg0, int arg1) {
		super(arg0, arg1);

		this.general = new Group(this, SWT.NONE);
		// dir
		this.directoryLabel = new Label(general, SWT.LEFT);
		this.directoryLabel.setText("Dir:\t" + this.dir);
		this.directoryValue = new Label(general, SWT.RIGHT);

		// TIMESTAMP
		this.timestampLabel = new Label(general, SWT.RIGHT);
		this.timestampLabel.setText("Timestamp:");
		this.timestampValue = new Label(general, SWT.NONE);
		this.timestampValue.setAlignment(SWT.LEFT);
		this.timestampValue.setText("0\t");

		// Amount of Nodes
		this.nodesLabel = new Label(general, SWT.RIGHT);
		this.nodesLabel.setText("Nodes:");
		this.nodesValue = new Label(general, SWT.NONE);
		this.nodesValue.setAlignment(SWT.LEFT);
		this.nodesValue.setText("0\t");

		// Amount of Edges
		this.edgesLabel = new Label(general, SWT.RIGHT);
		this.edgesLabel.setText("Edges:");
		this.edgesValue = new Label(general, SWT.NONE);
		this.edgesValue.setAlignment(SWT.LEFT);
		this.edgesValue.setText("0\t");

		// General Runtimes Group
		this.genRuntimes = new Group(this, SWT.NONE);
		this.genRuntimes.setText("General Runtimes");

		// Metric Runtimes Group
		this.metRuntimes = new Group(this, SWT.NONE);
		this.metRuntimes.setText("Metric Runtimes");

		// progressbar
		this.progressBar = new ProgressBar(general, SWT.NONE);
		this.progressBar.setToolTipText("Playback progress");
		this.progressLabel = new Label(general, SWT.NONE);
		this.progressLabel.setAlignment(SWT.LEFT);
		this.progressLabel.setText("00.00 %  ");

		// speedscale
		this.speed = new Scale(general, SWT.NONE);
		this.speed.setMinimum(0);
		this.speed.setMaximum(190);
		this.speed.setPageIncrement(10);
		this.speed.setSelection(100);
		this.speed.setToolTipText("Playback speed");

		this.speedLabel = new Label(general, SWT.NONE);
		this.speedLabel.setAlignment(SWT.LEFT);
		this.speedLabel.setText("100");

		this.speed.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int value = speed.getSelection();
				speedLabel.setText("" + (value));
			}
		});

		// GENERAL RUNTIMES
		this.batchGeneration = new Label(genRuntimes, SWT.NONE);
		batchGeneration.setText("BatchGeneration:");
		this.batchGenerationValue = new Label(genRuntimes, SWT.NONE);
		batchGenerationValue.setText("0.0\t");
		batchGenerationValue.setAlignment(SWT.LEFT);

		this.total = new Label(genRuntimes, SWT.NONE);
		total.setText("Total:");
		this.totalValue = new Label(genRuntimes, SWT.NONE);
		totalValue.setText("0.0\t");
		totalValue.setAlignment(SWT.LEFT);

		this.sum = new Label(genRuntimes, SWT.NONE);
		sum.setText("Sumne:");
		this.sumValue = new Label(genRuntimes, SWT.NONE);
		sumValue.setText("0.0\t");
		sumValue.setAlignment(SWT.LEFT);

		this.overhead = new Label(genRuntimes, SWT.NONE);
		overhead.setText("Overhead:");
		this.overheadValue = new Label(genRuntimes, SWT.NONE);
		overheadValue.setText("0.0\t");
		overheadValue.setAlignment(SWT.LEFT);

		this.metrics = new Label(genRuntimes, SWT.NONE);
		metrics.setText("Metrics:");
		this.metricsValue = new Label(genRuntimes, SWT.NONE);
		metricsValue.setText("0.0\t");
		metricsValue.setAlignment(SWT.LEFT);

		this.graphUpdate = new Label(genRuntimes, SWT.NONE);
		graphUpdate.setText("GraphUpdate:");
		this.graphUpdateValue = new Label(genRuntimes, SWT.NONE);
		graphUpdateValue.setText("0.0\t");
		graphUpdateValue.setAlignment(SWT.LEFT);

		// METRIC RUNTIMES
		this.degreeDistUpdate = new Label(metRuntimes, SWT.NONE);
		degreeDistUpdate.setText("degreeDistUpdate:");
		this.degreeDistUpdateValue = new Label(metRuntimes, SWT.NONE);
		degreeDistUpdateValue.setText("0.0\t");
		degreeDistUpdateValue.setAlignment(SWT.LEFT);

		this.degreeDistRecomp = new Label(metRuntimes, SWT.NONE);
		degreeDistRecomp.setText("degreeDistRecomp:");
		this.degreeDistRecompValue = new Label(metRuntimes, SWT.NONE);
		degreeDistRecompValue.setText("0.0\t");
		degreeDistRecompValue.setAlignment(SWT.LEFT);

		this.closedTriCCUpdate = new Label(metRuntimes, SWT.NONE);
		closedTriCCUpdate.setText("closedTriCCUpdate:");
		this.closedTriCCUpdateValue = new Label(metRuntimes, SWT.NONE);
		closedTriCCUpdateValue.setText("0.0\t");
		closedTriCCUpdateValue.setAlignment(SWT.LEFT);

		this.closedTriCCRecomp = new Label(metRuntimes, SWT.NONE);
		closedTriCCRecomp.setText("closedTriCCRecomp:");
		this.closedTriCCRecompValue = new Label(metRuntimes, SWT.NONE);
		closedTriCCRecompValue.setText("0.0\t");
		closedTriCCRecompValue.setAlignment(SWT.LEFT);

		this.openTriCCUpdate = new Label(metRuntimes, SWT.NONE);
		openTriCCUpdate.setText("openTriCCUpdate:");
		this.openTriCCUpdateValue = new Label(metRuntimes, SWT.NONE);
		openTriCCUpdateValue.setText("0.0\t");
		openTriCCUpdateValue.setAlignment(SWT.LEFT);

		this.openTriCCRecomp = new Label(metRuntimes, SWT.NONE);
		openTriCCRecomp.setText("openTriCCRecomp:");
		this.openTriCCRecompValue = new Label(metRuntimes, SWT.NONE);
		openTriCCRecompValue.setText("0.0\t");
		openTriCCRecompValue.setAlignment(SWT.LEFT);

		this.setLayout(new GridLayout(1, true));
		this.pack();
		general.setLayout(new GridLayout(2, false));
		general.pack();
		genRuntimes.setLayout(new GridLayout(2, false));
		genRuntimes.pack();
		metRuntimes.setLayout(new GridLayout(2, false));
		metRuntimes.pack();
	}

	/**
	 * Updates the shown data with a new BatchData object.
	 * 
	 * @param b
	 */
	public void update(BatchData b) {
		ValueList values = b.getValues();
		this.timestampValue.setText("" + b.getTimestamp());
		this.nodesValue.setText("" + (int) values.get("nodes").getValue());
		this.edgesValue.setText("" + (int) values.get("edges").getValue());

		for (String s : b.getGeneralRuntimes().getNames()) {
			double value = b.getGeneralRuntimes().get(s).getRuntime();
			switch (s) {
			case "batchGeneration":
				this.batchGenerationValue.setText("" + value);
				break;
			case "total":
				this.totalValue.setText("" + value);
				break;
			case "sum":
				this.sumValue.setText("" + value);
				break;
			case "overhead":
				this.overheadValue.setText("" + value);
				break;
			case "metrics":
				this.metricsValue.setText("" + value);
				break;
			case "graphUpdate":
				this.graphUpdateValue.setText("" + value);
				break;
			}
		}
		for (String s : b.getMetricRuntimes().getNames()) {
			double value = b.getMetricRuntimes().get(s).getRuntime();
			switch (s) {
			case "closedTriangleClusteringCoefficientUpdate":
				this.closedTriCCUpdateValue.setText("" + value);
				break;
			case "closedTriangleClusteringCoefficientRecomp":
				this.closedTriCCRecompValue.setText("" + value);
				break;
			case "openTriangleClusteringCoefficientUpdate":
				this.openTriCCUpdateValue.setText("" + value);
				break;
			case "openTriangleClusteringCoefficientRecomp":
				this.openTriCCRecompValue.setText("" + value);
				break;
			case "degreeDistributionUpdate":
				this.degreeDistUpdateValue.setText("" + value);
				break;
			case "degreeDistributionRecomp":
				this.degreeDistRecompValue.setText("" + value);
				break;
			}
		}
	}

	public int getSpeed() {
		return 200 - speed.getSelection();
	}

	public void reset() {
		// reset values
		this.timestampValue.setText("" + 0);
		this.nodesValue.setText("" + 0);
		this.edgesValue.setText("" + 0);

		// reset progressbar
		this.progressLabel.setText("00.00 %");

		// reset general runtimes
		this.progressBar.setSelection(0);
		this.batchGenerationValue.setText("" + 0.0);
		this.totalValue.setText("" + 0.0);
		this.sumValue.setText("" + 0.0);
		this.overheadValue.setText("" + 0.0);
		this.metricsValue.setText("" + 0.0);
		this.graphUpdateValue.setText("" + 0.0);
		// reset metric runtimes
		this.closedTriCCUpdateValue.setText("" + 0.0);
		this.closedTriCCRecompValue.setText("" + 0.0);
		this.openTriCCUpdateValue.setText("" + 0.0);
		this.openTriCCRecompValue.setText("" + 0.0);
		this.degreeDistUpdateValue.setText("" + 0.0);
		this.degreeDistRecompValue.setText("" + 0.0);
	}
}
