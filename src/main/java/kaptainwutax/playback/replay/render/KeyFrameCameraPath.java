package kaptainwutax.playback.replay.render;

import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameCameraPath implements CameraPath {

    List<KeyFrame> keyFrames = new ArrayList<>();
    int currentKeyFrameIndex;




    public static class KeyFrame {
        Vec3d position;
        Quaternion cameraRotation;
        long tickTimeOffset;
        float tickDeltaOffset;

        public KeyFrame(Vec3d position, Quaternion cameraRotation) {
            this.position = position;
            this.cameraRotation = cameraRotation.copy();
        }
    }

    public KeyFrame getStartKeyFrame() {
        return keyFrames.isEmpty() ? null : keyFrames.get(0);
    }

    public KeyFrame getEndKeyFrame() {
        return keyFrames.isEmpty() ? null : keyFrames.get(keyFrames.size() - 1);
    }
}
