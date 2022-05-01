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

import java.util.Map;

public final class SeeHomeCommand extends Command {

    public SeeHomeCommand(String name, String description) {
        super(name, description);

        this.setPermission("essentials.command.seehome");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!this.testPermission(commandSender)) {
            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + s + " <player>");

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

                HomesCommand.handleSeeHomes(commandSender, args[0], crossServerLocationMap);
            });

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of(target);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        HomesCommand.handleSeeHomes(commandSender, args[0], gamePlayer.getCrossServerLocationMap());

        return false;
    }
}