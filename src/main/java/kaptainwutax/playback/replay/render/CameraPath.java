package kaptainwutax.playback.replay.render;

import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public interface CameraPath {

    default Vec3d getCameraPositionAtTime(long tick, float tickDelta) {
        throw new UnsupportedOperationException();
    }
    default Quaternion getCameraRotationAtTime(long tick, float tickDelta) {
        throw new UnsupportedOperationException();
    }
}
