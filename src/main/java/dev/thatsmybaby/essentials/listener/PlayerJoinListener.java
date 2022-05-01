package dev.thatsmybaby.essentials.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.GamePlayerFactory;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        TaskUtils.runAsync(() -> GamePlayerFactory.getInstance().loadGamePlayer(player.getLoginChainData().getXUID(), player.getName()));
    }
}