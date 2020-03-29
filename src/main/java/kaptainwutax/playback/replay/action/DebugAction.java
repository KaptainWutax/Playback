package kaptainwutax.playback.replay.action;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kaptainwutax.playback.Playback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class DebugAction extends Action {
	private static final Gson GSON = new Gson();
	private static final java.lang.reflect.Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
	public static final Map<String, Function<ClientPlayerEntity, ?>> DEBUGS = new LinkedHashMap<>();

	static {
		DEBUGS.put("Player Id", Entity::getEntityId);
		DEBUGS.put("Position", Entity::getPos);
		DEBUGS.put("Rotation", DebugAction::getRotation);
		DEBUGS.put("Velocity", Entity::getVelocity);
		DEBUGS.put("Pose", Entity::getPose);
		DEBUGS.put("Entity list size", player -> player.clientWorld.getRegularEntityCount());
		DEBUGS.put("Player list size", player -> player.clientWorld.getPlayers().size());
		DEBUGS.put("Vehicle Rotation", DebugAction::getVehicleRotation);
	}

	//NOT USING MOJANGS Vec2f THAT DOESN'T EVEN OVERRIDE equals
	private static Vec3d getRotation(Entity entity) {
		return new Vec3d(entity.yaw, entity.pitch, 0);
	}

	private static final Vec3d NAN_VECTOR = new Vec3d(Float.NaN,Float.NaN, 0);
	private static Vec3d getVehicleRotation(PlayerEntity entity) {
		return entity.getVehicle() == null ? NAN_VECTOR : getRotation(entity.getVehicle());
	}

	protected Map<String, Object> values = new HashMap<>();

	public DebugAction() {}

	public DebugAction(ClientPlayerEntity player) {
		DEBUGS.forEach((name, debug) -> this.values.put(name, debug.apply(player)));
	}

	@Override
	public void play() {
		boolean everythingMatches = true;

		Formatter formatter = new Formatter();
		String header = "==============================[Tick " + Playback.tickCounter + "]==============================";
		formatter.format(header + "\n");

		for(Map.Entry<String, Function<ClientPlayerEntity, ?>> e : DEBUGS.entrySet()) {
			String name = e.getKey();
			Function<ClientPlayerEntity, ?> debug = e.getValue();

			//replayPlayer could be more future proof? Doesn't matter for now since it's the same player instance.
			Object actualValue = debug.apply(client.player);
			Object expectedValue = this.values.get(name);

			if(actualValue.equals(expectedValue) || expectedValue == null) { // hack for not serializing
				formatter.format("[Tick %d] %s is matching.\n", Playback.tickCounter, name);
			} else {
				formatter.format("[Tick %d] %s doesn't match! Is %s but should be %s.\n", Playback.tickCounter, name, actualValue, expectedValue);
				everythingMatches = false;
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
		values.clear();
		// values.putAll(GSON.fromJson(buf.readString(), MAP_TYPE));
	}

	@Override
	public void write(PacketByteBuf buf) {
		// buf.writeString(GSON.toJson(values, MAP_TYPE));
	}

}
