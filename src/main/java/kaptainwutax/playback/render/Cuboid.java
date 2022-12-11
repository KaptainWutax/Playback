package kaptainwutax.playback.render;

import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class Cuboid implements Renderer {

    public Vec3d start;
    public Vector3f size;

    private Line[] edges = new Line[12];

    public Cuboid(Vec3d pos) {
        this(pos, new Vector3f(1, 1, 1), Color.WHITE);
    }

    public Cuboid(Vec3d start, Vec3d end, Color color) {
        this(start, new Vector3f((float)(end.x - start.x), (float)(end.y - start.y), (float)(end.z - start.z)), color);
    }

    public Cuboid(Box box, Color color) {
        this(new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ), color);
    }

    public Cuboid(Vec3d start, Vector3f size, Color color) {
        this.start = start;
        this.size = size;
        this.edges[0] = new Line(this.start, this.start.add(this.size.x, 0, 0), color);
        this.edges[1] = new Line(this.start, this.start.add(0, this.size.y, 0), color);
        this.edges[2] = new Line(this.start, this.start.add(0, 0, this.size.z), color);
        this.edges[3] = new Line(this.start.add(this.size.x, 0, this.size.z), this.start.add(this.size.x, 0, 0), color);
        this.edges[4] = new Line(this.start.add(this.size.x, 0, this.size.z), this.start.add(this.size.x, this.size.y, this.size.z), color);
        this.edges[5] = new Line(this.start.add(this.size.x, 0, this.size.z), this.start.add(0, 0, this.size.z), color);
        this.edges[6] = new Line(this.start.add(this.size.x, this.size.y, 0), this.start.add(this.size.x, 0, 0), color);
        this.edges[7] = new Line(this.start.add(this.size.x, this.size.y, 0), this.start.add(0, this.size.y, 0), color);
        this.edges[8] = new Line(this.start.add(this.size.x, this.size.y, 0), this.start.add(this.size.x, this.size.y, this.size.z), color);
        this.edges[9] = new Line(this.start.add(0, this.size.y, this.size.z), this.start.add(0, 0, this.size.z), color);
        this.edges[10] = new Line(this.start.add(0, this.size.y, this.size.z), this.start.add(0, this.size.y, 0), color);
        this.edges[11] = new Line(this.start.add(0, this.size.y, this.size.z), this.start.add(this.size.x, this.size.y, this.size.z), color);
    }

    @Override
    public void render(float tickDelta, MatrixStack matrices) {
        if(this.start == null || this.size == null || this.edges == null)return;

        for(Line edge: this.edges) {
            if(edge == null)continue;
            edge.render(tickDelta, matrices);
        }
    }

    @Override
    public BlockPos getCenter() {
        Vec3d center = this.start.add(this.size.x / 2, this.size.y / 2, this.size.z / 2);
        return new BlockPos(center.x, center.y, center.z);
    }

}
