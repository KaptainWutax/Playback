package kaptainwutax.playback.replay.render.interpolation;

import com.mojang.serialization.DynamicOps;
import kaptainwutax.playback.replay.render.CameraState;

import java.util.List;

public interface Interpolator {
    boolean canInterpolate(ComponentKey<?> property);
    <K> void interpolate(ComponentKey<K> key, List<? extends CameraState> states, int from, int to, float t, CameraState.Mutable dest);
    InterpolationType<?> getType();
    default <T> T serialize(DynamicOps<T> ops) {
        return ops.empty();
    }
}
