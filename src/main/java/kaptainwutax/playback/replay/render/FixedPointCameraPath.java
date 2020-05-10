package kaptainwutax.playback.replay.render;

public class FixedPointCameraPath implements CameraPath {
    private final KeyFrame keyFrame;
    private final GameTimeStamp startTime;
    private final GameTimeStamp endTime;

    public FixedPointCameraPath(KeyFrame keyFrame, GameTimeStamp startTime, GameTimeStamp endTime) {
        this.keyFrame = keyFrame;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public CameraState getCameraStateAtTime(long tick, float tickDelta) {
        return keyFrame;
    }

    @Override
    public GameTimeStamp getStartTime() {
        return this.startTime;
    }

    @Override
    public GameTimeStamp getEndTime() {
        return this.endTime;
    }
}
