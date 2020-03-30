package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.util.PlaybackSerializable;
import kaptainwutax.playback.util.SerializationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Formatter;

public class DebugAction extends Action {
	private Debugs values;

	public DebugAction() {
		values = new Debugs();
	}

	public DebugAction(ClientPlayerEntity player) {
		values = new Debugs(player);
	}

	@Override
	public void play() {
		boolean everythingMatches = true;

		Formatter formatter = new Formatter();
		String header = "==============================[Tick " + Playback.tickCounter + "]==============================";
		formatter.format(header + "\n");

		Debugs actual = new Debugs(client.player);
		Field[] fields = Debugs.class.getFields();
		for (Field field : fields) {
			if (field.getModifiers() != Modifier.PUBLIC) continue;
			try {
				Object actualValue = field.get(actual);
				Object expectedValue = field.get(values);

				if(actualValue.equals(expectedValue)) {
					formatter.format("[Tick %d] %s is matching.\n", Playback.tickCounter, field.getName());
				} else {
					formatter.format("[Tick %d] %s doesn't match! Is %s but should be %s.\n", Playback.tickCounter, field.getName(), actualValue, expectedValue);
					everythingMatches = false;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if(!everythingMatches) {
			for(int i = 0; i < header.length(); i++) {
				formatter.format("=");
			}

			formatter.format("\n");
			System.out.format(formatter.toString());
		}
	}

	@Override
	public Type getType() {
		return Type.DEBUG;
	}

	@Override
	public void read(PacketByteBuf buf) {
		values.read(buf);
	}

	@Override
	public void write(PacketByteBuf buf) {
		values.write(buf);
	}

	private static class Debugs implements PlaybackSerializable {
		public int playerId;
		public Vec3d position;
		public Vec3d rotation;
		public Vec3d velocity;
		public EntityPose pose;
		public int entityListSize;
		public int playerListSize;
		public Vec3d vehicleRotation;

		public Debugs() {}

		public Debugs(ClientPlayerEntity player) {
			load(player);
		}

		public void load(ClientPlayerEntity player) {
			playerId = player.getEntityId();
			position = player.getPos();
			rotation = getRotation(player);
			velocity = player.getVelocity();
			pose = player.getPose();
			entityListSize = player.clientWorld.getRegularEntityCount();
			playerListSize = player.clientWorld.getPlayers().size();
			vehicleRotation = getVehicleRotation(player);
		}

		@Override
		public void read(PacketByteBuf buf) {
			playerId = buf.readVarInt();
			position = SerializationUtil.readVec3d(buf);
			rotation = SerializationUtil.readVec3d(buf);
			velocity = SerializationUtil.readVec3d(buf);
			pose = EntityPose.values()[buf.readVarInt()];
			entityListSize = buf.readVarInt();
			playerListSize = buf.readVarInt();
			vehicleRotation = SerializationUtil.readVec3d(buf);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeVarInt(playerId);
			SerializationUtil.writeVec3d(buf, position);
			SerializationUtil.writeVec3d(buf, rotation);
			SerializationUtil.writeVec3d(buf, velocity);
			buf.writeVarInt(pose.ordinal());
			buf.writeVarInt(entityListSize);
			buf.writeVarInt(playerListSize);
			SerializationUtil.writeVec3d(buf, vehicleRotation);
		}


		private static Vec3d getRotation(Entity entity) {
			return new Vec3d(entity.yaw, entity.pitch, 0);
		}

		private static final Vec3d NAN_VECTOR = new Vec3d(Float.NaN,Float.NaN, 0);
		private static Vec3d getVehicleRotation(PlayerEntity entity) {
			return entity.getVehicle() == null ? NAN_VECTOR : getRotation(entity.getVehicle());
		}

	}
}
