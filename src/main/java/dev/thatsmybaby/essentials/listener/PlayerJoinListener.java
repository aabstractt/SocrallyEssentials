package dev.thatsmybaby.essentials.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;

import java.sql.SQLException;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        TaskUtils.runAsync(() -> {
            try {
                CrossServerTeleportFactory.getInstance().createOrUpdateUser(player.getLoginChainData().getXUID(), player.getName());
            } catch (SQLException e) {
                Server.getInstance().getLogger().logException(e);

                player.kick("An error occurred when tried update your data...");
            }
        });
    }
}