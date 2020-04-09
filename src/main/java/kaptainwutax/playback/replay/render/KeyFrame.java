package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

public class KeyFrame {
    public final double x,y,z;
    public final float roll,pitch,yaw;
    public final long tick;
    public final float tickDelta;

    public KeyFrame(double x, double y, double z, float roll, float pitch, float yaw, long tick, float tickDelta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.tick = tick;
        this.tickDelta = tickDelta;
    }

    public CameraPath.GameTimeStamp getTimeStamp() {
        return new CameraPath.GameTimeStamp(this.tick, this.tickDelta);
    }

    public Vec3d getPositionVec() {
        return new Vec3d(x,y,z);
    }

    public Vector3f getRotationVec() {
        return new Vector3f(roll,pitch,yaw);
    }
}
