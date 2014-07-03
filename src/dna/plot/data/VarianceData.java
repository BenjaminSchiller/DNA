package dna.plot.data;

/**
 * @author benni
 * 
 */
public class VarianceData extends PlotData {

	public VarianceData(String data, String domain, PlotStyle style,
			String title) {
		super(data, domain, style, title);
	}

	@Override
	public boolean isStyleValid() {
		return this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
		return this.getEntry(lt, lw, offsetX, offsetY, this.style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType, PlotStyle style) {
		return this.getEntry(lt, lw, offsetX, offsetY, style);
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
		// 2 avg
		// 3 med
		// 4 min
		// 5 max
		// 6 var
		// 7 varLow
		// 8 varUp
		// 9 confLow
		// 10 confUp
		buff.append(dataLoc + " using ($1 + " + offsetX + "):($2 + " + offsetY
				+ "):($2 - $7 + " + offsetY + "):($2 + $8 + " + offsetY
				+ ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}
}
