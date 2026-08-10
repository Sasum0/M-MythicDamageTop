package me.sasumo.mythicdamagetop.combat;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final MythicDamageTopPlugin plugin;
    private final Map<UUID, CombatSession> sessions = new ConcurrentHashMap<>();

    public CombatManager(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    public CombatSession getOrCreateSession(UUID mobUniqueId, String mobType) {
        return sessions.computeIfAbsent(mobUniqueId, id -> new CombatSession(id, mobType));
    }

    public CombatSession getSession(UUID mobUniqueId) {
        return sessions.get(mobUniqueId);
    }

    public void addDamage(UUID mobUniqueId, String mobType, Player player, double damage) {
        CombatSession session = getOrCreateSession(mobUniqueId, mobType);
        session.addDamage(player.getUniqueId(), player.getName(), damage);
    }

    public void removeSession(UUID mobUniqueId) {
        sessions.remove(mobUniqueId);
    }

    public void clearAll() {
        sessions.clear();
    }

    /**
     * Limpia sesiones de combate huerfanas (mobs que se despawnearon,
     * fueron eliminados por comando, etc. sin disparar MythicMobDeathEvent),
     * usando el mismo timeout definido en combat.timeout-seconds.
     */
    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            long timeoutMillis = plugin.getConfigManager().getCombatTimeoutSeconds() * 1000L;
            sessions.values().removeIf(session -> (now - session.getLastDamageTime()) > timeoutMillis);
        }, 20L * 30, 20L * 30);
    }
}
