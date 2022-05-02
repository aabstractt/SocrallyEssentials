package dev.thatsmybaby.essentials.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.object.CrossServerLocation;

public final class DelWarpCommand extends Command {

    public DelWarpCommand(String name, String description) {
        super(name, description);

        this.setPermission("delwarp.command.admin");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /delwarp <name>");

            return false;
        }

        if (!commandSender.hasPermission("delwarp.command.admin")) {
            commandSender.sendMessage(TextFormat.RED + "You don't have permissions to use this command.");

            return false;
        }

        CrossServerLocation crossServerLocation = CrossServerTeleportFactory.getInstance().getWarpCrossServerLocation(args[0]);

        if (crossServerLocation == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("WARP_NOT_FOUND", args[0]));

            return false;
        }

        commandSender.sendMessage(Placeholders.replacePlaceholders("WARP_SUCCESSFULLY_DELETED", crossServerLocation.getName()));

        TaskUtils.runAsync(() -> CrossServerTeleportFactory.getInstance().removeWarpCrossServerLocation(crossServerLocation.getName()));

        return false;
    }
}