package kaptainwutax.playback.util;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SerializationUtil {
    public static <K, V> void writeMap(PacketByteBuf buf, Map<K, V> map, BiConsumer<PacketByteBuf, K> writeKey, BiConsumer<PacketByteBuf, V> writeValue) {
        Set<Map.Entry<K, V>> entries = map.entrySet();
        buf.writeInt(entries.size());
        for (Map.Entry<K, V> entry : entries) {
            writeKey.accept(buf, entry.getKey());
            writeValue.accept(buf, entry.getValue());
        }
    }

    public static <K, V> Map<K, V> readMap(PacketByteBuf buf, Function<PacketByteBuf, K> readKey, Function<PacketByteBuf, V> readValue) {
        int len = buf.readVarInt();
        Map<K, V> map = new LinkedHashMap<>(len);
        for (int i = 0; i < len; i++) {
            map.put(readKey.apply(buf), readValue.apply(buf));
        }
        return map;
    }

    public static Vec3d readVec3d(PacketByteBuf buf) {
        return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void writeVec3d(PacketByteBuf buf, Vec3d v) {
        buf.writeDouble(v.x);
        buf.writeDouble(v.y);
        buf.writeDouble(v.z);
    }
}
