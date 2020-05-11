package kaptainwutax.playback.replay.render.interpolation;

import java.util.function.DoubleUnaryOperator;

public class Polynomial implements DoubleUnaryOperator {
    private final double[] coefficients;

    public Polynomial(double ...coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public double applyAsDouble(double operand) {
        double result = 0;
        for (int i = coefficients.length - 1; i >= 0; i--) {
            result = operand * result + coefficients[i];
        }
        return result;
    }
}
