package kaptainwutax.playback.replay.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class CameraState {
    protected double x, y, z;
    protected double yaw, pitch, roll;

    public CameraState() {}

    public CameraState(double x, double y, double z, double yaw, double pitch, double roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public CameraState(CameraState state) {
        this(state.x, state.y, state.z, state.yaw, state.pitch, state.roll);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3d getPosition() {
        return new Vec3d(x, y, z);
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public Vec3d getRotation() {
        return new Vec3d(yaw, pitch, roll);
    }

    public Quaternion getRotationQuaternion() {
        Quaternion rotation = new Quaternion(0, 0, 0, 1);
        rotation.hamiltonProduct(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) -yaw));
        rotation.hamiltonProduct(Vector3f.POSITIVE_X.getDegreesQuaternion((float) pitch));
        rotation.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion((float) roll));
        return rotation;
    }

    public static class Mutable extends CameraState {
        public Mutable() {}

        public Mutable(double x, double y, double z, double yaw, double pitch, double roll) {
            super(x, y, z, yaw, pitch, roll);
        }

        public Mutable(CameraState state) {
            super(state);
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public void setPosition(Vec3d pos) {
            this.x = pos.x;
            this.y = pos.y;
            this.z = pos.z;
        }

        public void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public void setPitch(double pitch) {
            this.pitch = pitch;
        }

        public void setRoll(double roll) {
            this.roll = roll;
        }

        public void setRotation(Vec3d rot) {
            this.yaw = rot.x;
            this.pitch = rot.y;
            this.roll = rot.z;
        }

        public void set(CameraState state) {
            this.x = state.x;
            this.y = state.y;
            this.z = state.z;
            this.yaw = state.yaw;
            this.pitch = state.pitch;
            this.roll = state.roll;
        }
    }

    public static Vec3d toEuler(Quaternion q) {
        float w = q.getA();
        float x = q.getB();
        float y = q.getC();
        float z = q.getD();

        double pitch = Math.atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y));
        double yaw = Math.asin(MathHelper.clamp(2 * (w * y - z * x), -1, 1));
        double roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));
        return new Vec3d(-Math.toDegrees(yaw), Math.toDegrees(pitch), Math.toDegrees(roll));
    }
}
