package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.AbstractEssentials;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.object.GamePlayer;

public final class TpaCommand extends Command {

    public TpaCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /tpa <player>");

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

        Player target = Server.getInstance().getPlayer(args[0]);

        if (target == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_NOT_FOUND", args[0]));

            return false;
        }

        if (AbstractEssentials.released() && target.getName().equalsIgnoreCase(commandSender.getName())) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("YOU_CANT_USE_THIS_ON_YOURSELF"));

            return false;
        }

        GamePlayer targetGamePlayer = GamePlayer.of(target);

        if (targetGamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (targetGamePlayer.getPendingTpaRequests().contains(gamePlayer.getName()) || gamePlayer.getPendingTpaRequestsSent().contains(targetGamePlayer.getName())) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("TPA_REQUEST_ALREADY_SENT", args[0]));

            return false;
        }

        if (AbstractEssentials.released() && !targetGamePlayer.isAcceptingTpaRequests()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("TARGET_NOT_IS_ACCEPTING_TPA_REQUESTS"));

            return false;
        }

        targetGamePlayer.getPendingTpaRequests().add(gamePlayer.getName());
        gamePlayer.getPendingTpaRequestsSent().add(targetGamePlayer.getName());

        target.sendMessage(Placeholders.replacePlaceholders("TPA_REQUEST_RECEIVED", commandSender.getName()));
        commandSender.sendMessage(Placeholders.replacePlaceholders("TPA_REQUEST_SUCCESSFULLY_SENT", target.getName()));

        TaskHandler taskHandler = Server.getInstance().getScheduler().scheduleDelayedTask(AbstractEssentials.getInstance(), () -> {
            if (!gamePlayer.getPendingTpaRequestsSent().contains(target.getName())) {
                return;
            }

            if (((Player) commandSender).isOnline()) {
                commandSender.sendMessage(Placeholders.replacePlaceholders("TPA_REQUEST_SENT_EXPIRED", targetGamePlayer.getName()));

                gamePlayer.removeRunnable(targetGamePlayer.getXuid());
            }

            gamePlayer.getPendingTpaRequestsSent().remove(target.getName());
            targetGamePlayer.getPendingTpaRequests().remove(commandSender.getName());
        }, AbstractEssentials.getInstance().getConfig().getInt("general.tpa-timeout", 5) * 20);

        gamePlayer.addRunnable(targetGamePlayer.getXuid(), taskHandler);

        return false;
    }
}