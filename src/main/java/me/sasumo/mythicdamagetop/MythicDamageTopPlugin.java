package me.sasumo.mythicdamagetop;

import me.sasumo.mythicdamagetop.combat.CombatManager;
import me.sasumo.mythicdamagetop.commands.AdminCommand;
import me.sasumo.mythicdamagetop.commands.BossSpawnCommand;
import me.sasumo.mythicdamagetop.config.ConfigManager;
import me.sasumo.mythicdamagetop.listeners.MobDamageListener;
import me.sasumo.mythicdamagetop.listeners.MobDeathListener;
import me.sasumo.mythicdamagetop.listeners.MobSpawnListener;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicDamageTopPlugin extends JavaPlugin {

    private static MythicDamageTopPlugin instance;

    private ConfigManager configManager;
    private CombatManager combatManager;

    @Override
    public void onEnable() {
        instance = this;

        if (getServer().getPluginManager().getPlugin("MythicMobs") == null) {
            getLogger().severe("MythicMobs no fue encontrado. Desactivando MythicDamageTop.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        saveResource("mobs.yml", false);

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.combatManager = new CombatManager(this);

        getServer().getPluginManager().registerEvents(new MobDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(this), this);

        BossSpawnCommand bossSpawnCommand = new BossSpawnCommand(this);
        if (getCommand("bossspawn") != null) {
            getCommand("bossspawn").setExecutor(bossSpawnCommand);
            getCommand("bossspawn").setTabCompleter(bossSpawnCommand);
        }

        if (getCommand("mdt") != null) {
            getCommand("mdt").setExecutor(new AdminCommand(this));
        }

        getLogger().info("MythicDamageTop habilitado correctamente. " +
                configManager.getMobConfigs().size() + " mob(s) cargado(s).");
    }

    @Override
    public void onDisable() {
        if (combatManager != null) {
            combatManager.clearAll();
        }
    }

    public static MythicDamageTopPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public void reload() {
        reloadConfig();
        configManager.load();
    }
}
