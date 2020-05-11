package kaptainwutax.playback.replay.render.interpolation;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import kaptainwutax.playback.replay.render.CameraState;

import java.util.List;

public class CatmullRomSplineInterpolator implements Interpolator {
    public final double alpha;

    public CatmullRomSplineInterpolator(double alpha) {
        this.alpha = alpha;
    }

    public CatmullRomSplineInterpolator(Dynamic<?> config) {
        this(config.get("alpha").asDouble(0.5));
    }

    @Override
    public boolean canInterpolate(ComponentKey<?> property) {
        return property.getType() == Double.class;
    }

    @Override
    public <K> void interpolate(ComponentKey<K> key, List<? extends CameraState> states, int from, int to, float t, CameraState.Mutable dest) {
        ComponentKey<Double> dKey = (ComponentKey<Double>) key;
        double p1 = dKey.get(states.get(from));
        double p2 = dKey.get(states.get(to));
        double p0 = from > 0 ? dKey.get(states.get(from - 1)) : p1;
        double p3 = to < states.size() - 1 ? dKey.get(states.get(to + 1)) : p2;
        Polynomial p = makePolynomial(p0, p1, p2, p3);
        dKey.set(dest, alpha * p.applyAsDouble(t));
    }

    private static Polynomial makePolynomial(double p0, double p1, double p2, double p3) {
        return new Polynomial(
            2 * p1,
            -p0 + p2,
            2 * p0 - 5 * p1 + 4 * p2 - p3,
            -p0 + 3 * p1 - 3 * p2 + p3
        );
    }

    @Override
    public InterpolationType<?> getType() {
        return InterpolationType.CATMULL_ROM_SPLINE;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return ops.mergeInto(ops.emptyMap(), ops.createString("threshold"), ops.createDouble(alpha));
    }
}
