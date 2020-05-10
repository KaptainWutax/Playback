package kaptainwutax.playback.replay.render.interpolation;

public class LinearAngleInterpolator extends LinearInterpolator {
    public static LinearAngleInterpolator INSTANCE = new LinearAngleInterpolator();

    @Override
    public boolean canInterpolate(ComponentKey<?> property) {
        return property == ComponentKey.YAW || property == ComponentKey.PITCH || property == ComponentKey.ROLL;
    }

    @Override
    public Double interpolate(Double a, Double b, float t) {
        double shortest = ((((b - a) % 360) + 540) % 360) - 180;
        return a + (shortest * t) % 360;
    }
}
