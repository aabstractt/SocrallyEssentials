package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;

import java.sql.SQLException;

public final class DeleteHomeCommand extends Command {

    public DeleteHomeCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        if (!this.testPermission(commandSender)) {
            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /delhome <home>");

            return false;
        }

        TaskUtils.runAsync(() -> {
            try {
                if (CrossServerTeleportFactory.getInstance().getHomePosition(((Player) commandSender).getLoginChainData().getXUID(), args[0]) == null) {
                    commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", args[0]));

                    return;
                }

                CrossServerTeleportFactory.getInstance().removePlayerHome(((Player) commandSender).getLoginChainData().getXUID(), args[0]);

                commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_SUCCESSFULLY_REMOVED", args[0]));
            } catch (SQLException e) {
                e.printStackTrace();

                ((Player) commandSender).kick("An error occurred with DeleteHomeCommand.java");
            }
        });

        return false;
    }
}