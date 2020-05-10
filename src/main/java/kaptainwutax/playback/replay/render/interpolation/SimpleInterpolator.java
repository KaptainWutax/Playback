package kaptainwutax.playback.replay.render.interpolation;

import kaptainwutax.playback.replay.render.CameraState;

import java.util.List;

public abstract class SimpleInterpolator<T> implements Interpolator {
    @Override
    @SuppressWarnings("unchecked")
    public <K> void interpolate(ComponentKey<K> key, List<? extends CameraState> states, int from, int to, float t, CameraState.Mutable dest) {
        interpolate((ComponentKey<T>) key, states.get(from), states.get(to), t, dest);
    }

    public void interpolate(ComponentKey<T> key, CameraState from, CameraState to, float t, CameraState.Mutable dest) {
        key.set(dest, interpolate(key.get(from), key.get(to), t));
    }

    public abstract T interpolate(T a, T b, float t);
}
