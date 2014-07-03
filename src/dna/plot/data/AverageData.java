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
		return this.getEntry(lt, lw, offsetX, offsetY, this.style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			PlotStyle style) {
		// plot style
		PlotStyle styleTemp;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		buff.append(dataLoc + " using ($1 + " + offsetX + "):($2 + " + offsetY
				+ ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		return this
				.getEntry(lt, lw, offsetX, offsetY, distPlotType, this.style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType, PlotStyle style) {
		// plot style
		PlotStyle styleTemp;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		if (distPlotType.equals(DistributionPlotType.cdfOnly))
			buff.append(dataLoc + " using ($1 + " + offsetX + "):($2 + "
					+ offsetY + ") smooth cumulative with " + styleTemp);
		else
			buff.append(dataLoc + " using ($1 + " + offsetX + "):($2 + "
					+ offsetY + ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}
}
