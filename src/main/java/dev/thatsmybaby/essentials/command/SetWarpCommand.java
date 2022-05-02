package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;

public final class SetWarpCommand extends Command {

    public SetWarpCommand(String name, String description) {
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
            commandSender.sendMessage(TextFormat.RED + "Usage: /setwarp <name>");

            return false;
        }

        commandSender.sendMessage(Placeholders.replacePlaceholders("WARP_SUCCESSFULLY_" + (CrossServerTeleportFactory.getInstance().getWarpCrossServerLocation(args[0]) == null ? "CREATED" : "UPDATED"), args[0]));

        TaskUtils.runAsync(() -> CrossServerTeleportFactory.getInstance().createWarpCrossServerLocation(args[0], ((Player) commandSender).getLocation()));

        return false;
    }
}