package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

public interface CameraPath {

    Vec3d getCameraPositionAtTime(long tick, float tickDelta);
    Vector3f getCameraRotationAtTime(long tick, float tickDelta);

    GameTimeStamp getStartTime();
    GameTimeStamp getEndTime();
}
