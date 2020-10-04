package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface CameraPath {
    CameraState getCameraStateAtTime(int frame);

    int getFrames();

    default GameTimeStamp getStartTime() {
        return getCameraStateAtTime(0).time;
    }

    default GameTimeStamp getEndTime() {
        return getCameraStateAtTime(getFrames() - 1).time;
    }

    CameraPathType<?> getType();
    <T> T serialize(DynamicOps<T> ops);

    static CameraPath deserialize(Dynamic<?> config) {
        Identifier typeId = new Identifier(config.get("type").asString().orElseThrow(IllegalArgumentException::new));
        CameraPathType<?> type = CameraPathType.REGISTRY.get(typeId);
        if (type == null) throw new IllegalArgumentException("Unknown camera path type " + typeId);
        return type.create(config.get("value").get().orElseThrow(IllegalArgumentException::new));
    }

    static <T> T serialize(DynamicOps<T> ops, CameraPath path) {
        Map<T, T> map = new LinkedHashMap<>();
        map.put(ops.createString("type"), ops.createString(CameraPathType.getId(path.getType()).toString()));
        map.put(ops.createString("value"), path.serialize(ops));
        return ops.createMap(map);
    }
}
