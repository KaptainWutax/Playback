package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

public class FixedPointCameraPath implements CameraPath {

    private KeyFrame keyFrame;
    private GameTimeStamp startTime;
    private GameTimeStamp endTime;

    public FixedPointCameraPath(KeyFrame keyFrame, GameTimeStamp startTime, GameTimeStamp endTime) {
        this.keyFrame = keyFrame;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Vec3d getCameraPositionAtTime(long tick, float tickDelta) {
        if ((this.startTime.tick > tick) || ((this.startTime.tick == tick) && (this.startTime.tickDelta > tickDelta)) ||
                (this.endTime.tick < tick) || ((this.endTime.tick == tick) && (this.endTime.tickDelta < tickDelta))) {
            return null;
        }

        return new Vec3d(this.keyFrame.x, this.keyFrame.y, this.keyFrame.z);
    }

    @Override
    public Vector3f getCameraRotationAtTime(long tick, float tickDelta) {
        if ((this.startTime.tick > tick) || ((this.startTime.tick == tick) && (this.startTime.tickDelta > tickDelta)) ||
                (this.endTime.tick < tick) || ((this.endTime.tick == tick) && (this.endTime.tickDelta < tickDelta))) {
            return null;
        }
        return new Vector3f(this.keyFrame.roll, this.keyFrame.pitch, this.keyFrame.yaw);
    }

    @Override
    public GameTimeStamp getStartTime() {
        return this.startTime;
    }

    @Override
    public GameTimeStamp getEndTime() {
        return this.endTime;
    }
}
