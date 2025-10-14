package me.parsa.depositplugin.Listeners;

import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class DepositWorldLoadListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        String worldName = world.getName();

        // Skip non-arena worlds (optional)
        if (!worldName.contains("Private-") && !worldName.contains("_temp")) return;

        String path = "worlds." + worldName + ".chestLocations";

        // Only create an empty section if it doesn’t exist
        if (!ArenasConfig.get().contains(path)) {
            DepositPlugin.debug("World loaded: " + worldName + " — creating empty chest location section.");
            ArenasConfig.get().set(path, new java.util.ArrayList<>());
            ArenasConfig.save();
        } else {
            DepositPlugin.debug("World loaded: " + worldName + " already exists in chestLocations.yml");
        }
    }
}
