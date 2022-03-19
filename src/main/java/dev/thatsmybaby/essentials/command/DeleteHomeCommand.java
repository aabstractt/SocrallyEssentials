package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.HomeFactory;

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
            Position targetPosition = HomeFactory.getInstance().getHomePosition(((Player) commandSender).getLoginChainData().getXUID(), args[0]);

            if (targetPosition == null) {
                commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", args[0]));

                return;
            }

            HomeFactory.getInstance().removePlayerHome(((Player) commandSender).getLoginChainData().getXUID(), args[0]);

            commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_SUCCESSFULLY_REMOVED", args[0]));
        });

        return false;
    }
}