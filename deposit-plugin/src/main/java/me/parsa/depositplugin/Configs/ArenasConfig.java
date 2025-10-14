package me.parsa.depositplugin.Configs;

import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ArenasConfig {

    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void setup() {
        // Store chestLocations.yml in Bedwars addons folder
        file = new File(DepositPlugin.bedWars.getAddonsPath(), "chestLocations.yml");

        // Ensure folder exists
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // -------------------------------
        // 🔍 Smart reset logic
        // -------------------------------
        boolean shouldReset = false;
        try {
            // Check all loaded arenas in BedWars
            shouldReset = DepositPlugin.bedWars.getArenaUtil().getArenas().stream()
                    .anyMatch(arena -> {
                        String name = arena.getWorldName().toLowerCase();
                        return name.startsWith("private-")
                                || name.startsWith("8f8346-")
                                || name.startsWith("6c1427-");
                    });
        } catch (Exception ignored) {
            // In case arenas are not yet loaded, skip check
        }

        // If matches our temp world prefixes, reset config
        if (shouldReset && file.exists()) {
            if (file.delete()) {
                DepositPlugin.info("Detected temporary SWM worlds (Private-/8f8346-/6c1427-). Resetting chestLocations.yml.");
            } else {
                DepositPlugin.error("Could not delete old chestLocations.yml for reset.");
            }
        }

        // Create file if missing
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                DepositPlugin.error("Failed to create chestLocations.yml: " + e.getMessage());
            }
        }

        // Load configuration
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return fileConfiguration;
    }

    public static void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            DepositPlugin.error("Error while saving chestLocations.yml: " + e.getMessage());
        }
    }

    public static void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
