package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.HomeFactory;
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

        if (gamePlayer == null) return false;

        Location l = ((Player) commandSender).getLocation();

        commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_SUCCESSFULLY_CREATED", args[0], String.valueOf(l.getFloorX()), String.valueOf(l.getFloorY()), String.valueOf(l.getFloorZ())));
        TaskUtils.runAsync(() -> {
            CrossServerLocation crossServerLocation = HomeFactory.getInstance().createPlayerCrossServerLocation(((Player) commandSender).getLoginChainData().getXUID(), args[0], l);
        });

        return false;
    }
}