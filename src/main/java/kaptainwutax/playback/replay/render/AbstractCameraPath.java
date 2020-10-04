package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractCameraPath implements CameraPath {
    protected int frames;

    protected AbstractCameraPath(Dynamic<?> config) {
        this.frames = config.get("frames").asInt(1);
    }

    protected AbstractCameraPath(int frames) {
        this.frames = frames;
    }

    protected double getProgress(int frame) {
        return getProgress(frame, frames);
    }

    protected static double getProgress(int frame, int frames) {
        return (double) frame / frames;
    }

    protected static CameraState linearTime(CameraState base, int frame, int frames, GameTimeStamp start, GameTimeStamp end) {
        CameraState.Mutable mutable = new CameraState.Mutable(base);
        mutable.setTime(MathHelper.lerp(getProgress(frame, frames), start.asDouble(), end.asDouble()));
        return mutable;
    }

    @Override
    public int getFrames() {
        return frames;
    }
}
