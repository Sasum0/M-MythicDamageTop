package me.sasumo.mythicdamagetop.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class RewardConfig {

    private final List<String> commands;
    private final String message;
    private final String title;
    private final String subtitle;

    private RewardConfig(List<String> commands, String message, String title, String subtitle) {
        this.commands = commands;
        this.message = message;
        this.title = title;
        this.subtitle = subtitle;
    }

    public static RewardConfig fromSection(ConfigurationSection section) {
        return new RewardConfig(
                section.getStringList("commands"),
                section.getString("message", ""),
                section.getString("title", ""),
                section.getString("subtitle", "")
        );
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
