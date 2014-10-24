package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.collections.EntryCounter;
import com.jcwhatever.bukkit.generic.collections.EntryCounter.RemovalPolicy;
import com.jcwhatever.bukkit.generic.converters.ValueConverters;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.entity.Player;

import java.util.UUID;

@RegionTypeInfo(
        name="tell",
        description="Tell a player who enters or leaves the region a message.")
public class TellRegion extends AbstractPVRegion implements GenericsEventListener {

    protected static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
            .set("max-triggers-per-player", 1, ValueType.INTEGER, "Set the max number of times the message will be displayed per player. -1 for unlimited.")
            .set("enter-message", ValueType.STRING, "The message to display to the player who enters the region.")
            .set("leave-message", ValueType.STRING, "The message to display to the player who enters the region.")
            .setValueConverter("enter-message", ValueConverters.ALT_CHAT_COLOR)
            .setValueConverter("leave-message", ValueConverters.ALT_CHAT_COLOR)
        ;
    }

    private EntryCounter<UUID> _enterCount = new EntryCounter<UUID>(RemovalPolicy.BOTTOM_OUT);
    private EntryCounter<UUID> _leaveCount = new EntryCounter<UUID>(RemovalPolicy.BOTTOM_OUT);

    private int _maxTriggersPerPlayer = 1;
    private String _enterMessage;
    private String _leaveMessage;

    @Override
    protected boolean canDoPlayerEnter(Player p) {

        if (!isEnabled() || _enterMessage == null)
            return false;

        if (_maxTriggersPerPlayer > -1) {

            int tellCount = _enterCount.getCount(p.getUniqueId());

            if (tellCount >= _maxTriggersPerPlayer)
                return false;
        }

        return true;
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {

        tellMessage(player, _enterMessage);

        _enterCount.add(player.getUniqueId());
    }


    @Override
    protected boolean canDoPlayerLeave(Player p) {

        if (!isEnabled() || _leaveMessage == null)
            return false;

        if (_maxTriggersPerPlayer > -1) {

            int tellCount = _leaveCount.getCount(p.getUniqueId());

            if (tellCount >= _maxTriggersPerPlayer)
                return false;
        }

        return true;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {

        tellMessage(player, _leaveMessage);

        _leaveCount.add(player.getUniqueId());
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
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        setIsPlayerWatcher(false);
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _maxTriggersPerPlayer = dataNode.getInteger("max-triggers-per-player", _maxTriggersPerPlayer);
        _enterMessage = dataNode.getString("enter-message");
        _leaveMessage = dataNode.getString("leave-message");
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    protected void tellMessage(ArenaPlayer player, String message) {
        Msg.tell(player.getHandle(), message);
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {
        _enterCount.reset();
        _leaveCount.reset();
    }


}
