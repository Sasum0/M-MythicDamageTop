package me.sasumo.mythicdamagetop.config;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final MythicDamageTopPlugin plugin;

    // config.yml
    private boolean debug;
    private int combatTimeoutSeconds;
    private double minimumDamageToQualify;
    private int topMaxSize;
    private boolean resetOnRespawn;
    private int broadcastLineDelayTicks;
    private String bossSpawnPermission;
    private boolean allowAmountArgument;
    private boolean placeholderApiEnabled;

    // mobs.yml
    private final Map<String, MobConfig> mobConfigs = new HashMap<>();

    public ConfigManager(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        FileConfiguration cfg = plugin.getConfig();

        this.debug = cfg.getBoolean("settings.debug", false);
        this.combatTimeoutSeconds = cfg.getInt("combat.timeout-seconds", 15);
        this.minimumDamageToQualify = cfg.getDouble("combat.minimum-damage-to-qualify", 1);
        this.topMaxSize = cfg.getInt("top.max-size", 20);
        this.resetOnRespawn = cfg.getBoolean("top.reset-on-respawn", true);
        this.broadcastLineDelayTicks = cfg.getInt("broadcast.line-delay-ticks", 20);
        this.bossSpawnPermission = cfg.getString("command.bossspawn.permission", "mythicdamagetop.bossspawn");
        this.allowAmountArgument = cfg.getBoolean("command.bossspawn.allow-amount-argument", true);
        this.placeholderApiEnabled = cfg.getBoolean("placeholderapi.enabled", true);

        loadMobs();
    }

    private void loadMobs() {
        mobConfigs.clear();

        File mobsFile = new File(plugin.getDataFolder(), "mobs.yml");
        if (!mobsFile.exists()) {
            plugin.saveResource("mobs.yml", false);
        }

        YamlConfiguration mobsCfg = YamlConfiguration.loadConfiguration(mobsFile);
        ConfigurationSection mobsSection = mobsCfg.getConfigurationSection("mobs");
        if (mobsSection == null) {
            plugin.getLogger().warning("mobs.yml no tiene una seccion 'mobs'. No se cargo ningun mob.");
            return;
        }

        for (String mobId : mobsSection.getKeys(false)) {
            ConfigurationSection mobSection = mobsSection.getConfigurationSection(mobId);
            if (mobSection == null) continue;

            MobConfig mobConfig = MobConfig.fromSection(mobId, mobSection, topMaxSize);
            mobConfigs.put(mobId.toLowerCase(), mobConfig);
        }
    }

    public MobConfig getMobConfig(String mobId) {
        if (mobId == null) return null;
        return mobConfigs.get(mobId.toLowerCase());
    }

    public Map<String, MobConfig> getMobConfigs() {
        return mobConfigs;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getCombatTimeoutSeconds() {
        return combatTimeoutSeconds;
    }

    public double getMinimumDamageToQualify() {
        return minimumDamageToQualify;
    }

    public int getTopMaxSize() {
        return topMaxSize;
    }

    public boolean isResetOnRespawn() {
        return resetOnRespawn;
    }

    public int getBroadcastLineDelayTicks() {
        return broadcastLineDelayTicks;
    }

    public String getBossSpawnPermission() {
        return bossSpawnPermission;
    }

    public boolean isAllowAmountArgument() {
        return allowAmountArgument;
    }

    public boolean isPlaceholderApiEnabled() {
        return placeholderApiEnabled;
    }
}
