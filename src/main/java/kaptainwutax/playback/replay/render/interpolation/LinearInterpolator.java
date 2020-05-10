package kaptainwutax.playback.replay.render.interpolation;

import net.minecraft.util.math.MathHelper;

public class LinearInterpolator extends SimpleInterpolator<Double> {
    public static final LinearInterpolator INSTANCE = new LinearInterpolator();

    @Override
    public boolean canInterpolate(ComponentKey<?> property) {
        return property.getType() == Double.class;
    }

    @Override
    public Double interpolate(Double a, Double b, float t) {
        return MathHelper.lerp(t, a, b);
    }
}
