package com.yourserver.playercustomisation.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

import com.yourserver.playercustomisation.PlayerCustomisation;

public class ColorUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private static Pattern NICKNAME_PATTERN = null;
    private static PlayerCustomisation plugin = null;

    //new initialisation method test
    public static void init(PlayerCustomisation instance) {
        plugin = instance;
        reloadPattern();
    }

    public static void reloadPattern() {
        if (plugin != null) {
            String pattern = plugin.getConfig().getString("nickname.validation-pattern", "^[a-zA-Z0-9_]{3,16}$");
            NICKNAME_PATTERN = Pattern.compile(pattern);
        } else {
            // Fallback pattern
            NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
        }
    }

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Handle legacy color codes first
        Component component = legacySerializer.deserialize(text);
        
        // Then handle MiniMessage format
        try {
            component = miniMessage.deserialize(legacySerializer.serialize(component));
        } catch (Exception e) {
            // If MiniMessage parsing fails, return legacy format
        }
        
        return legacySerializer.serialize(component);
    }

    public static Component parseToComponent(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        try {
            // Try MiniMessage first
            return miniMessage.deserialize(text);
        } catch (Exception e) {
            // Fallback to legacy
            return legacySerializer.deserialize(text);
        }
    }

        public static boolean isValidNickname(String nickname) {
        if (NICKNAME_PATTERN == null) {
            reloadPattern();
        }
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    public static boolean isValidColorCode(String color) {
        if (color == null || color.isEmpty()) {
            return false;
        }
        
        // Check if it's a valid legacy color code or MiniMessage format
        try {
            Component test = parseToComponent(color + "test");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String stripColor(String text) {
        if (text == null) return null;
        Component component = parseToComponent(text);
        return legacySerializer.serialize(component.color(null));
    }
}