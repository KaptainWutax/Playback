package kaptainwutax.playback.render;

import kaptainwutax.playback.render.util.Color;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

public class Cube extends Cuboid {

    public Cube(BlockPos pos) {
        this(pos, Color.WHITE);
    }

    public Cube(BlockPos pos, Color color) {
        super(Renderer.toVec3d(pos), new Vector3f(1.0F, 1.0F, 1.0F), color);
    }

}
