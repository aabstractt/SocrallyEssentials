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

        if (!Placeholders.isNumber(args[1])) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("INVALID_NUMBER", args[1]));

            return false;
        }

        int amount = Integer.parseInt(args[1]);

        Player target = Server.getInstance().getPlayer(args[0]);

        if (target == null) {
            TaskUtils.runAsync(() -> handleAsync(commandSender, args[0], amount));

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of(target);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        gamePlayer.setMaxHomeSize(amount);

        commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_LIMIT_UPDATED", gamePlayer.getName(), args[1]));

        TaskUtils.runAsync(() -> GamePlayerFactory.getInstance().updateMaxHomeSize(gamePlayer.getXuid(), amount));

        return false;
    }

    private void handleAsync(CommandSender commandSender, String name, int amount) {
        String xuid = GamePlayerFactory.getInstance().getTargetXuid(name);

        if (xuid == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", name));

            return;
        }

        commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_LIMIT_UPDATED", name, String.valueOf(amount)));

        GamePlayerFactory.getInstance().updateMaxHomeSize(xuid, amount);
    }
}