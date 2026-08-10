package me.sasumo.mythicdamagetop.combat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CombatSession {

    private final UUID mobUniqueId;
    private final String mobType;
    private final Map<UUID, Double> damageMap = new LinkedHashMap<>();
    private final Map<UUID, String> playerNames = new LinkedHashMap<>();
    private volatile long lastDamageTime;

    public CombatSession(UUID mobUniqueId, String mobType) {
        this.mobUniqueId = mobUniqueId;
        this.mobType = mobType;
        this.lastDamageTime = System.currentTimeMillis();
    }

    public synchronized void addDamage(UUID playerId, String playerName, double damage) {
        damageMap.merge(playerId, damage, Double::sum);
        playerNames.put(playerId, playerName);
        this.lastDamageTime = System.currentTimeMillis();
    }

    public double getTotalDamage() {
        return damageMap.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public UUID getMobUniqueId() {
        return mobUniqueId;
    }

    public String getMobType() {
        return mobType;
    }

    public Map<UUID, Double> getDamageMap() {
        return damageMap;
    }

    public Map<UUID, String> getPlayerNames() {
        return playerNames;
    }

    public long getLastDamageTime() {
        return lastDamageTime;
    }
}
