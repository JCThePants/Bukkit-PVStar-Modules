package com.jcwhatever.pvs.modules.gamestate;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.Kits;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.events.ArenaStartedEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

@ArenaExtensionInfo(
        name = "PVGameState",
        description = "Set player state when game starts in an arena.")
public class GameStateExtension extends ArenaExtension implements IEventListener {

    private String _kitName;
    private int _expLevels = 0;
    private int _maxHealth = 20;
    private int _health = 20;
    private int _hunger = 20;
    private float _walkSpeed = 0.25f;

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    public String getKitName() {
        return _kitName;
    }

    public void setKitName(@Nullable String kitName) {

        save("kit", _kitName = kitName);
    }

    public int getExpLevels() {
        return _expLevels;
    }

    public void setExpLevels(int expLevels) {
        save("exp-levels", _expLevels = expLevels);
    }

    public int getMaxHealth() {
        return _maxHealth;
    }

    public void setMaxHealth(int max) {
        save("max-health", _maxHealth = max);
    }

    public int getHealth() {
        return _health;
    }

    public void setHealth(int health) {
        save("health", _health = health);
    }

    public int getHunger() {
        return _hunger;
    }

    public void setHunger(int hunger) {
        save("hunger", _hunger = hunger);
    }

    public float getWalkSpeed() {
        return _walkSpeed;
    }

    public void setWalkSpeed(float speed) {
        save("walk-speed", _walkSpeed = speed);
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);

        IDataNode dataNode = getDataNode();
        _kitName = dataNode.getString("kit");
        _expLevels = dataNode.getInteger("exp-levels", _expLevels);
        _maxHealth = dataNode.getInteger("max-health", _maxHealth);
        _health = dataNode.getInteger("health", _health);
        _hunger = dataNode.getInteger("hunger", _hunger);
        _walkSpeed = (float)dataNode.getDouble("walk-speed", _walkSpeed);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onGameStart(@SuppressWarnings("unused") ArenaStartedEvent event) {


        IKit kit = _kitName != null ? Kits.get(_kitName) : null;

        IArenaPlayerCollection players = getArena().getGame().getPlayers();
        for (IArenaPlayer arenaPlayer : players) {

            Player player = arenaPlayer.getPlayer();

            if (kit != null)
                kit.give(player);

            player.setMaxHealth(_maxHealth);
            player.setHealth(_health);
            player.setLevel(_expLevels);
            player.setWalkSpeed(_walkSpeed);
            player.setFoodLevel(_hunger);
        }
    }

    private void save(String nodeName, Object value) {
        IDataNode dataNode = getDataNode();
        dataNode.set(nodeName, value);
        dataNode.save();
    }
}
