package me.parsa.depositplugin.Listeners;

import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class PlayerJoin implements Listener {

    private static final List<String> SWM_WORLD_PREFIXES = Arrays.asList("Private-", "8f8346-", "6c1427-");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!DepositPlugin.plugin.configuration.getBoolean("set-chest-locations-on-join")) {
            return;
        }

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        DepositPlugin.status("PlayerJoinEvent fired for " + player.getName() + " in world: " + worldName);

        // Only process SWM-created (cloned) worlds
        boolean isTargetWorld = SWM_WORLD_PREFIXES.stream().anyMatch(worldName::startsWith);
        if (!isTargetWorld) {
            DepositPlugin.status("Skipping chest location scan: not an SWM world (" + worldName + ")");
            return;
        }

        // Schedule retrying check
        new BukkitRunnable() {
            int attempts = 0;

            @Override
            public void run() {
                attempts++;

                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    DepositPlugin.status("[" + attempts + "] World " + worldName + " not yet loaded — waiting...");
                    if (attempts >= 10) cancel();
                    return;
                }

                if (DepositPlugin.bedWars == null) {
                    DepositPlugin.status("[" + attempts + "] BedWars instance not ready — waiting...");
                    if (attempts >= 10) cancel();
                    return;
                }

                if (DepositPlugin.bedWars.getArenaUtil().getArenaByName(worldName) == null) {
                    DepositPlugin.status("[" + attempts + "] Arena for world " + worldName + " not yet recognized — waiting...");
                    if (attempts >= 10) cancel();
                    return;
                }

                // ✅ Now the arena is fully registered
                DepositPlugin.status("WorldLoadEvent ran for " + worldName);
                new GameStartListener(DepositPlugin.plugin, ArenasConfig.get()).createHDLocations();
                DepositPlugin.status("WorldLoadEvent done for " + worldName);
                cancel();
            }
        }.runTaskTimer(DepositPlugin.plugin, 40L, 20L); // Start after 2s, retry every 1s
    }
}
