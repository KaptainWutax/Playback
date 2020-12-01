package kaptainwutax.playback.replay.encoding;

import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.util.function.BiConsumer;

public abstract class Encoder<O extends Encoder.Options> implements AutoCloseable {
    public final O options;

    public Encoder(O options) {
        this.options = options;
    }

    public void open() throws IOException {

    }

    public abstract void captureFrame(BiConsumer<DirectBuffer, Integer> render) throws IOException;

    public static class Options {
        public final int width;
        public final int height;
        public final float frameRate;

        public Options(int width, int height, float frameRate) {
            this.width = width;
            this.height = height;
            this.frameRate = frameRate;
        }
    }
}
