package me.parsa.depositplugin.Listeners;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerKillEvent event){
        if (DepositPlugin.plugin.configuration.getBoolean("disable-hologram-after-death")) {
            DepositPlugin.debug("PlayerKillEvent triggered");
            new BukkitRunnable() {
                @Override
                public void run() {
                    DepositPlugin.debug("Run for player death triggered");
                    Player player = event.getVictim();
                    DepositPlugin.debug("Initializing gamestartlistener");
                    GameStartListener gameStartListener = new GameStartListener(DepositPlugin.plugin, ArenasConfig.get());
                    DepositPlugin.debug("Deleting hd");
                    gameStartListener.deleteHolograms(player);
                    DepositPlugin.debug("Done");
                }
            }.runTaskLaterAsynchronously(DepositPlugin.plugin, 5L);

        }
    }

}