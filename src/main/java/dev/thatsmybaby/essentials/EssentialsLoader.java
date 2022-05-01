package dev.thatsmybaby.essentials;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.essentials.command.*;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.listener.PlayerJoinListener;
import lombok.Getter;

import java.io.File;

public final class EssentialsLoader extends PluginBase {

    @Getter private static EssentialsLoader instance;

    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveResource("messages.yml");
        this.saveResource("hikari.properties");

        CrossServerTeleportFactory.getInstance().init(new File(this.getDataFolder(), "hikari.properties"));

        this.getServer().getCommandMap().register("essentials", new SetHomeCommand("sethome", "Set a new home"));
        this.getServer().getCommandMap().register("essentials", new HomeCommand("home", "Go to a specify home"));
        this.getServer().getCommandMap().register("essentials", new DeleteHomeCommand("delhome", "Delete a home"));
        this.getServer().getCommandMap().register("essentials", new RemoveHomeCommand("remhome", "Remove a player home"));
        this.getServer().getCommandMap().register("essentials", new SeeHomeCommand("seehome", "See all player home"));

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onDisable() {
        CrossServerTeleportFactory.getInstance().close();
    }
}