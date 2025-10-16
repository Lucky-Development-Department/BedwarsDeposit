package me.parsa.depositplugin.Listeners;

import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Arrays;
import java.util.List;

public class DepositWorldLoadListener implements Listener {

    private static final List<String> SWM_WORLD_PREFIXES = Arrays.asList("Private-", "8f8346-", "6c1427-");

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        final World world = event.getWorld();
        final String worldName = world.getName();

        if (!(startsWithAny(worldName, SWM_WORLD_PREFIXES) || worldName.endsWith("_temp"))) return;

        String path = "worlds." + worldName + ".chestLocations";

        // Only create an empty section if it doesn’t exist
        if (!ArenasConfig.get().contains(path)) {
            //ArenasConfig.setup();
            DepositPlugin.status("World loaded: " + worldName + " — creating empty chest location section.");
            ArenasConfig.get().set(path, new java.util.ArrayList<>());
            ArenasConfig.save();
        } else {
            DepositPlugin.status("World loaded: " + worldName + " already exists in chestLocations.yml");
        }
    }

    private boolean startsWithAny(String name, List<String> prefixes) {
        for (String p : prefixes) if (name.startsWith(p)) return true;
        return false;
    }
}
