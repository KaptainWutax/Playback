package kaptainwutax.playback.replay.render;

import kaptainwutax.playback.replay.render.interpolation.ComponentKey;
import kaptainwutax.playback.replay.render.interpolation.HierarchyInterpolator;
import kaptainwutax.playback.replay.render.interpolation.Interpolator;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameCameraPath implements CameraPath {
    private static final Interpolator defaultInterpolator = new HierarchyInterpolator();

    private List<KeyFrame> keyFrames = new ArrayList<>();

    public KeyFrameCameraPath() {

    }
    public KeyFrameCameraPath add(KeyFrame kf) {
        keyFrames.add(kf);
        return this;
    }

    @Override
    public CameraState getCameraStateAtTime(long tick, float tickDelta) {
        int i = this.getLowerIndexForTimestamp(tick, tickDelta);
        if (i < 0) return null;

        KeyFrame prev = this.keyFrames.get(i);
        if (i + 1 >= this.keyFrames.size()) {
            return prev;
        }
        KeyFrame next = this.keyFrames.get(i+1);
        CameraState.Mutable state = new CameraState.Mutable();
        float delta = calculateDelta(prev.tick, prev.tickDelta, tick, tickDelta) / calculateDelta(prev, next);
        defaultInterpolator.interpolate(ComponentKey.KEY_FRAME, keyFrames, i, i + 1, delta, state);
        return state;
    }

    private static float calculateDelta(KeyFrame a, KeyFrame b) {
        return calculateDelta(a.tick, a.tickDelta, b.tick, b.tickDelta);
    }

    private static float calculateDelta(long tickA, float tickDeltaA, long tickB, float tickDeltaB) {
        long ticks = tickB - tickA;
        float tickDeltas = tickDeltaB - tickDeltaA;
        return ticks + tickDeltas;
    }

    @Override
    public GameTimeStamp getStartTime() {
        return this.keyFrames.isEmpty() ? null : this.keyFrames.get(0).getTimeStamp();
    }

    @Override
    public GameTimeStamp getEndTime() {
        return this.keyFrames.isEmpty() ? null : this.keyFrames.get(this.keyFrames.size()-1).getTimeStamp();
    }

    public List<KeyFrame> getKeyFrames() {
        return keyFrames;
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
