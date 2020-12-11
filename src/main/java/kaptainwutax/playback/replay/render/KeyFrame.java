package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

public class KeyFrame extends CameraState {
    public final int frame;

    public KeyFrame(Dynamic<?> config) {
        super(config);
        this.frame = config.get("frame").asInt(0);
    }

    public KeyFrame(int frame, GameTimeStamp time, double x, double y, double z, double yaw, double pitch, double roll, double fov, boolean renderPlayer, boolean renderGui) {
        super(time, x, y, z, yaw, pitch, roll, fov, renderPlayer, renderGui);
        this.frame = frame;
    }

    public KeyFrame(int frame, CameraState cameraState) {
        super(cameraState);
        this.frame = frame;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        T map = super.serialize(ops);
        return ops.mergeToMap(map, ops.createString("frame"), ops.createInt(frame)).get().orThrow();
    }

}
