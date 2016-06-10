package dna.visualization.config.graph;

import dna.visualization.config.JSON.JSONObject;

public class ProjectionConfig {

	protected boolean enabled;
	protected boolean useVanishingPoint;
	protected boolean VanishingPointLogScale;

	protected double vp_X;
	protected double vp_Y;
	protected double vp_Z;
	protected double vpScalingFactor;

	protected double s0_X;
	protected double s0_Y;
	protected double s0_Z;

	protected double s1_X;
	protected double s1_Y;
	protected double s1_Z;

	protected double offset_X;
	protected double offset_Y;

	public ProjectionConfig(boolean enabled, boolean useVanishingPoint,
			boolean VanishingPointLogScale, double vp_X, double vp_Y,
			double vp_Z, double vpScalingFactor, double s0_X, double s0_Y,
			double s0_Z, double s1_X, double s1_Y, double s1_Z,
			double offset_X, double offset_Y) {
		this.enabled = enabled;
		this.useVanishingPoint = useVanishingPoint;
		this.VanishingPointLogScale = VanishingPointLogScale;

		this.vp_X = vp_X;
		this.vp_Y = vp_Y;
		this.vp_Z = vp_Z;
		this.vpScalingFactor = vpScalingFactor;

		this.s0_X = s0_X;
		this.s0_Y = s0_Y;
		this.s0_Z = s0_Z;

		this.s1_X = s1_X;
		this.s1_Y = s1_Y;
		this.s1_Z = s1_Z;

		this.offset_X = offset_X;
		this.offset_Y = offset_Y;
	}

	public static ProjectionConfig getFromJSONObject(JSONObject o) {
		GraphPanelConfig defaultGraphPanelConfig = GraphPanelConfig.defaultGraphPanelConfig;

		boolean enabled = false;
		boolean useVanishingPoint = false;
		boolean vanishingPointLogScale = true;

		double vp_X = 29.0;
		double vp_Y = 32.0;
		double vp_Z = 150.0;
		double vpScalingFactor = 1.0;

		double s0_X = 1.0;
		double s0_Y = 0.0;
		double s0_Z = 0.2;

		double s1_X = 0.0;
		double s1_Y = 1.0;
		double s1_Z = 0.25;

		double offset_X = 0.0;
		double offset_Y = 0.0;

		if (defaultGraphPanelConfig != null) {
			ProjectionConfig def = defaultGraphPanelConfig
					.getProjectionConfig();

			// get default values
			enabled = def.isEnabled();
			useVanishingPoint = def.isUseVanishingPoint();
			vanishingPointLogScale = def.isVanishingPointLogScale();

			vp_X = def.getVp_X();
			vp_Y = def.getVp_Y();
			vp_Z = def.getVp_Z();
			vpScalingFactor = def.getVpScalingFactor();

			s0_X = def.getS0_X();
			s0_Y = def.getS0_Y();
			s0_Z = def.getS0_Z();

			s1_X = def.getS1_X();
			s1_Y = def.getS1_Y();
			s1_Z = def.getS1_Z();

			offset_X = def.getOffset_X();
			offset_Y = def.getOffset_Y();
		}

		if (JSONObject.getNames(o) != null) {
			for (String s : JSONObject.getNames(o)) {
				switch (s) {
				case "Enabled":
					enabled = o.getBoolean(s);
					break;
				case "OffsetVector_X":
					offset_X = o.getDouble(s);
					break;
				case "OffsetVector_Y":
					offset_Y = o.getDouble(s);
					break;
				case "ScalingMatrixS0_X":
					s0_X = o.getDouble(s);
					break;
				case "ScalingMatrixS0_Y":
					s0_Y = o.getDouble(s);
					break;
				case "ScalingMatrixS0_Z":
					s0_Z = o.getDouble(s);
					break;
				case "ScalingMatrixS1_X":
					s1_X = o.getDouble(s);
					break;
				case "ScalingMatrixS1_Y":
					s1_Y = o.getDouble(s);
					break;
				case "ScalingMatrixS1_Z":
					s1_Z = o.getDouble(s);
					break;
				case "UseVanishingPoint":
					useVanishingPoint = o.getBoolean(s);
					break;
				case "VanishingPointLogScale":
					vanishingPointLogScale = o.getBoolean(s);
					break;
				case "VanishingPoint_X":
					vp_X = o.getDouble(s);
					break;
				case "VanishingPoint_Y":
					vp_Y = o.getDouble(s);
					break;
				case "VanishingPoint_Z":
					vp_Z = o.getDouble(s);
					break;
				}
			}
		}

		return new ProjectionConfig(enabled, useVanishingPoint,
				vanishingPointLogScale, vp_X, vp_Y, vp_Z, vpScalingFactor,
				s0_X, s0_Y, s0_Z, s1_X, s1_Y, s1_Z, offset_X, offset_Y);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isUseVanishingPoint() {
		return useVanishingPoint;
	}

	public boolean isVanishingPointLogScale() {
		return VanishingPointLogScale;
	}

	public double getVp_X() {
		return vp_X;
	}

	public double getVp_Y() {
		return vp_Y;
	}

	public double getVp_Z() {
		return vp_Z;
	}

	public double getVpScalingFactor() {
		return vpScalingFactor;
	}

	public double getS0_X() {
		return s0_X;
	}

	public double getS0_Y() {
		return s0_Y;
	}

	public double getS0_Z() {
		return s0_Z;
	}

	public double getS1_X() {
		return s1_X;
	}

	public double getS1_Y() {
		return s1_Y;
	}

	public double getS1_Z() {
		return s1_Z;
	}

	public double getOffset_X() {
		return offset_X;
	}

	public double getOffset_Y() {
		return offset_Y;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setUseVanishingPoint(boolean useVanishingPoint) {
		this.useVanishingPoint = useVanishingPoint;
	}

	public void setVanishingPointLogScale(boolean vanishingPointLogScale) {
		VanishingPointLogScale = vanishingPointLogScale;
	}

	public void setVp_X(double vp_X) {
		this.vp_X = vp_X;
	}

	public void setVp_Y(double vp_Y) {
		this.vp_Y = vp_Y;
	}

	public void setVp_Z(double vp_Z) {
		this.vp_Z = vp_Z;
	}

	public void setVpScalingFactor(double vpScalingFactor) {
		this.vpScalingFactor = vpScalingFactor;
	}

	public void setS0_X(double s0_X) {
		this.s0_X = s0_X;
	}

	public void setS0_Y(double s0_Y) {
		this.s0_Y = s0_Y;
	}

	public void setS0_Z(double s0_Z) {
		this.s0_Z = s0_Z;
	}

	public void setS1_X(double s1_X) {
		this.s1_X = s1_X;
	}

	public void setS1_Y(double s1_Y) {
		this.s1_Y = s1_Y;
	}

	public void setS1_Z(double s1_Z) {
		this.s1_Z = s1_Z;
	}

	public void setOffset_X(double offset_X) {
		this.offset_X = offset_X;
	}

	public void setOffset_Y(double offset_Y) {
		this.offset_Y = offset_Y;
	}

}
