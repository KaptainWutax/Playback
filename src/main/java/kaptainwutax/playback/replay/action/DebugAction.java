package kaptainwutax.playback.replay.action;

import com.sun.javafx.geom.Vec2f;
import kaptainwutax.playback.Playback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class DebugAction extends Action {

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
	private static Vec2f getRotation(Entity entity) {
		return new Vec2f(entity.yaw, entity.pitch);
	}

	private static final Vec2f NAN_VECTOR = new Vec2f(Float.NaN,Float.NaN);
	private static Vec2f getVehicleRotation(PlayerEntity entity) {
		return entity.getVehicle() == null ? NAN_VECTOR : getRotation(entity.getVehicle());
	}

	protected Map<String, Object> values = new HashMap<>();

	public DebugAction() {
		DEBUGS.forEach((name, debug) -> this.values.put(name, debug.apply(client.player)));
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

			if(actualValue.equals(expectedValue)) {
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

}
