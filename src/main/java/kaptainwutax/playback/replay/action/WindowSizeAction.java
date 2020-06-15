package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class WindowSizeAction extends Action {
    private WindowSize windowSize;

    public WindowSizeAction() {
        super(true);
    }

    public WindowSizeAction(WindowSize windowSize) {
        this();
        this.windowSize = windowSize;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        super.read(buf);
        int width = buf.readVarInt();
        int height = buf.readVarInt();
        int scaledHeight = buf.readVarInt();
        int scaledWidth = buf.readVarInt();
        double scaleFactor = buf.readDouble();

        this.windowSize = new WindowSize(width, height, scaledWidth, scaledHeight, scaleFactor);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        super.write(buf);
        buf.writeVarInt(this.windowSize.getWidth());
        buf.writeVarInt(this.windowSize.getHeight());
        buf.writeVarInt(this.windowSize.getScaledHeight());
        buf.writeVarInt(this.windowSize.getScaledWidth());
        buf.writeDouble(this.windowSize.getScaleFactor());
    }

    @Override
    public void play() {
        Playback.getManager().recording.setCurrentRecordedWindowSize(this.windowSize);
    }
}
