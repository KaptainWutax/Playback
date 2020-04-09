package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameCameraPath implements CameraPath {

    private List<KeyFrame> keyFrames = new ArrayList<>();

    public KeyFrameCameraPath() {

    }
    public KeyFrameCameraPath add(KeyFrame kf) {
        keyFrames.add(kf);
        return this;
    }

    @Override
    public Vec3d getCameraPositionAtTime(long tick, float tickDelta) {
        //maybe do some caching or at least binary search in the array list

        int i = this.getLowerIndexForTimestamp(tick, tickDelta);
        if (i < 0) return null;

        KeyFrame prev = this.keyFrames.get(i);
        KeyFrame next;
        if (i+1 < this.keyFrames.size()) {
            next = this.keyFrames.get(i+1);
        } else {
            return prev.getPositionVec();
        }
        //Linear interpolation
        double delta = ((tick + (double)tickDelta) - prev.tick - prev.tickDelta) /
                (next.tick + (double)next.tickDelta - prev.tick - prev.tickDelta);
        return new Vec3d(MathHelper.lerp(delta, prev.x, next.x),
                MathHelper.lerp(delta, prev.y, next.y),
                MathHelper.lerp(delta, prev.z, next.z));
    }

    @Override
    public Vector3f getCameraRotationAtTime(long tick, float tickDelta) {
        //maybe do some caching or at least binary search in the array list

        int i = this.getLowerIndexForTimestamp(tick, tickDelta);
        if (i < 0) return null;

        KeyFrame prev = this.keyFrames.get(i);
        KeyFrame next;
        if (i+1 < this.keyFrames.size()) {
            next = this.keyFrames.get(i+1);
        } else {
            return prev.getRotationVec();
        }
        //Linear interpolation
        float delta = ((tick + (float)tickDelta) - prev.tick - prev.tickDelta) /
                (next.tick + (float)next.tickDelta - prev.tick - prev.tickDelta);
        return new Vector3f(MathHelper.lerp(delta, prev.roll, next.roll),
                MathHelper.lerp(delta, prev.pitch, next.pitch),
                MathHelper.lerp(delta, prev.yaw, next.yaw));
    }

    @Override
    public GameTimeStamp getStartTime() {
        return this.keyFrames.isEmpty() ? null : this.keyFrames.get(0).getTimeStamp();
    }

    @Override
    public GameTimeStamp getEndTime() {
        return this.keyFrames.isEmpty() ? null : this.keyFrames.get(this.keyFrames.size()-1).getTimeStamp();
    }

    private int getLowerIndexForTimestamp(long tick, float tickDelta) {
        //naive implementation
        for (int i = 0; i < this.keyFrames.size(); i++) {
            KeyFrame keyFrame = this.keyFrames.get(i);
            int cmp;
            if ((cmp = GameTimeStamp.compareTo(keyFrame.tick, keyFrame.tickDelta, tick, tickDelta)) != -1) {
                //i-1 if we passed the timestamp, if we are the timestamp, return without -1
                return i - cmp;
            }
        }
        return -1;
    }
}
