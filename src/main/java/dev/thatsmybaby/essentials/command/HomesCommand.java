package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import dev.thatsmybaby.essentials.object.GamePlayer;

import java.util.Map;

public final class HomesCommand extends Command {

    public HomesCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 0 && !(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /homes <player>");

            return false;
        }

        if (commandSender instanceof Player && (args.length == 0 || !commandSender.hasPermission("homes.command.others"))) {
            GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

            if (gamePlayer == null) {
                commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

                return false;
            }

            handleSeeHomes(commandSender, commandSender.getName(), gamePlayer.getCrossServerLocationMap());

            return false;
        }

        Player target = Server.getInstance().getPlayer(args[0]);

        if (target == null) {
            TaskUtils.runAsync(() -> {
                Map<String, CrossServerLocation> crossServerLocationMap = CrossServerTeleportFactory.getInstance().loadPlayerCrossServerLocations(args[0], false);

                if (crossServerLocationMap == null) {
                    commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", args[0]));

                    return;
                }

                handleSeeHomes(commandSender, args[0], crossServerLocationMap);
            });

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of(target);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        HomesCommand.handleSeeHomes(commandSender, target.getName(), gamePlayer.getCrossServerLocationMap());

        return false;
    }

    public static void handleSeeHomes(CommandSender sender, String name, Map<String, CrossServerLocation> crossServerLocationMap) {
        sender.sendMessage(Placeholders.replacePlaceholders("HOME_LIST_PLAYER", name));

        if (crossServerLocationMap.isEmpty()) {
            sender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOME_EMPTY"));

            return;
        }

        for (CrossServerLocation crossServerLocation : crossServerLocationMap.values()) {
            Location l = crossServerLocation.getLocation();

            sender.sendMessage(Placeholders.replacePlaceholders("HOME_LIST_TEXT", crossServerLocation.getName(), String.valueOf(l.getFloorX()), String.valueOf(l.getFloorY()), String.valueOf(l.getFloorZ()), l.getLevelName()));
        }
    }
}