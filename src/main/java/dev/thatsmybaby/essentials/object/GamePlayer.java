package dev.thatsmybaby.essentials.object;

import cn.nukkit.Player;
import cn.nukkit.scheduler.TaskHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor @Getter
public final class GamePlayer {

    private static final Map<String, GamePlayer> playerMap = new HashMap<>();

    private final String xuid;
    private final String name;

    private final Map<String, TaskHandler> runnableMap = new HashMap<>();
    private final Map<String, CrossServerLocation> crossServerLocationMap;

    @Setter private int maxHomeSize;

    @Setter private boolean acceptingTpaRequests;
    @Setter private boolean alreadyTeleporting;

    private String lastTpaRequest;
    private final List<String> pendingTpaRequests = new LinkedList<>();
    private final List<String> pendingTpaRequestsSent = new LinkedList<>();

    public CrossServerLocation getCrossServerLocation(String name) {
        return this.crossServerLocationMap.get(name.toLowerCase());
    }

    public void setCrossServerLocation(String name, CrossServerLocation gameHome) {
        this.crossServerLocationMap.put(name.toLowerCase(), gameHome);
    }

    public void removeCrossServerLocation(String name) {
        this.crossServerLocationMap.remove(name.toLowerCase());
    }

    public void addRunnable(String xuid, TaskHandler taskHandler) {
        this.runnableMap.put(xuid, taskHandler);
    }

    public void removeRunnable(String xuid) {
        this.runnableMap.remove(xuid);
    }

    public void cancelRunnable(String xuid) {
        TaskHandler taskHandler = this.runnableMap.remove(xuid);

        if (taskHandler == null) {
            return;
        }

        taskHandler.cancel();
    }

    public static void add(String xuid, GamePlayer gamePlayer) {
        playerMap.put(xuid, gamePlayer);
    }

    public static void clear(String xuid) {
        GamePlayer gamePlayer = playerMap.remove(xuid);

        if (gamePlayer == null) return;

        gamePlayer.crossServerLocationMap.clear();

        gamePlayer.runnableMap.values().forEach(TaskHandler::cancel);
        gamePlayer.runnableMap.clear();
    }

    public static GamePlayer of(Player player) {
        return of(player.getLoginChainData().getXUID());
    }

    public static GamePlayer of(String xuid) {
        return playerMap.get(xuid);
    }
}