package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class DebugAction implements IAction {

	public static final Map<String, Function<ClientPlayerEntity, ?>> DEBUGS = new LinkedHashMap<>();

	static {
		DEBUGS.put("Player Id", Entity::getEntityId);
		DEBUGS.put("Position", Entity::getPos);
		DEBUGS.put("Rotation", Entity::getRotationVector);
		DEBUGS.put("Velocity", Entity::getVelocity);
		DEBUGS.put("Pose", Entity::getPose);
		DEBUGS.put("Entity list size", player -> player.clientWorld.getRegularEntityCount());
		DEBUGS.put("Player list size", player -> player.clientWorld.getPlayers().size());
	}

	protected Map<String, Object> values = new HashMap<>();

	public DebugAction() {
		DEBUGS.forEach((name, debug) -> this.values.put(name, debug.apply(MinecraftClient.getInstance().player)));
	}

	@Override
	public void play() {
		boolean everythingMatches = true;

		Formatter formatter = new Formatter();
		String header = "==============================[Tick " + Playback.tickCounter + "]==============================\n";
		formatter.format(header);

		for(Map.Entry<String, Function<ClientPlayerEntity, ?>> e : DEBUGS.entrySet()) {
			String name = e.getKey();
			Function<ClientPlayerEntity, ?> debug = e.getValue();

			//replayPlayer could be more future proof? Doesn't matter for now since it's the same player instance.
			Object actualValue = debug.apply(MinecraftClient.getInstance().player);
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
