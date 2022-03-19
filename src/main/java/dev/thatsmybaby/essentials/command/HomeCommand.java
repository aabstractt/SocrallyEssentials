package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.HomeFactory;

public final class HomeCommand extends Command {

    public HomeCommand(String name, String description) {
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
            commandSender.sendMessage(TextFormat.RED + "Usage: /home <name>");

            return false;
        }

        TaskUtils.runAsync(() -> {
            Position targetPosition = HomeFactory.getInstance().getHomePosition(((Player) commandSender).getLoginChainData().getXUID(), args[0]);

            if (targetPosition == null) {
                commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", args[0]));

                return;
            }

            commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_TELEPORTING", args[0]));

            TaskUtils.runLater(() -> ((Player) commandSender).teleport(targetPosition), (20 * 15));
        });

        return false;
    }
}