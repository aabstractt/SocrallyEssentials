package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class SpawnCommand extends Command {

    public SpawnCommand(String name) {
        super(name);

        this.setPermission("essentials.command.spawn");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        if (!this.testPermission(commandSender)) return false;

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (gamePlayer.isAlreadyTeleporting()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_ALREADY_TELEPORTING"));

            return false;
        }

        HomeCommand.doTeleportQueue((Player) commandSender, gamePlayer, Placeholders.stringFromLocation(Location.fromObject(Server.getInstance().getDefaultLevel().getSpawnLocation())));

        return false;
    }
}