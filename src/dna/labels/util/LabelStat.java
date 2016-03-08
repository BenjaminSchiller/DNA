package dna.labels.util;

/**
 * An object which represents a label and contains statistical information.
 * 
 * @author Rwilmes
 * 
 */
public class LabelStat {

	private String identifier;
	private int total;

	private int negatives;
	private int positives;

	private int trueNegatives;
	private int falseNegatives;
	private int condNegatives;

	private int truePositives;
	private int falsePositives;
	private int condPositives;

	public LabelStat(String identifier) {
		this.identifier = identifier;
		this.total = 0;

		this.negatives = 0;
		this.positives = 0;

		this.trueNegatives = 0;
		this.falseNegatives = 0;
		this.condNegatives = 0;

		this.truePositives = 0;
		this.falsePositives = 0;
		this.condPositives = 0;
	}

	public void incrTrueNegatives() {
		this.trueNegatives++;
		this.negatives++;
		this.total++;
	}

	public void incrFalseNegatives() {
		this.falseNegatives++;
		this.negatives++;
		this.total++;
	}

	public void incrCondNegatives() {
		this.condNegatives++;
		this.negatives++;
		this.total++;
	}

	public void incrTruePositives() {
		this.truePositives++;
		this.positives++;
		this.total++;
	}

	public void incrFalsePositives() {
		this.falsePositives++;
		this.positives++;
		this.total++;
	}

	public void incrCondPositives() {
		this.condPositives++;
		this.positives++;
		this.total++;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public int getTotal() {
		return this.total;
	}

	public int getNegatives() {
		return this.negatives;
	}

	public int getPositives() {
		return this.positives;
	}

	public int getTrueNegatives() {
		return this.trueNegatives;
	}

	public int getFalseNegatives() {
		return this.falseNegatives;
	}

	public int getCondNegatives() {
		return this.condNegatives;
	}

	public int getTruePositives() {
		return this.truePositives;
	}

	public int getFalsePositives() {
		return this.falsePositives;
	}

	public int getCondPositives() {
		return this.condPositives;
	}

	public double getFlooredRateTotal(int value) {
		if (this.total == 0)
			return 0;
		return Math.floor(100.0 * value / this.total) / 100;
	}

	public double getFlooredRateNegatives(int value) {
		if (this.negatives == 0)
			return 0;
		return Math.floor(100.0 * value / this.negatives) / 100;
	}

	public double getFlooredRatePositives(int value) {
		if (this.positives == 0)
			return 0;
		return Math.floor(100.0 * value / this.positives) / 100;
	}

	public static void printInfoLine() {
		System.out.println("Name" + "\t\t\t" + "total" + "\t" + "#n" + "\t"
				+ "t-n" + "\t" + "f-n" + "\t" + "c-n" + "\t" + "#p" + "\t"
				+ "t-p" + "\t" + "f-p" + "\t" + "c-p");
	}

	public void printAll(boolean info) {
		if (info)
			printInfoLine();
		print();
		printRatesTotal();
		printRatesNegativesPositives();
	}

	public void print() {
		System.out.println(getIdentifier() + "\tt=" + getTotal() + "\tn="
				+ getNegatives() + "\t" + getTrueNegatives() + "\t"
				+ getFalseNegatives() + "\t" + getCondNegatives() + "\tp="
				+ getPositives() + "\t" + getTruePositives() + "\t"
				+ getFalsePositives() + "\t" + getCondPositives());
	}

	public void printRates() {
		System.out.println(getIdentifier() + "\t"
				+ getFlooredRateNegatives(getFalseNegatives()) + "\t"
				+ getFlooredRatePositives(getFalsePositives()) + "\t"
				+ getFlooredRatePositives(getCondPositives()));
	}

	public void printAllRates() {
		System.out.println(getIdentifier() + "\t"
				+ getFlooredRateNegatives(getTrueNegatives()) + "\t"
				+ getFlooredRateNegatives(getFalseNegatives()) + "\t"
				+ getFlooredRateNegatives(getCondNegatives()) + "\t"
				+ getFlooredRatePositives(getTruePositives()) + "\t"
				+ getFlooredRatePositives(getFalsePositives()) + "\t"
				+ getFlooredRatePositives(getCondPositives()));
	}

	protected void printRatesTotal() {
		System.out.println("rates-total:\t" + "\t\t\t"
				+ getFlooredRateTotal(getTrueNegatives()) + "\t"
				+ getFlooredRateTotal(getFalseNegatives()) + "\t"
				+ getFlooredRateTotal(getCondNegatives()) + "\t\t"
				+ getFlooredRateTotal(getTruePositives()) + "\t"
				+ getFlooredRateTotal(getFalsePositives()) + "\t"
				+ getFlooredRateTotal(getCondPositives()));
	}

	protected void printRatesNegativesPositives() {
		System.out.println("rates-pos/neg:\t" + "\t\t\t"
				+ getFlooredRateNegatives(getTrueNegatives()) + "\t"
				+ getFlooredRateNegatives(getFalseNegatives()) + "\t"
				+ getFlooredRateNegatives(getCondNegatives()) + "\t\t"
				+ getFlooredRatePositives(getTruePositives()) + "\t"
				+ getFlooredRatePositives(getFalsePositives()) + "\t"
				+ getFlooredRatePositives(getCondPositives()));
	}

}
