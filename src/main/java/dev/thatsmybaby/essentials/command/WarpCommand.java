package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class WarpCommand extends Command {

    public WarpCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /warp <name>");

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (gamePlayer.isAlreadyTeleporting()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_ALREADY_TELEPORTING"));

            return false;
        }

        CrossServerLocation crossServerLocation = CrossServerTeleportFactory.getInstance().getWarpCrossServerLocation(args[0]);

        if (crossServerLocation == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("WARP_NOT_FOUND", args[0]));

            return false;
        }

        if (!commandSender.hasPermission("warp." + crossServerLocation.getName().toLowerCase())) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("NO_ENOUGH_PERMS_WARP", crossServerLocation.getName()));

            return false;
        }

        HomeCommand.doTeleportQueue((Player) commandSender, gamePlayer, crossServerLocation.getLocationSerialized());

        return false;
    }
}