package kaptainwutax.playback.gui;

public class WindowSize {
    private final int width;
    private final int height;
    private final int scaledWidth;
    private final int scaledHeight;
    private final double scaleFactor;

    public WindowSize(int width, int height, int scaledWidth, int scaledHeight, double scaleFactor) {
        this.width = width;
        this.height = height;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.scaleFactor = scaleFactor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }
}
