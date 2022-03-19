package dev.thatsmybaby.essentials;

import cn.nukkit.Server;

@SuppressWarnings("deprecation")
public class TaskUtils {

    public static void runAsync(Runnable runnable) {
        if (isPrimaryThread()) {
            Server.getInstance().getScheduler().scheduleTask(runnable, true);

            return;
        }

        runnable.run();
    }

    public static void runSync(Runnable runnable) {
        if (isPrimaryThread()) {
            runnable.run();
        } else {
            Server.getInstance().getScheduler().scheduleTask(runnable);
        }
    }

    public static void runLater(Runnable runnable, int delay) {
        Server.getInstance().getScheduler().scheduleDelayedTask(runnable, delay);
    }

    protected static boolean isPrimaryThread() {
        return Server.getInstance().isPrimaryThread();
    }
}