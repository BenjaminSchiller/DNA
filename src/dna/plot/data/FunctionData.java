package dna.plot.data;

/**
 * @author benni
 * 
 */
public class FunctionData extends PlotData {

	public FunctionData(String functionName, String formular, PlotStyle style,
			String title, String source) {
		super(functionName, formular, style, title, source);
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
				noTitle);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY,
			DistributionPlotType distPlotType, PlotStyle style, boolean noTitle) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				style, noTitle);
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

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		buff.append(this.data + " with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(noTitle ? " notitle" : " title \"" + this.getLine() + "\"");

		return buff.toString();
	}

	public String getLine() {
		return super.data + "=" + super.domain;
	}

}
