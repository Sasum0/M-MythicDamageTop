package me.sasumo.mythicdamagetop.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MobConfig {

    private final String mobId;
    private boolean enabled;
    private int topSize;
    private final Map<Integer, RewardConfig> rewards = new LinkedHashMap<>();
    private List<String> announceLines = new ArrayList<>();
    private List<String> spawnMessageLines = new ArrayList<>();
    private int defaultAmount = 1;
    private int maxAmount = 1;
    private String defaultFacing = "SOUTH";

    private MobConfig(String mobId) {
        this.mobId = mobId;
    }

    public static MobConfig fromSection(String mobId, ConfigurationSection section, int topMaxSize) {
        MobConfig config = new MobConfig(mobId);
        config.enabled = section.getBoolean("enabled", true);
        config.topSize = Math.max(1, Math.min(section.getInt("top.size", 5), topMaxSize));

        ConfigurationSection rewardsSection = section.getConfigurationSection("top.rewards");
        if (rewardsSection != null) {
            for (String posKey : rewardsSection.getKeys(false)) {
                try {
                    int position = Integer.parseInt(posKey.trim());
                    ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(posKey);
                    if (rewardSection != null) {
                        config.rewards.put(position, RewardConfig.fromSection(rewardSection));
                    }
                } catch (NumberFormatException ex) {
                    // Clave no numerica en top.rewards (ej: rangos "3-5"), se ignora.
                    // Si se quiere soportar rangos en el futuro, parsear aca.
                }
            }
        }

        config.announceLines = section.getStringList("announce.lines");
        config.spawnMessageLines = section.getStringList("spawn-message.lines");

        config.defaultAmount = Math.max(1, section.getInt("spawn-command.default-amount", 1));
        config.maxAmount = Math.max(config.defaultAmount, section.getInt("spawn-command.max-amount", 1));
        config.defaultFacing = section.getString("spawn-command.default-facing", "SOUTH");

        return config;
    }

    public String getMobId() {
        return mobId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTopSize() {
        return topSize;
    }

    public Map<Integer, RewardConfig> getRewards() {
        return rewards;
    }

    public List<String> getAnnounceLines() {
        return announceLines;
    }

    public List<String> getSpawnMessageLines() {
        return spawnMessageLines;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public String getDefaultFacing() {
        return defaultFacing;
    }
}
