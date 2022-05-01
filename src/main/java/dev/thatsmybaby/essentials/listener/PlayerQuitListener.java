package dev.thatsmybaby.essentials.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent ev) {
        GamePlayer.clear(ev.getPlayer().getLoginChainData().getXUID());
    }
}