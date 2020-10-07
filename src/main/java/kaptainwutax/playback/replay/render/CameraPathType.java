package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import kaptainwutax.playback.Playback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Function;

public class CameraPathType<T extends CameraPath> {
    public static final SimpleRegistry<CameraPathType<?>> REGISTRY = new SimpleRegistry<>(
            RegistryKey.ofRegistry(Playback.createIdentifier("camera_path_type")), Lifecycle.stable());

    private final Function<Dynamic<?>, T> constructor;

    private CameraPathType(Function<Dynamic<?>, T> constructor) {
        this.constructor = constructor;
    }

    public T create(Dynamic<?> config) {
        return constructor.apply(config);
    }

    private static <T extends CameraPath> CameraPathType<T> register(String id, CameraPathType<T> type) {
        return REGISTRY.add(RegistryKey.of(REGISTRY.getKey(), Playback.createIdentifier(id)), type, Lifecycle.stable());
    }

    public static Identifier getId(CameraPathType<?> type) {
        Identifier id = REGISTRY.getId(type);
        if (id == null) throw new IllegalStateException("CameraPathType without id!");
        return id;
    }

    public static final CameraPathType<FixedPointCameraPath> FIXED = register("fixed", new CameraPathType<>(FixedPointCameraPath::new));
    public static final CameraPathType<KeyFrameCameraPath> KEY_FRAMES = register("key_frames", new CameraPathType<>(KeyFrameCameraPath::new));
}
