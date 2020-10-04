package kaptainwutax.playback.replay.render.interpolation;

import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import kaptainwutax.playback.replay.render.CameraState;
import net.minecraft.util.Identifier;

import java.util.*;

public class HierarchyInterpolator extends TreeMap<ComponentKey<?>, Interpolator> implements Interpolator {
    private final Set<ComponentKey<?>> missing = new LinkedHashSet<>();
    private Interpolator defaultInterpolator;

    public HierarchyInterpolator() {
        super(Comparator.comparingInt(ComponentKey::getDepth));
        missing.addAll(ComponentKey.KEY_FRAME.getLeafComponents());
    }

    public HierarchyInterpolator(Interpolator defaultInterpolator) {
        this();
        this.defaultInterpolator = defaultInterpolator;
    }

    public HierarchyInterpolator(Dynamic<?> config) {
        this();
        this.putAll(config.asMap(
            k -> ComponentKey.REGISTRY.get(new Identifier(k.asString().orElseThrow(IllegalArgumentException::new))),
            v -> {
                Identifier typeId = new Identifier(v.get("type").asString().orElseThrow(IllegalArgumentException::new));
                InterpolationType<?> type = InterpolationType.REGISTRY.get(typeId);
                if (type == null) throw new IllegalArgumentException("Unknown interpolation type " + typeId);
                Dynamic<?> cfg = v.get("config").orElseEmptyMap();
                return type.create(cfg);
            }
        ));
    }

    @Override
    public boolean canInterpolate(ComponentKey<?> property) {
        return property == ComponentKey.KEY_FRAME;
    }

    @Override
    public Interpolator put(ComponentKey<?> key, Interpolator value) {
        if (!value.canInterpolate(key)) throw new IllegalArgumentException(value.getType() + " cannot interpolate " + key);
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
    @SuppressWarnings("unchecked")
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
                    if (defaultInterpolator != null) {
                        defaultInterpolator.interpolate(k, states, from, to, t, dest);
                    } else {
                        LinearInterpolator.INSTANCE.interpolate((ComponentKey<Double>) k, stateFrom, stateTo, t, dest);
                    }
                }
            }
        }
    }

    @Override
    public InterpolationType<?> getType() {
        return InterpolationType.HIERARCHY;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        Map<T, T> map = new LinkedHashMap<>();
        for (Map.Entry<ComponentKey<?>, Interpolator> e : entrySet()) {
            Map<T, T> eMap = new LinkedHashMap<>();
            InterpolationType<?> type = e.getValue().getType();
            eMap.put(ops.createString("type"), ops.createString(InterpolationType.getId(type).toString()));
            T config = e.getValue().serialize(ops);
            if (ops.getType(config) != DSL.nilType()) {
                eMap.put(ops.createString("config"), config);
            }
            map.put(ops.createString(ComponentKey.getId(e.getKey()).toString()), ops.createMap(eMap));
        }
        return ops.createMap(map);
    }
}
