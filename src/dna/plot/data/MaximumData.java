package dna.plot.data;

/**
 * @author benni
 * 
 */
public class MaximumData extends PlotData {

	public MaximumData(String data, String domain, PlotStyle style,
			String title, String source) {
		super(data, domain, style, title, source);
	}

	@Override
	public boolean isStyleValid() {
		return !this.style.equals(PlotStyle.candlesticks)
				&& !this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, boolean noTitle) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				this.style, noTitle);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY,
			DistributionPlotType distPlotType, boolean noTitle) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				distPlotType, this.style, noTitle);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, DistributionPlotType type,
			PlotStyle style, boolean noTitle) {
		// plot style
		PlotStyle styleTemp;
		DistributionPlotType distPlotType;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		if (type == null) {
			if (this.plotAsCdf)
				distPlotType = DistributionPlotType.cdfOnly;
			else
				distPlotType = DistributionPlotType.distOnly;
		} else {
			distPlotType = type;
		}

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		if (distPlotType.equals(DistributionPlotType.cdfOnly))
			buff.append(dataLoc + " using ($1 + " + offsetX + "):($4 + "
					+ offsetY + ") smooth cumulative with " + styleTemp);
		else
			buff.append(dataLoc + " using ($1 + " + offsetX + "):($4 + "
					+ offsetY + ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		if (noTitle || title == null)
			buff.append(" notitle");
		else
			buff.append(" title \"" + this.title + "\"");
		return buff.toString();
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, PlotStyle style, boolean noTitle) {
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

		// data point scaling
		String xpoint = "$1 + ";
		String ypoint = "$4 + ";
		if (!scalingX.equals("null"))
			xpoint = scalingX.replace("x", "$1") + " + ";
		if (!scalingY.equals("null"))
			ypoint = scalingY.replace("y", "$4") + " + ";

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		buff.append(dataLoc + " using (" + xpoint + offsetX + "):(" + ypoint
				+ offsetY + ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		if (noTitle || title == null)
			buff.append(" notitle");
		else
			buff.append(" title \"" + this.title + "\"");
		return buff.toString();
	}
}
