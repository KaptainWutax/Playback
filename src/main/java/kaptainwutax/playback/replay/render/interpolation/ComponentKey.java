package kaptainwutax.playback.replay.render.interpolation;

import kaptainwutax.playback.replay.render.CameraState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ComponentKey<T> {
    public static final Registry<ComponentKey<?>> REGISTRY = new SimpleRegistry<>();

    @Nullable
    private final ComponentKey<?> parent;
    private final Function<CameraState, T> getter;
    private final BiConsumer<CameraState.Mutable, T> setter;
    private final Class<T> type;
    private final List<ComponentKey<?>> subComponents = new ArrayList<>();
    private final int depth;

    protected ComponentKey(@Nullable ComponentKey<?> parent, Class<T> type, Function<CameraState, T> getter, BiConsumer<CameraState.Mutable, T> setter) {
        this.parent = parent;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        if (parent != null) {
            parent.subComponents.add(this);
            this.depth = parent.depth + 1;
        } else {
            this.depth = 0;
        }
    }

    public T get(CameraState state) {
        return getter.apply(state);
    }

    public void set(CameraState.Mutable state, T value) {
        setter.accept(state, value);
    }

    public Class<T> getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

    public List<ComponentKey<?>> getSubComponents() {
        return Collections.unmodifiableList(subComponents);
    }

    public List<ComponentKey<?>> getLeafComponents() {
        if (subComponents.isEmpty()) return Collections.singletonList(this);
        List<ComponentKey<?>> leafs = new ArrayList<>();
        for (ComponentKey<?> sub : subComponents) leafs.addAll(sub.getLeafComponents());
        return leafs;
    }

    private static <T> ComponentKey<T> register(String key, ComponentKey<T> component) {
        return ((MutableRegistry<ComponentKey<?>>) REGISTRY).add(new Identifier("playback", key), component);
    }

    public static Identifier getId(ComponentKey<?> key) {
        Identifier id = REGISTRY.getId(key);
        if (id == null) throw new IllegalStateException("ComponentKey without id!");
        return id;
    }

    public static final ComponentKey<CameraState> KEY_FRAME = register("key_frame", new ComponentKey<>(null, CameraState.class, Function.identity(), CameraState.Mutable::set));
    public static final ComponentKey<Double> TIME = register("time", new ComponentKey<>(KEY_FRAME, Double.class, CameraState::getTimeAsDouble, CameraState.Mutable::setTime));
    public static final ComponentKey<Vec3d> POSITION = register("position", new ComponentKey<>(KEY_FRAME, Vec3d.class, CameraState::getPosition, CameraState.Mutable::setPosition));
    public static final ComponentKey<Double> X = register("x", new ComponentKey<>(POSITION, Double.class, CameraState::getX, CameraState.Mutable::setX));
    public static final ComponentKey<Double> Y = register("y", new ComponentKey<>(POSITION, Double.class, CameraState::getY, CameraState.Mutable::setY));
    public static final ComponentKey<Double> Z = register("z", new ComponentKey<>(POSITION, Double.class, CameraState::getZ, CameraState.Mutable::setZ));
    public static final ComponentKey<Vec3d> ROTATION = register("rotation", new ComponentKey<>(KEY_FRAME, Vec3d.class, CameraState::getRotation, CameraState.Mutable::setRotation));
    public static final ComponentKey<Double> YAW = register("yaw", new ComponentKey<>(ROTATION, Double.class, CameraState::getYaw, CameraState.Mutable::setYaw));
    public static final ComponentKey<Double> PITCH = register("pitch", new ComponentKey<>(ROTATION, Double.class, CameraState::getPitch, CameraState.Mutable::setPitch));
    public static final ComponentKey<Double> ROLL = register("roll", new ComponentKey<>(ROTATION, Double.class, CameraState::getRoll, CameraState.Mutable::setRoll));
    public static final ComponentKey<Double> FOV = register("fov", new ComponentKey<>(KEY_FRAME, Double.class, CameraState::getFov, CameraState.Mutable::setFov));
}
