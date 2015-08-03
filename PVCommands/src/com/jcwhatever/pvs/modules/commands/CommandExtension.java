package com.jcwhatever.pvs.modules.commands;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.events.players.PlayerCommandEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ArenaExtensionInfo(
        name="PVCommands",
        description="Manage commands that can be used in an arena.")
public class CommandExtension extends ArenaExtension implements IEventListener {

    private final Set<String> _allowed = new HashSet<>(10);

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    /**
     * Add a whitelisted command.
     *
     * @param command  The command to add.
     */
    public void addCommand(String command) {
        PreCon.notNullOrEmpty(command);

        _allowed.add(command.toLowerCase());
        save();
    }

    /**
     * Delete a command from the whitelist.
     *
     * @param command  The command to remove.
     */
    public void delCommand(String command) {
        PreCon.notNullOrEmpty(command);

        _allowed.remove(command.toLowerCase());
        save();
    }

    /**
     * Get all whitelisted commands.
     */
    public List<String> getCommands() {
        return new ArrayList<>(_allowed);
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
        load();
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    private void load() {
        IDataNode dataNode = getDataNode();

        List<String> allowed = dataNode.getStringList("allowed-commands", new ArrayList<String>(0));
        assert allowed != null;

        _allowed.clear();
        _allowed.addAll(allowed);
    }

    private void save() {
        IDataNode dataNode = getDataNode();

        dataNode.set("allowed-commands", _allowed);
        dataNode.save();
    }

    @EventMethod(invokeForCancelled = true)
    private void onCommand(PlayerCommandEvent event) {

        if (!event.isCancelled())
            return;

        String command = event.getCommand().toLowerCase();

        if (_allowed.contains(command))
            event.setCancelled(false);
    }
}
