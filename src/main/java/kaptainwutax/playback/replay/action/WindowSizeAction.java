package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;

public class WindowSizeAction extends Action {
    private WindowSize windowSize;
    private boolean runOnResolutionChanged;

    public WindowSizeAction() {
        super(true);
    }

    public WindowSizeAction(WindowSize windowSize, boolean runOnResolutionChanged) {
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
        int framebufferWidth = buf.readVarInt();
        int framebufferHeight = buf.readVarInt();

        this.windowSize = new WindowSize(width, height, scaledWidth, scaledHeight, scaleFactor, framebufferWidth, framebufferHeight);
        this.runOnResolutionChanged = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        super.write(buf);
        buf.writeVarInt(this.windowSize.getWidth());
        buf.writeVarInt(this.windowSize.getHeight());
        buf.writeVarInt(this.windowSize.getScaledHeight());
        buf.writeVarInt(this.windowSize.getScaledWidth());
        buf.writeDouble(this.windowSize.getScaleFactor());
        buf.writeVarInt(this.windowSize.getFramebufferWidth());
        buf.writeVarInt(this.windowSize.getFramebufferHeight());

        buf.writeBoolean(this.runOnResolutionChanged);
    }

    @Override
    public void play() {
        Playback.getManager().recording.setCurrentRecordedWindowSize(this.windowSize);
        if (this.runOnResolutionChanged)
            MinecraftClient.getInstance().onResolutionChanged();
    }
}
