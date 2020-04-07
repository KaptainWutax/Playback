package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

public interface CameraPath {

    Vec3d getCameraPositionAtTime(long tick, float tickDelta);
    Vector3f getCameraRotationAtTime(long tick, float tickDelta);
    GameTimeStamp getStartTime();
    GameTimeStamp getEndTime();

    class GameTimeStamp {
        final long tick;
        final float tickDelta;
        public GameTimeStamp(long tick, float tickDelta) {
            this.tick = tick;
            this.tickDelta = tickDelta;
        }

        /**
         * @return 1 if this is later, -1 if this is earlier, 0 if same time
         */
        public int compareTo(long tick, float tickDelta) {
            if (tick < this.tick) return 1;
            if (tick > this.tick) return -1;
            if (tickDelta < this.tickDelta) return 1;
            if (tickDelta > this.tickDelta) return -1;
            return 0;
        }
    }
}
