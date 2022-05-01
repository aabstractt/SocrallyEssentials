package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class SetHomeCommand extends Command {

    public SetHomeCommand(String name, String description) {
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
            commandSender.sendMessage(TextFormat.RED + "Usage: /sethome <home>");

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (gamePlayer.getCrossServerLocation(args[0]) == null && gamePlayer.getCrossServerLocationMap().size() > gamePlayer.getMaxHomeSize()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("MAX_HOMES_REACHED", String.valueOf(gamePlayer.getCrossServerLocationMap().size())));

            return false;
        }

        TaskUtils.runAsync(() -> {
            CrossServerLocation crossServerLocation = CrossServerTeleportFactory.getInstance().createPlayerCrossServerLocation(((Player) commandSender).getLoginChainData().getXUID(), args[0], ((Player) commandSender).getLocation());

            if (crossServerLocation == null) {
                commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

                return;
            }

            commandSender.sendMessage(Placeholders.replacePlaceholders("SET_HOME_SUCCESSFULLY_" + (gamePlayer.getCrossServerLocation(args[0]) == null ? "CREATED" : "UPDATED"), args[0]));

            gamePlayer.setCrossServerLocation(args[0], crossServerLocation);
        });

        return false;
    }
}