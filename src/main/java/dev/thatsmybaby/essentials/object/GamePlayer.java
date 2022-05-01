package dev.thatsmybaby.essentials.object;

import cn.nukkit.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor @Getter
public final class GamePlayer {

    private static final Map<String, GamePlayer> playerMap = new HashMap<>();

    private final String xuid;
    private final String name;

    private final Map<String, CrossServerLocation> crossServerLocationMap;

    public static GamePlayer of(Player player) {
        return of(player.getLoginChainData().getXUID());
    }

    public static GamePlayer of(String xuid) {
        return playerMap.get(xuid);
    }
}