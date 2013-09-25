package dna.visualization;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import dna.series.data.BatchData;
import dna.visualization.components.StatsDisplay;

public class Visualization {

	public static void main(String[] args) throws IOException {
		String dir = "data/test15/run.0/";
		final BatchHandler bh = new BatchHandler(dir);
		bh.updateBatches();

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("DNA - Dynamic Network Analyzer");
		shell.setImage(new Image(display, "logo/dna.png"));
		/*
		 * GROUPS / COMPOSITES
		 */
		Group statistics = new Group(shell, SWT.SHADOW_IN);
		statistics.setText("Statistics");

		final StatsDisplay statsModule = new StatsDisplay(statistics, SWT.NONE);
		statsModule.setLayout(new GridLayout(1, false));
		statsModule.pack();

		Group buttons = new Group(statistics, SWT.SHADOW_ETCHED_OUT);
		buttons.setLayout(new GridLayout(3, true));
		buttons.pack();

		Image logo = new Image(display,
				"logo/dna-logo-v5.png");
		Button logoButton = new Button(statistics, SWT.NONE);
		logoButton.setImage(logo);

		/*
		 * BUTTONS
		 */
		Button start = new Button(buttons, SWT.PUSH);
		start.setText("Start");

		Button reset = new Button(buttons, SWT.PUSH);
		reset.setText("Reset");

		/*
		 * BUTTON SCRIPTS
		 */
		start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					while (bh.isNewBatchAvailable()) {
						// progress calculation
						double coeff = 100 / (double) bh.getBatches().size();
						double progress = coeff * (bh.getIndex());
						statsModule.progressBar.setSelection((int) progress);
						statsModule.progressLabel.setText(((Math
								.floor(progress * 100)) / 100) + " %");
						// batchdata update
						BatchData tempBatch = bh.getNextBatch();
						statsModule.update(tempBatch);
						Thread.sleep(statsModule.getSpeed());
					}
					statsModule.progressBar.setSelection(100);
					statsModule.progressLabel.setText("100.00 %");
				} catch (Exception exc) {
					exc.printStackTrace();
				}

			}
		});

		reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				bh.setIndex(0);
				statsModule.reset();
			}
		});

		//
		//
		//
		// OPENING SHELL
		//
		//
		//
		statistics.setLayout(new GridLayout(1, true));
		statistics.pack();
		shell.setLayout(new RowLayout());
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
