package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.entity.Player;

@RegionTypeInfo(
        name="damage",
        description="Players are damaged (or given health) when they enter the region.")
public class DamageRegion extends AbstractPVRegion {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("damage", 1.0D, ValueType.DOUBLE, "The amount of damage inflicted on a player. Negative values give health.")
        ;
    }

    private double _damage = 1.0D;


    @Override
    protected void onPlayerEnter(ArenaPlayer player) {

        Player p = player.getHandle();

        if (_damage < 0) {
            double health = p.getHealth() + Math.abs(_damage);
            health = Math.min(p.getMaxHealth(), health);

            p.setHealth(health);
        }
        else {
            p.damage(_damage);
        }
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
        // do nothing
    }

    @Override
    protected boolean onTrigger() {
        return false;
    }

    @Override
    protected boolean onUntrigger() {
        return false;
    }

    @Override
    protected void onEnable() {
        setIsPlayerWatcher(true);
    }

    @Override
    protected void onDisable() {
        setIsPlayerWatcher(false);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _damage = dataNode.getDouble("damage", _damage);
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }
}
