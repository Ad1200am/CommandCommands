package net.ad1200am.commandCommands.commands;

import net.ad1200am.commandCommands.readers.Config;
import net.ad1200am.commandCommands.readers.Permissions;
import net.ad1200am.commandCommands.utils.ListUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class CommandNode {

    private final ConfigurationSection configuration;
    private final Permissions.Level permissionLevel;
    private final String name;

    public CommandNode(String name) {
        this.name = name;
        configuration = Config.getFileConfiguration().getConfigurationSection("commands." + name);
        permissionLevel = Permissions.getLevel((configuration == null || configuration.get("level") == null) ? 0 : Double.valueOf(configuration.getDouble("level")).shortValue());
    }

    private CommandNode(ConfigurationSection configuration, Permissions.Level permissionLevel, String name) {
        this.configuration = configuration;
        this.permissionLevel = configuration.get("level") == null ? permissionLevel : Permissions.getLevel(Double.valueOf(configuration.getDouble("level")).shortValue());
        this.name = name;
    }

    public Permissions.Level getPermissionLevel() {
        return permissionLevel;
    }

    public List<String> getCommand() {

        List<String> list = configuration.getStringList("cmd");
        if (!list.isEmpty()) return list;
        if (configuration.getString("cmd") == null) return List.of();
        return ListUtils.of(configuration.getString("cmd"));
    }

    public boolean isLeaf() {
        return getBranchNames().isEmpty();
    }

    public String getName() {
        return name;
    }

    public List<String> getBranchNames() {
        String path = "next";
        ConfigurationSection section = configuration.getConfigurationSection(path);
        if (section == null) return new ArrayList<>();
        return section.getKeys(false).stream().toList();
    }

    public List<CommandNode> getBranches() {
        List<CommandNode> branches = new ArrayList<>();
        ConfigurationSection section0 = configuration.getConfigurationSection("next");
        if (section0 == null || isLeaf()) return new ArrayList<>();
        for (String key : getBranchNames()) {
            ConfigurationSection section = section0.getConfigurationSection(key);
            if (section == null) continue;
            branches.add(new CommandNode(section, permissionLevel, key));
        }
        return branches;
    }
}
