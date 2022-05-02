package dev.thatsmybaby.essentials.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import dev.thatsmybaby.essentials.AbstractEssentials;
import dev.thatsmybaby.essentials.Placeholders;

import java.io.File;

public final class EssentialsReloadCommand extends Command {

    public EssentialsReloadCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Placeholders.messages = (new Config(new File(AbstractEssentials.getInstance().getDataFolder(), "messages.yml"))).getAll();

        return false;
    }
}