package sign.editor;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SignEditor implements ModInitializer {

	public static boolean autoSign = false;
	public static boolean noRender = false;
	public static boolean CustomMode = false;
	public static boolean BackMode = false;
	public static boolean MAXMODE = false;
	public static int signLength = 80;

	public static String customText1 = "";
	public static String customText2 = "";
	public static String customText3 = "";
	public static String customText4 = "";

	public static Minecraft mc;

	private static final Queue<Runnable> TICK_TASKS = new ArrayDeque<>();
	private static final List<DelayedTask> DELAYED_TASKS = new ArrayList<>();

	private static long currentTick = 0;


	@Override
	public void onInitialize() {
		System.out.println("INITIALIZED SIGN EDITOR"); // If not firing, then smth happened
		mc = Minecraft.getInstance();

		init(); // Start the ticker
	}

	private static class DelayedTask {
		private final long targetTick;
		private final Runnable task;

		private DelayedTask(long targetTick, Runnable task) {
			this.targetTick = targetTick;
			this.task = task;
		}
	}

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(_ -> {
			currentTick++;

			int tasksThisTick = TICK_TASKS.size();
			for (int i = 0; i < tasksThisTick; i++) {
				Runnable task = TICK_TASKS.poll();
				if (task != null) {
					task.run();
				}
			}

			List<Runnable> dueTasks = new ArrayList<>();
			Iterator<DelayedTask> iterator = DELAYED_TASKS.iterator();

			while (iterator.hasNext()) {
				DelayedTask delayedTask = iterator.next();
				if (delayedTask.targetTick <= currentTick) {
					dueTasks.add(delayedTask.task);
					iterator.remove();
				}
			}

			for (Runnable task : dueTasks) {
				task.run();
			}
		});
	}

	public static void scheduleNextTick(Runnable task) {
		TICK_TASKS.add(task);
	}
}
