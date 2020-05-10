package kaptainwutax.playback.replay.render;

public class KeyFrame extends CameraState {
    // TODO: position keyframes at certain frame numbers and use these for the replay timestamp
    public final long tick;
    public final float tickDelta;

    public KeyFrame(double x, double y, double z, double yaw, double pitch, double roll, long tick, float tickDelta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.tick = tick;
        this.tickDelta = tickDelta;
    }

    public GameTimeStamp getTimeStamp() {
        return new GameTimeStamp(this.tick, this.tickDelta);
    }

    public double getTimeStampAsDouble() {
        return (double)this.tick + this.tickDelta;
    }
}
