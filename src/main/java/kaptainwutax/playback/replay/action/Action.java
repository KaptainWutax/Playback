package kaptainwutax.playback.replay.action;

import io.netty.buffer.Unpooled;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

public abstract class Action implements PlaybackSerializable {
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	protected static MinecraftClient client = MinecraftClient.getInstance();

	private boolean isFramePerfect;
	private float tickDelta;

	public Action(boolean isFramePerfect) {
		this.isFramePerfect = isFramePerfect;

		if(this.isFramePerfect) {
			this.tickDelta = client.getTickDelta();
		}
	}

	public float getTickDelta() {
		return tickDelta;
	}

	public abstract void play();

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		if(this.isFramePerfect) {
			buf.writeFloat(this.tickDelta);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		if(this.isFramePerfect) {
			this.tickDelta = buf.readFloat();
		}
	}

	public static void writeAction(PacketByteBuf buf, Action action) throws IOException {
		buf.writeVarInt(Type.of(action).ordinal());
		PacketByteBuf tmp = new PacketByteBuf(Unpooled.buffer());
		action.write(tmp);
		buf.writeVarInt(tmp.writerIndex());
		buf.writeBytes(tmp);
		tmp.release();
	}

	public static Action readAction(PacketByteBuf buf) throws IOException {
		Action action = Type.values[buf.readVarInt()].instantiate();
		int size = buf.readVarInt();
		PacketByteBuf actionBuf = new PacketByteBuf(buf.slice(buf.readerIndex(), size));
		action.read(actionBuf);
		buf.readerIndex(buf.readerIndex() + size);
		return action;
	}

	public enum Type {
		DEBUG(DebugAction.class),
		PACKET(PacketAction.class),
		KEY(KeyAction.class),
		MOUSE(MouseAction.class),
		WINDOW_FOCUS(WindowFocusAction.class),
		WINDOW_SIZE(WindowSizeAction.class),
		CLIPBOARD_READ(ClipboardReadAction.class),
		LOST_FOCUS_PAUSE_SCREEN(LostFocusPauseScreenAction.class),
		PAUSED_STATE(SetPausedAction.class);

		static final Type[] values = values();
		private static final Map<Class<?>, Type> classToTypeMap = new HashMap<>();

		static {
			for (Type t : values) classToTypeMap.put(t.cls, t);
		}

		public final Class<? extends Action> cls;
		private final MethodHandle constr;
		Type(Class<? extends Action> cls) {
			this.cls = cls;
			try {
				this.constr = LOOKUP.findConstructor(cls, MethodType.methodType(void.class));
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Default action constructor does not exist", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Default action constructor is not accessible", e);
			}
		}

		public Action instantiate() {
			try {
				return (Action) constr.invoke();
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}

		public static Type of(Action action) {
			Type t = classToTypeMap.get(action.getClass());
			if (t == null) throw new IllegalStateException("Invalid action " + action + " has no corresponding type");
			return t;
		}
	}
}
