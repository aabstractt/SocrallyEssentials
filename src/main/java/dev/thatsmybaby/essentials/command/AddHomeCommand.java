package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.GamePlayerFactory;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class AddHomeCommand extends Command {

    public AddHomeCommand(String name, String description) {
        super(name, description);

        this.setPermission("essentials.command.addhome");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + s + " <player> <number>");

            return false;
        }

        if (!this.testPermission(commandSender)) return false;

        boolean negative = args[1].charAt(0) == '-';
        int amount = Placeholders.parseInt(negative ? args[1].substring(1) : args[1]);

        if (amount == 0) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("INVALID_NUMBER", args[1]));

            return false;
        }

        Player target = Server.getInstance().getPlayer(args[0]);

        if (target == null) {
            TaskUtils.runAsync(() -> handleAsync(commandSender, args[0], negative, amount));

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of(target);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        gamePlayer.setMaxHomeSize(negative ? gamePlayer.getMaxHomeSize() - amount : gamePlayer.getMaxHomeSize() + amount);

        commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_LIMIT_" + (negative ? "REMOVED" : "ADDED"), gamePlayer.getName(), String.valueOf(amount)));

        TaskUtils.runAsync(() -> GamePlayerFactory.getInstance().updateMaxHomeSize(gamePlayer.getXuid(), gamePlayer.getMaxHomeSize()));

        return false;
    }

    private void handleAsync(CommandSender commandSender, String name, boolean negative, int amount) {
        String xuid = GamePlayerFactory.getInstance().getTargetXuid(name);

        if (xuid == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", name));

            return;
        }

        commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_LIMIT_" + (negative ? "REMOVED" : "ADDED"), name, String.valueOf(amount)));

        int currentAmount = GamePlayerFactory.getInstance().getTargetMaxHomeSize(xuid);

        GamePlayerFactory.getInstance().updateMaxHomeSize(xuid, negative ? currentAmount - amount : currentAmount + amount);
    }
}