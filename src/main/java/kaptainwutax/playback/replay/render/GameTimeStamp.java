package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;

import com.mojang.serialization.DynamicOps;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameTimeStamp implements Comparable<GameTimeStamp>{
    public final long tick;
    public final float tickDelta;

    public GameTimeStamp(Dynamic<?> d) {
        this.tick = d.get("tick").asLong(0);
        this.tickDelta = d.get("tickDelta").asFloat(0);
    }

    public GameTimeStamp(long tick, float tickDelta) {
        this.tick = tick;
        this.tickDelta = tickDelta;
    }

    public GameTimeStamp(double time) {
        this.tick = (long) time;
        this.tickDelta = (float) (time - this.tick);
    }

    public boolean isBefore(GameTimeStamp other) {
        return other == null ? false : this.compareTo(other) < 0;
    }
    public boolean isAfter(GameTimeStamp other) {
        return other == null ? false : this.compareTo(other) > 0;
    }

    public int compareTo(GameTimeStamp other) {
        return compareTo(this.tick, this.tickDelta, other.tick, other.tickDelta);
    }

    public double asDouble() {
        return (double)this.tick + this.tickDelta;
    }

    /**
     * @return 1 if this is later, -1 if this is earlier, 0 if same time
     */
    public int compareTo(long tick, float tickDelta) {
        return compareTo(this.tick, this.tickDelta, tick, tickDelta);
    }

    /**
     * @param tick1 first timestamp
     * @param tickDelta1 first timestamp
     * @param tick2 second timestamp
     * @param tickDelta2 second timestamp
     * @return 1 if first time stamp is later, 0 if equal, -1 if first time stamp is earlier
     */
    public static int compareTo(long tick1, float tickDelta1, long tick2, float tickDelta2) {
        if (tick1 > tick2) return 1;
        if (tick1 < tick2) return -1;
        return Float.compare(tickDelta1, tickDelta2);
    }

    public <T> T serialize(DynamicOps<T> ops) {
        Map<T, T> map = new LinkedHashMap<>();
        map.put(ops.createString("tick"), ops.createLong(tick));
        map.put(ops.createString("tickDelta"), ops.createFloat(tickDelta));
        return ops.createMap(map);
    }
}
