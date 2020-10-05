package kaptainwutax.playback.replay.render;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CameraState {
    protected GameTimeStamp time;
    protected double x, y, z;
    protected double yaw, pitch, roll;
    protected double fov;

    public CameraState() {}

    public CameraState(Dynamic<?> config) {
        Optional<List<Double>> posOpt = config.get("position").asListOpt(d -> d.asDouble(0)).result();
        if (posOpt.isPresent() && posOpt.get().size() == 3) {
            List<Double> pos = posOpt.get();
            this.x = pos.get(0);
            this.y = pos.get(1);
            this.z = pos.get(2);
        }
        Optional<List<Double>> rotOpt = config.get("rotation").asListOpt(d -> d.asDouble(0)).result();
        if (rotOpt.isPresent() && rotOpt.get().size() == 3) {
            List<Double> rot = rotOpt.get();
            this.yaw = rot.get(0);
            this.pitch = rot.get(1);
            this.roll = rot.get(2);
        }
        this.fov = config.get("fov").asDouble(90);
        this.time = new GameTimeStamp(config);
    }

    public CameraState(GameTimeStamp time, double x, double y, double z, double yaw, double pitch, double roll, double fov) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.fov = fov;
    }

    public CameraState(CameraState state) {
        this(state.time, state.x, state.y, state.z, state.yaw, state.pitch, state.roll, state.fov);
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

    public double getFov() {
        return fov;
    }

    public GameTimeStamp getTime() {
        return time;
    }

    public double getTimeAsDouble() {
        return time.asDouble();
    }

    public <T> T serialize(DynamicOps<T> ops) {
        Map<T, T> map = new LinkedHashMap<>();
        map.put(ops.createString("position"), ops.createList(Stream.of(
                ops.createDouble(x), ops.createDouble(y), ops.createDouble(z)
        )));
        map.put(ops.createString("rotation"), ops.createList(Stream.of(
                ops.createDouble(yaw), ops.createDouble(pitch), ops.createDouble(roll)
        )));
        map.put(ops.createString("fov"), ops.createDouble(fov));
        return ops.mergeInto(ops.createMap(map), time.serialize(ops));
    }

    public static class Mutable extends CameraState {
        public Mutable() {}

        public Mutable(GameTimeStamp time, double x, double y, double z, double yaw, double pitch, double roll, double fov) {
            super(time, x, y, z, yaw, pitch, roll, fov);
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

        public void setFov(double fov) {
            this.fov = fov;
        }

        public void setTime(GameTimeStamp time) {
            this.time = time;
        }

        public void setTime(double time) {
            this.time = new GameTimeStamp(time);
        }

        public void set(CameraState state) {
            this.x = state.x;
            this.y = state.y;
            this.z = state.z;
            this.yaw = state.yaw;
            this.pitch = state.pitch;
            this.roll = state.roll;
            this.fov = state.fov;
        }
    }

    public static Vec3d toEuler(Quaternion q) {
        float w = q.getW();
        float x = q.getX();
        float y = q.getY();
        float z = q.getZ();

        double pitch = Math.atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y));
        double yaw = Math.asin(MathHelper.clamp(2 * (w * y - z * x), -1, 1));
        double roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));
        return new Vec3d(-Math.toDegrees(yaw), Math.toDegrees(pitch), Math.toDegrees(roll));
    }
}
