package kaptainwutax.playback.render.util;

public class Color {

	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);

	private final int rgb;

	public Color(int rgb) {
		this.rgb = rgb;
	}

	public Color(int red, int green, int blue) {
		this(red << 16 | green << 8 | blue);
	}

	public int getRed() {
		return this.rgb >> 16;
	}

	public int getGreen() {
		return (this.rgb >> 8) & ((1 << 8) - 1);
	}

	public int getBlue() {
		return this.rgb & ((1 << 8) - 1);
	}

	public int getRGB() {
		return this.rgb;
	}

	public float getFRed() {
		return this.getRed() / 255.0F;
	}

	public float getFGreen() {
		return this.getGreen() / 255.0F;
	}

	public float getFBlue() {
		return this.getBlue() / 255.0F;
	}

}
