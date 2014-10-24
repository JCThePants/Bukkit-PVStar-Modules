package com.jcwhatever.bukkit.pvs.modules.playerstate;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.player.PlayerState;
import com.jcwhatever.bukkit.generic.player.PlayerState.RestoreLocation;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerRemovedEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

public class PlayerStateModule extends PVStarModule implements GenericsEventListener {

    @Override
    protected void onRegisterTypes() {

        PVStarAPI.getExtensionManager().registerType(PlayerStateExtension.class);
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getEventManager().register(this);
        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), PVStarAPI.getPlugin());
    }

    @GenericsEventHandler
    private void onPlayerRemoved(PlayerRemovedEvent event) {

        if (!event.isRestoring() || event.getPlayer().getHandle().isDead())
            return;

        PlayerState state = PlayerState.get(PVStarAPI.getPlugin(), event.getPlayer().getHandle());
        if (state == null || !state.isSaved())
            return;

        try {
            event.setRestoreLocation(state.restore(RestoreLocation.FALSE));
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}
