package dev.thatsmybaby.essentials;

import cn.nukkit.command.Command;
import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.essentials.command.HomeCommand;
import dev.thatsmybaby.essentials.command.SetHomeCommand;
import dev.thatsmybaby.essentials.factory.HomeFactory;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;

public final class EssentialsLoader extends PluginBase {

    @Getter private static EssentialsLoader instance;

    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveResource("messages.yml");
        this.saveResource("hikari.properties");

        HomeFactory.getInstance().init(new File(this.getDataFolder(), "hikari.properties"));

        this.getServer().getCommandMap().register("essentials", new SetHomeCommand("sethome", "Set a new home"));
        this.getServer().getCommandMap().register("essentials", new HomeCommand("home", "Go to a specify home"));
    }
}