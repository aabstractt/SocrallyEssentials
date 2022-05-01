package dev.thatsmybaby.essentials.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.HomeFactory;

import java.sql.SQLException;

public final class RemoveHomeCommand extends Command {

    public RemoveHomeCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + commandLabel + " <player> <home_name>");

            return false;
        }

        if (!this.testPermission(commandSender)) {
            return false;
        }

        TaskUtils.runAsync(() -> {
            try {
                String xuid = HomeFactory.getInstance().getTargetXuid(args[0]);

                if (xuid == null) {
                    commandSender.sendMessage(TextFormat.RED + "Player " + args[0] + " not found");

                    return;
                }

                if (HomeFactory.getInstance().getHomePosition(xuid, args[1]) == null) {
                    commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", args[1]));

                    return;
                }

                HomeFactory.getInstance().removePlayerHome(xuid, args[1]);

                commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_SUCCESSFULLY_REMOVED", args[1], args[0]));
            } catch (SQLException e) {
                e.printStackTrace();

                commandSender.sendMessage(TextFormat.RED + "An error occurred with RemoveHomeCommand.java " + e);
            }
        });

        return false;
    }
}