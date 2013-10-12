package dna.visualization.components.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

/**
 * Used within a metric visualizer component to give the user control over which
 * data is shown in the visualizer and how.
 * 
 * @author RWilmes
 * 
 */
public class Legend extends JPanel {

	private Legend thisLegend;
	private LegendList list;
	private JScrollPane scrollBar;

	private JPanel addButtonPanel;
	private JButton addButton;

	public Legend() {
		super();
		thisLegend = this;
		this.setPreferredSize(new Dimension(130, 320));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// init list and add it
		this.list = new LegendList();
		this.list.setPreferredSize(new Dimension(125, 280));
		c.gridx = 0;
		c.gridy = 0;
		this.add(list, c);
		this.scrollBar = new JScrollPane(this.list);
		this.add(scrollBar);

		// init addbutton panel and add it
		this.addButtonPanel = new JPanel();
		this.addButtonPanel.setPreferredSize(new Dimension(130, 20));
		this.addButtonPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.addButtonPanel.setLayout(new BoxLayout(this.addButtonPanel,
				BoxLayout.X_AXIS));

		// add button init
		this.addButton = new JButton("+");
		this.addButton.setForeground(Color.BLACK);
		this.addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Random r = new Random();
				thisLegend.addItemToList("test" + r.nextDouble());
			}
		});
		this.addButtonPanel.add(addButton);

		c.gridx = 0;
		c.gridy = 1;
		this.add(addButtonPanel, c);
	}

	public void addItemToList(String name) {
		this.list.addItem(name, Color.BLACK);
		this.validate();
	}

}
