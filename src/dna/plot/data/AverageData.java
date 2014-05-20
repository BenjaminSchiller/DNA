package dna.plot.data;

/**
 * @author benni
 * 
 */
public class AverageData extends PlotData {

	public AverageData(String data, String domain, PlotStyle style, String title) {
		super(data, domain, style, title);
	}

	@Override
	public boolean isStyleValid() {
		return !this.style.equals(PlotStyle.candlesticks)
				&& !this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
		StringBuffer buff = new StringBuffer();
		buff.append("'-' using ($1 + " + offsetX + "):($2 + " + offsetY
				+ ") with " + this.style);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}

	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		StringBuffer buff = new StringBuffer();
		if (distPlotType.equals(DistributionPlotType.cdfOnly))
			buff.append("'-' using ($1 + " + offsetX + "):($2 + " + offsetY
					+ ") smooth cumulative with " + this.style);
		else
			buff.append("'-' using ($1 + " + offsetX + "):($2 + " + offsetY
					+ ") with " + this.style);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}

}
