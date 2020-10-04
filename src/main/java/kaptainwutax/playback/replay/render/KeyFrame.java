package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

public class KeyFrame extends CameraState {
    public final int frame;

    public KeyFrame(Dynamic<?> config) {
        super(config);
        this.frame = config.get("frame").asInt(0);
    }

    public KeyFrame(int frame, GameTimeStamp time, double x, double y, double z, double yaw, double pitch, double roll, double fov) {
        super(time, x, y, z, yaw, pitch, roll, fov);
        this.frame = frame;
    }

    public <T> T serialize(DynamicOps<T> ops) {
        T map = super.serialize(ops);
        return ops.mergeInto(map, ops.createString("frame"), ops.createInt(frame));
    }
}
