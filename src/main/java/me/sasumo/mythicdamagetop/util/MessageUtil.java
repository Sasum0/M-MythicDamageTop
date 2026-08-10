package me.sasumo.mythicdamagetop.util;

import org.bukkit.ChatColor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    // Acepta tanto #RRGGBB como &#RRGGBB
    private static final Pattern HEX_PATTERN = Pattern.compile("&?#([A-Fa-f0-9]{6})");

    public static String color(String text) {
        if (text == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String legacyHex = toLegacyHex(matcher.group(1));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(legacyHex));
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Convierte "RRGGBB" al formato legacy que entiende el cliente de Minecraft:
     * §x§R§R§G§G§B§B. No depende de ChatColor.of(String), que fue removido
     * del API en versiones recientes (Adventure-only).
     */
    private static String toLegacyHex(String hex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.COLOR_CHAR).append('x');
        for (char c : hex.toCharArray()) {
            sb.append(ChatColor.COLOR_CHAR).append(c);
        }
        return sb.toString();
    }

    public static String replace(String text, Map<String, String> placeholders) {
        if (text == null) return "";
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String formatDamage(double damage) {
        return String.format("%.1f", damage);
    }

    public static String formatPercent(double percent) {
        return String.format("%.1f", percent);
    }
}
