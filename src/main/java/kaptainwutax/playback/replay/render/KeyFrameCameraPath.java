package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kaptainwutax.playback.replay.render.interpolation.ComponentKey;
import kaptainwutax.playback.replay.render.interpolation.HierarchyInterpolator;

import java.util.*;

public class KeyFrameCameraPath extends AbstractCameraPath {
    private static final HierarchyInterpolator DEFAULT_INTERPOLATOR = new HierarchyInterpolator();

    private final List<KeyFrame> keyFrames = new ArrayList<>();
    private final Int2ObjectMap<HierarchyInterpolator> interpolators = new Int2ObjectOpenHashMap<>();

    {
        interpolators.defaultReturnValue(DEFAULT_INTERPOLATOR);
    }

    private HierarchyInterpolator nextInterp;

    public <T> KeyFrameCameraPath(Dynamic<T> config) {
        super(config);
        List<Object> list = config.asList(e -> {
            String type = e.get("type").asString(null);
            if (type == null) return null;
            Optional<Dynamic<T>> value = e.get("value").get().result();
            if (!value.isPresent()) return null;
            switch (type) {
                case "key_frame": return new KeyFrame(value.get());
                case "interpolation": return new HierarchyInterpolator(value.get());
            }
            return null;
        });
        for (Object o : list) {
            if (o instanceof HierarchyInterpolator) interpolate(((HierarchyInterpolator) o));
            else if (o instanceof KeyFrame) {
                KeyFrame kf = (KeyFrame) o;
                keyFrame(kf);
                frames = Math.max(frames, kf.frame);
            }
        }
    }

    public KeyFrameCameraPath(int frames) {
        super(frames);
    }

    public KeyFrameCameraPath keyFrame(KeyFrame kf) {
        // find previous index and insert there to keep the list sorted
        int index = getLowerIndexForFrame(kf.frame) + 1;
        if (nextInterp != null && index > 0) {
            interpolators.put(index - 1, nextInterp);
            nextInterp = null;
        }
        keyFrames.add(index, kf);
        frames = Math.max(frames, kf.frame);
        return this;
    }

    public KeyFrameCameraPath interpolate(HierarchyInterpolator interp) {
        nextInterp = interp;
        return this;
    }

    @Override
    public CameraState getCameraStateAtTime(int frame) {
        int i = this.getLowerIndexForFrame(frame);
        if (i < 0) return keyFrames.get(0);

        KeyFrame prev = this.keyFrames.get(i);
        if (prev.frame > frame || i + 1 >= this.keyFrames.size()) {
            return prev;
        }
        KeyFrame next = this.keyFrames.get(i+1);
        CameraState.Mutable state = new CameraState.Mutable();
        float delta = (float) (frame - prev.frame) / (next.frame - prev.frame);
        interpolators.get(i).interpolate(ComponentKey.KEY_FRAME, keyFrames, i, i + 1, delta, state);
        return state;
    }

    public List<KeyFrame> getKeyFrames() {
        return keyFrames;
    }

    private int getLowerIndexForFrame(int frame) {
        if (keyFrames.isEmpty()) return -1;
        // binary search for the matching key frame
        int start = 0;
        int end = keyFrames.size() - 1;
        while (start <= end) {
            int mid = (start + end) >>> 1;
            int midVal = keyFrames.get(mid).frame;

            if (midVal < frame) {
                start = mid + 1;
            } else if (midVal > frame) {
                end = mid - 1;
            } else {
                return mid; // exact match
            }
        }
        // no exact match (common) -> return the previous index
        return start - 1;
    }

    @Override
    public CameraPathType<?> getType() {
        return CameraPathType.KEY_FRAMES;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        int size = keyFrames.size();
        List<T> list = new ArrayList<>(size + interpolators.size());
        for (int i = 0; i < size; i++) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("key_frame"));
            map.put(ops.createString("value"), keyFrames.get(i).serialize(ops));
            list.add(ops.createMap(map));
            if (i < size - 1 && interpolators.containsKey(i)) {
                map.put(ops.createString("type"), ops.createString("interpolation"));
                map.put(ops.createString("value"), interpolators.get(i).serialize(ops));
                list.add(ops.createMap(map));
            }
        }
        return ops.createList(list.stream());
    }
}
