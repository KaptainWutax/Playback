package kaptainwutax.playback.replay.render.interpolation;

import kaptainwutax.playback.replay.render.CameraState;

import java.util.*;

public class HierarchyInterpolator extends TreeMap<ComponentKey<?>, Interpolator> implements Interpolator {
    private final Set<ComponentKey<?>> missing = new LinkedHashSet<>();

    public HierarchyInterpolator() {
        super(Comparator.comparingInt(ComponentKey::getDepth));
        missing.addAll(ComponentKey.KEY_FRAME.getLeafComponents());
    }

    @Override
    public boolean canInterpolate(ComponentKey<?> property) {
        return property == ComponentKey.KEY_FRAME;
    }

    @Override
    public Interpolator put(ComponentKey<?> key, Interpolator value) {
        missing.removeAll(key.getLeafComponents());
        return super.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (!super.remove(key, value)) return false;
        recalculateMissing();
        return true;
    }

    @Override
    public Interpolator remove(Object key) {
        Interpolator removed = super.remove(key);
        if (removed != null) recalculateMissing();
        return removed;
    }

    private void recalculateMissing() {
        missing.addAll(ComponentKey.KEY_FRAME.getLeafComponents());
        for (ComponentKey<?> key : keySet()) missing.removeAll(key.getLeafComponents());
    }

    @Override
    public <K> void interpolate(ComponentKey<K> key, List<? extends CameraState> states, int from, int to, float t, CameraState.Mutable dest) {
        // Set values using configured interpolators from root to leaf
        for (Map.Entry<ComponentKey<?>, Interpolator> interp : entrySet()) {
            interp.getValue().interpolate(interp.getKey(), states, from, to, t, dest);
        }
        // Use linear interpolation for the values that weren't touched
        if (!missing.isEmpty()) {
            CameraState stateFrom = states.get(from);
            CameraState stateTo = states.get(to);
            for (ComponentKey<?> k : missing) {
                if (LinearAngleInterpolator.INSTANCE.canInterpolate(k)) {
                    LinearAngleInterpolator.INSTANCE.interpolate((ComponentKey<Double>) k, stateFrom, stateTo, t, dest);
                } else {
                    LinearInterpolator.INSTANCE.interpolate((ComponentKey<Double>) k, stateFrom, stateTo, t, dest);
                }
            }
        }
    }
}
