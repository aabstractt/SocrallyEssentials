package dev.thatsmybaby.essentials;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Placeholders {

    public static Map<String, Object> messages = new HashMap<>();

    public static String replacePlaceholders(String text, String... args) {
        if (messages.isEmpty()) {
            messages = (new Config(new File(EssentialsLoader.getInstance().getDataFolder(), "messages.yml"))).getAll();
        }

        if (messages.containsKey(text)) {
            text = messages.get(text).toString();
        }

        for (int i = 0; i < args.length; i++) {
            text = text.replaceAll("\\{%" + i + "}", args[i]);
        }

        return TextFormat.colorize(text);
    }

    public static String positionToString(Position position) {
        return String.format("%s;%s;%s;%s", position.getFloorX(), position.getFloorY(), position.getFloorZ(), position.getLevel().getFolderName());
    }

    public static Position stringToPosition(String string) {
        String[] split = string.split(";");

        if (split.length < 4) {
            throw new PluginException("Invalid string length");
        }

        if (!Server.getInstance().loadLevel(split[3])) {
            throw new PluginException("World " + split[3] + " not found");
        }

        return new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Server.getInstance().getLevelByName(split[3]));
    }
}