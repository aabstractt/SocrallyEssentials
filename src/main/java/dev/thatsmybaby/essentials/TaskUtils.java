package dev.thatsmybaby.essentials;

import cn.nukkit.Server;

@SuppressWarnings("deprecation")
public class TaskUtils {

    public static void runAsync(Runnable runnable) {
        if (Server.getInstance().isPrimaryThread()) {
            Server.getInstance().getScheduler().scheduleTask(runnable, true);
        } else {
            runnable.run();
        }
    }

    public static void runSync(Runnable runnable) {
        if (Server.getInstance().isPrimaryThread()) {
            runnable.run();
        } else {
            Server.getInstance().getScheduler().scheduleTask(runnable);
        }
    }

    public static void runLater(Runnable runnable, int delay) {
        Server.getInstance().getScheduler().scheduleDelayedTask(runnable, delay);
    }
}