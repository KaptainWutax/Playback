package kaptainwutax.playback.gui;

public class WindowSize {
    private final int width;
    private final int height;
    private final int scaledWidth;
    private final int scaledHeight;
    private final double scaleFactor;
    private final int framebufferWidth;
    private final int framebufferHeight;

    public WindowSize(int width, int height, int scaledWidth, int scaledHeight, double scaleFactor, int framebufferWidth, int framebufferHeight) {
        this.width = width;
        this.height = height;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.scaleFactor = scaleFactor;
        this.framebufferWidth = framebufferWidth;
        this.framebufferHeight = framebufferHeight;
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

    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }
}
