package net.ad1200am.commandCommands.readers;

import net.ad1200am.commandCommands.Main;
import net.ad1200am.commandCommands.utils.ListUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Config {

    private static FileConfiguration configuration = setUpConfig();

    public static void reload() {
        configuration = setUpConfig();
    }

    private static FileConfiguration setUpConfig() {
        File file = new File(Main.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "config.yml");
        if (file.exists()) return YamlConfiguration.loadConfiguration(file);
        try {
            Files.createDirectories(Path.of(Main.getPlugin().getDataFolder().getAbsolutePath()));
            boolean ignored = file.createNewFile();
            FileConfiguration c = YamlConfiguration.loadConfiguration(file);
            c.set("reloadOnUse", false);
            c.setComments("reloadOnUse", ListUtils.of("In this file you can create your own commands.",
                    "You have to define your commands in the \"commands\" array.",
                    "Use the argument \"level\" to define which permission level can execute the command.",
                    "The argument \"cmd\" defines the commands which will be executed. You can write a list of commands, or just one.",
                    "Branches can be created with a brach named \"next\"",
                    null,
                    null,
                    "Set \"reloadOnUse\" to true only if you wish to edit the \"ops.json\"-file on a live server.",
                    "Setting this to true may cause lag on large servers when using custom commands!"));
            c.set("commands", new ArrayList<>());
            c.setComments("commands", ListUtils.of(null,
                    "Define your commands here",
                    "An example:",
                    "commands:",
                    "  spawn:",
                    "    level: 0",
                    "    cmd: execute at @e[type=minecraft:marker,name=spawn] run tp @s ~ ~ ~",
                    "    next:",
                    "      sethere:",
                    "        level: 4",
                    "        cmd:",
                    "          - execute at @e[type=minecraft:marker,name=spawn] run forceload remove ~ ~ ~ ~",
                    "          - kill @e[type=minecraft:marker,name=spawn]",
                    "          - summon minecraft:marker ~ ~ ~ {CustomName:'[{\"text\":\"spawn\"}]'}",
                    "          - forceload add ~ ~ ~ ~",
                    null));
            c.save(file);
            return c;
        } catch (IOException e) {
            Main.getPlugin().getLogger().warning(e.toString());
        }
        return new YamlConfiguration();
    }

    public static boolean getReloadOnUse() {
        return configuration.getBoolean("reloadOnUse");
    }

    public static FileConfiguration getFileConfiguration() {
        return configuration;
    }
}
