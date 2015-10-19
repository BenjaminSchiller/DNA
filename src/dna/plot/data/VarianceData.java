package dna.plot.data;

/**
 * @author benni
 * 
 */
public class VarianceData extends PlotData {

	public VarianceData(String data, String domain, PlotStyle style,
			String title, String source) {
		super(data, domain, style, title, source);
	}

	@Override
	public boolean isStyleValid() {
		return this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				this.style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY,
			DistributionPlotType distPlotType, PlotStyle style) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, PlotStyle style) {
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
		String ypoint = "$2 + ";
		String yvarlow = "$2 - $7 + ";
		String yvarhigh = "$2 + $8 + ";
		if (!scalingX.equals("null"))
			xpoint = scalingX.replace("x", "$1") + " + ";
		if (!scalingY.equals("null")) {
			ypoint = scalingY.replace("y", "$2") + " + ";
			yvarlow = scalingY.replace("y", "$2 - $7") + " + ";
			yvarhigh = scalingY.replace("y", "$2 + $8") + " + ";
		}

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
		buff.append(dataLoc + " using (" + xpoint + offsetX + "):(" + ypoint
				+ offsetY + "):(" + yvarlow + offsetY + "):(" + yvarhigh
				+ offsetY + ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}
}
