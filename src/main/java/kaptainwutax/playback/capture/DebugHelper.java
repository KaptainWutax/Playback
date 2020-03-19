package kaptainwutax.playback.capture;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.action.DebugPositionAction;
import kaptainwutax.playback.capture.action.DebugRotationAction;
import kaptainwutax.playback.capture.action.DebugVelocityAction;
import kaptainwutax.playback.capture.action.IAction;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class DebugHelper {

	public static Entity trackedEntity;
	public static List<Vec3d> args = new ArrayList<>();
	public static List<Exception> stackTraces = new ArrayList<>();

	public static int index = 0;
	public static int maxIndex = -1;

	public static int counterMixinInvoke;

	public static void trackEntity(Entity entity) {
		trackedEntity = entity;
	}

	public static void registerEvent(Entity entity, double x, double y, double z) {
		if (entity == trackedEntity) {
			Exception myExc = new Exception();
			if (Playback.recording.isRecording()) {
				stackTraces.add(myExc);
				args.add(new Vec3d(x,y,z));

				if (index != 0) {
					maxIndex = -1;
					index = 0;
					args.clear();
					stackTraces.clear();
				}

				maxIndex++;
			}

			if (Playback.isReplaying && index < maxIndex ){
				if (!new Vec3d(x,y,z).equals(args.get(index)))
					System.out.println("Different setPos args!");
				else
					System.out.println("setpos args good");

//				if (!Arrays.equals(myExc.getStackTrace(), stackTraces.get(index).getStackTrace())){ //for some reason this gets an out of bounds
//					System.out.println("Different setPos stacktrace!");
//				}
				index++;

			}
		}
	}

	public static void clearStackTraces(Entity entity) {
		if (entity == trackedEntity) {
			stackTraces.clear();
		}
	}

	public static void printStackTraces(Entity entity){
		if (entity == trackedEntity) {
			stackTraces.forEach(Throwable::printStackTrace);
		}
	}

	public static void restartReplaying(){
		index = 0;
		DebugPositionAction.restart();
		DebugRotationAction.restart();
		DebugVelocityAction.restart();
	}

}
