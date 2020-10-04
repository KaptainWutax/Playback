package kaptainwutax.playback.replay.render.interpolation;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Function;

public class InterpolationType<T extends Interpolator> {
    public static final Registry<InterpolationType<?>> REGISTRY = new SimpleRegistry<>();
    private final Function<Dynamic<?>, T> constructor;

    private InterpolationType(Function<Dynamic<?>, T> constructor) {
        this.constructor = constructor;
    }

    public T create(Dynamic<?> config) {
        return constructor.apply(config);
    }

    private static <T extends Interpolator> InterpolationType<T> register(String id, InterpolationType<T> type) {
        return ((MutableRegistry<InterpolationType<?>>) REGISTRY).add(new Identifier("playback", id), type);
    }

    public static Identifier getId(InterpolationType<?> type) {
        Identifier id = REGISTRY.getId(type);
        if (id == null) throw new IllegalStateException("InterpolationType without id!");
        return id;
    }

    public static final InterpolationType<LinearInterpolator> LINEAR = register("linear", new InterpolationType<>(LinearInterpolator::new));
    public static final InterpolationType<LinearAngleInterpolator> LINEAR_ANGLE = register("linear_angle", new InterpolationType<>(LinearAngleInterpolator::new));
    public static final InterpolationType<DiscreteInterpolator> DISCRETE = register("discrete", new InterpolationType<>(DiscreteInterpolator::new));
    public static final InterpolationType<HierarchyInterpolator> HIERARCHY = register("hierarchy", new InterpolationType<>(HierarchyInterpolator::new));
    public static final InterpolationType<CatmullRomSplineInterpolator> CATMULL_ROM_SPLINE = register("catmull_rom_spline", new InterpolationType<>(CatmullRomSplineInterpolator::new));
}
