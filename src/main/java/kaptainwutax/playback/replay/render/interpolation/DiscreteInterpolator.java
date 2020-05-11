package kaptainwutax.playback.replay.render.interpolation;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import kaptainwutax.playback.replay.render.CameraState;

import java.util.List;

public class DiscreteInterpolator implements Interpolator {
    public final float threshold;

    public DiscreteInterpolator(float threshold) {
        this.threshold = threshold;
    }

    public DiscreteInterpolator(Dynamic<?> config) {
        this(config.get("threshold").asFloat(0.5f));
    }

    @Override
    public boolean canInterpolate(ComponentKey<?> property) {
        return true;
    }

    @Override
    public <K> void interpolate(ComponentKey<K> key, List<? extends CameraState> states, int from, int to, float t, CameraState.Mutable dest) {
        if (t >= threshold) {
            key.set(dest, key.get(states.get(to)));
        } else {
            key.set(dest, key.get(states.get(from)));
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return ops.mergeInto(ops.emptyMap(), ops.createString("threshold"), ops.createFloat(threshold));
    }

    @Override
    public InterpolationType<?> getType() {
        return InterpolationType.DISCRETE;
    }
}
