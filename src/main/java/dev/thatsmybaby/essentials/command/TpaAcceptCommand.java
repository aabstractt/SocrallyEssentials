package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.AbstractEssentials;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class TpaAcceptCommand extends Command {

    public TpaAcceptCommand(String name, String description) {
        super(name, description, "", new String[]{"ypyes"});
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

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

        if (AbstractEssentials.released() && !gamePlayer.isAcceptingTpaRequests()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("YOU_NOT_ARE_ACCEPTING_TPA_REQUESTS"));

            return false;
        }

        String targetName;

        if (args.length == 0 || !commandSender.hasPermission("tpa.accept.others")) {
            targetName = gamePlayer.getLastTpaRequest();
        } else {
            targetName = AbstractEssentials.getTargetName(args[0]);
        }

        if (targetName == null) {
            commandSender.sendMessage(args.length == 0 ? TextFormat.RED + "Usage: /tpaccept <player>" : Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", args[0]));

            return false;
        }

        Player target = Server.getInstance().getPlayer(targetName);

        if (target == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", targetName));

            return false;
        }

        GamePlayer targetGamePlayer = GamePlayer.of(target);

        if (targetGamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (!targetGamePlayer.isAcceptingTpaRequests()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("TARGET_NOT_IS_ACCEPTING_TPA_REQUESTS"));

            return false;
        }

        if (targetGamePlayer.isAlreadyTeleporting()) {
            target.sendMessage(Placeholders.replacePlaceholders("PLAYER_ALREADY_TELEPORTING"));

            return false;
        }

        targetGamePlayer.cancelRunnable(gamePlayer.getXuid());

        targetGamePlayer.getPendingTpaRequestsSent().remove(gamePlayer.getName());
        gamePlayer.getPendingTpaRequests().remove(target.getName());

        commandSender.sendMessage(Placeholders.replacePlaceholders("TPA_REQUEST_SUCCESSFULLY_ACCEPTED", targetName));
        target.sendMessage(Placeholders.replacePlaceholders("TARGET_TPA_REQUEST_SUCCESSFULLY_ACCEPTED", commandSender.getName()));

        HomeCommand.doTeleportQueue(target, targetGamePlayer, Placeholders.stringFromLocation(((Player) commandSender).getLocation()));

        return false;
    }
}