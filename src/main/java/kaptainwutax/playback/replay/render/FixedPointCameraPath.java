package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedPointCameraPath extends AbstractCameraPath {
    private final CameraState state;
    private final GameTimeStamp startTime;
    private final GameTimeStamp endTime;

    public FixedPointCameraPath(Dynamic<?> config) {
        super(config);
        state = new CameraState(config.get("state").get().orElseThrow(IllegalArgumentException::new));
        startTime = new GameTimeStamp(config.get("start").get().orElseThrow(IllegalArgumentException::new));
        endTime = new GameTimeStamp(config.get("end").get().orElseThrow(IllegalArgumentException::new));
    }

    public FixedPointCameraPath(int frames, CameraState state, GameTimeStamp startTime, GameTimeStamp endTime) {
        super(frames);
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public CameraState getCameraStateAtTime(int frame) {
        return linearTime(state, frame, frames, startTime, endTime);
    }

    @Override
    public CameraPathType<?> getType() {
        return CameraPathType.FIXED;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        Map<T, T> map = new LinkedHashMap<>();
        map.put(ops.createString("state"), state.serialize(ops));
        map.put(ops.createString("start"), startTime.serialize(ops));
        map.put(ops.createString("end"), endTime.serialize(ops));
        return ops.createMap(map);
    }
}
