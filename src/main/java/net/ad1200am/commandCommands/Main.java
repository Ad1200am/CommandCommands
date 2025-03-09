package net.ad1200am.commandCommands;

import net.ad1200am.commandCommands.commands.CommandRegister;
import net.ad1200am.commandCommands.commands.CommandReload;
import net.ad1200am.commandCommands.readers.Config;
import net.ad1200am.commandCommands.readers.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        if (Config.getReloadOnUse()) {
            plugin.getLogger().log(Level.INFO, "ops.json is being read on every CommandCommands command execution. More information about it are in the config file.");
            Permissions.setReloadOnUse(true);
        }
        CommandRegister.setUpCommands();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Permissions(), this);

        getCommand("reloadcommands").setExecutor(new CommandReload());
        getCommand("reloadcommands").setTabCompleter(new CommandReload());
    }

    @Override
    public void onDisable() {
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
