package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import dev.thatsmybaby.essentials.object.GamePlayer;

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

        Player target = Server.getInstance().getPlayer(args[0]);

        if (target == null) {
            TaskUtils.runAsync(() -> handleAsync(commandSender, args[0], args[1]));

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of(target);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        CrossServerLocation crossServerLocation = gamePlayer.getCrossServerLocation(args[1]);

        if (crossServerLocation == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", args[1]));

            return false;
        }

        commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_SUCCESSFULLY_REMOVED", target.getName(), crossServerLocation.getName()));

        TaskUtils.runAsync(() -> CrossServerTeleportFactory.getInstance().removePlayerCrossServerLocation(target.getName(), crossServerLocation.getName()));

        gamePlayer.removeCrossServerLocation(crossServerLocation.getName());

        return false;
    }

    private void handleAsync(CommandSender sender, String name, String homeName) {
        int rowCount = CrossServerTeleportFactory.getInstance().removePlayerCrossServerLocation(name, homeName);

        if (rowCount == -1) {
            sender.sendMessage(Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", name));

            return;
        }

        if (rowCount != 1) {
            sender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", homeName));

            return;
        }

        sender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_SUCCESSFULLY_REMOVED", name, homeName));
    }
}