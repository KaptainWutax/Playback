package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameCameraPath implements CameraPath {

    private List<KeyFrame> keyFrames = new ArrayList<>();
    int currentKeyFrameIndex;

    @Override
    public Vec3d getCameraPositionAtTime(long tick, float tickDelta) {
        return null;
    }

    @Override
    public Vector3f getCameraRotationAtTime(long tick, float tickDelta) {
        return null;
    }

    @Override
    public GameTimeStamp getStartTime() {
        return null;
    }

    @Override
    public GameTimeStamp getEndTime() {
        return null;
    }

    public KeyFrame getStartKeyFrame() {
        return keyFrames.isEmpty() ? null : keyFrames.get(0);
    }

    public KeyFrame getEndKeyFrame() {
        return keyFrames.isEmpty() ? null : keyFrames.get(keyFrames.size() - 1);
    }
}
