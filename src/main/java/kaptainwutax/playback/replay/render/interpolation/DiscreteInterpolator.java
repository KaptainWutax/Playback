package kaptainwutax.playback.replay.render.interpolation;

import kaptainwutax.playback.replay.render.CameraState;

import java.util.List;

public class DiscreteInterpolator implements Interpolator {
    public final float threshold;

    public DiscreteInterpolator(float threshold) {
        this.threshold = threshold;
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
}
