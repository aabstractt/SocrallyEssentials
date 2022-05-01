package dev.thatsmybaby.essentials.object;

import cn.nukkit.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor @Getter
public final class GamePlayer {

    private static final Map<String, GamePlayer> playerMap = new HashMap<>();

    private final String xuid;
    private final String name;

    private final Map<String, CrossServerLocation> crossServerLocationMap;

    @Setter private int maxHomeSize;
    @Setter private boolean alreadyTeleporting;

    public CrossServerLocation getCrossServerLocation(String name) {
        return this.crossServerLocationMap.get(name.toLowerCase());
    }

    public void setCrossServerLocation(String name, CrossServerLocation gameHome) {
        this.crossServerLocationMap.put(name.toLowerCase(), gameHome);
    }

    public void removeCrossServerLocation(String name) {
        this.crossServerLocationMap.remove(name.toLowerCase());
    }

    public static void add(String xuid, GamePlayer gamePlayer) {
        playerMap.put(xuid, gamePlayer);
    }

    public static GamePlayer of(Player player) {
        return of(player.getLoginChainData().getXUID());
    }

    public static GamePlayer of(String xuid) {
        return playerMap.get(xuid);
    }
}