package dev.thatsmybaby.essentials;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.essentials.command.*;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.factory.GamePlayerFactory;
import dev.thatsmybaby.essentials.listener.PlayerJoinListener;
import dev.thatsmybaby.essentials.listener.PlayerQuitListener;
import lombok.Getter;

import java.io.File;

public final class AbstractEssentials extends PluginBase {

    @Getter private static AbstractEssentials instance;

    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveResource("messages.yml");
        this.saveResource("hikari.properties");

        GamePlayerFactory.getInstance().init(new File(this.getDataFolder(), "hikari.properties"));
        CrossServerTeleportFactory.getInstance().init(new File(this.getDataFolder(), "hikari.properties"));

        this.getServer().getCommandMap().register("essentials", new AddHomeCommand("addhome", "Add home amount to a player"));
        this.getServer().getCommandMap().register("essentials", new SetHomeCommand("sethome", "Set a new home"));
        this.getServer().getCommandMap().register("essentials", new HomeCommand("home", "Go to a specify home"));
        this.getServer().getCommandMap().register("essentials", new DeleteHomeCommand("delhome", "Delete a home"));
        this.getServer().getCommandMap().register("essentials", new RemoveHomeCommand("remhome", "Remove a player home"));
        this.getServer().getCommandMap().register("essentials", new SeeHomeCommand("seehome", "See all player home"));
        this.getServer().getCommandMap().register("essentials", new HomesCommand("homes", "See you home list"));
        this.getServer().getCommandMap().register("essentials", new EssentialsReloadCommand("ereload"));

        this.getServer().getCommandMap().register("essentials", new TpaCommand("tpa", "Request tpa to a player"));
        this.getServer().getCommandMap().register("essentials", new TpaAcceptCommand("tpaccept", "Accept a tpa request"));

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    @Override
    public void onDisable() {
        GamePlayerFactory.getInstance().close();

        CrossServerTeleportFactory.getInstance().close();
    }

    public static boolean released() {
        return false;
    }

    public static String getTargetName(String name) {
        Player player = Server.getInstance().getPlayer(name);

        return player != null ? player.getName() : null;
    }
}