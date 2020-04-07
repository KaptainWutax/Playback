package kaptainwutax.playback.replay.render;

public class KeyFrame {
    public final double x,y,z;
    public final float roll,pitch,yaw;
    public final long tickTimeOffset;
    public final float tickDeltaOffset;

    public KeyFrame(double x, double y, double z, float roll, float pitch, float yaw, long tick, float tickDelta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.tickTimeOffset = tick;
        this.tickDeltaOffset = tickDelta;
    }
}
