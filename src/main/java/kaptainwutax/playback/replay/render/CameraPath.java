package kaptainwutax.playback.replay.render;

public interface CameraPath {
    CameraState getCameraStateAtTime(long tick, float tickDelta);

    GameTimeStamp getStartTime();
    GameTimeStamp getEndTime();
}
