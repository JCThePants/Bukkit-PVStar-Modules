package com.jcwhatever.bukkit.pvs.modules.regions.regions;


import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.utils.BlockUtils;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaMoveEvent;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RegionTypeInfo(
        name="recrumble",
        description="A region of blocks that are removed moments after being walked on by players. Blocks are restored moments later.")
public class ReCrumbleFloorRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    private Set<Location> _dropped = new HashSet<Location>((int)getVolume());

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        // do nothing
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
        getArena().getEventManager().register(this);

        if (!canRestore()) {

            Future result;

            try {
                result = this.saveData();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            result.onCancel(new CancelHandler() {
                @Override
                public void run(String reason) {
                    setEnabled(false);

                }
            }).onFail(new FailHandler() {
                @Override
                public void run(String reason) {
                    setEnabled(false);
                }
            });
        }
    }

    @Override
    protected void onDisable() {
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {

    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onCoordsChanged(Location p1, Location p2) throws IOException {
        super.onCoordsChanged(p1, p2);

        if (!canRestore()) {
            saveData();
        }
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {

        if (!isEnabled())
            return;

        try {
            restoreData(BuildMethod.PERFORMANCE, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _dropped.clear();
    }

    @GenericsEventHandler
    private void onPlayerMove(PlayerArenaMoveEvent event) {

        if (!isEnabled())
            return;

        Location loc = event.getPlayer().getLocation();

        Location adjusted = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

        if (_dropped.contains(adjusted))
            return;

        if (!contains(adjusted))
            return;

        Block block = adjusted.getBlock();
        if (block.getType() == Material.AIR ||
                block.getType() == Material.WATER ||
                block.getType() == Material.LAVA)
            return;

        _dropped.add(adjusted);

        breakBlock(block.getLocation());
    }


    private void breakBlock(final Location location) {

        ArenaScheduler.runTaskLater(getArena(), 5, new Runnable() {

            @Override
            public void run() {

                Block below = location.clone().add(0, -1, 0).getBlock();
                final BlockState blockState = location.getBlock().getState();

                if (below.getType() != Material.AIR) {
                    location.getBlock().setType(Material.AIR);
                } else {
                    BlockUtils.dropRemoveBlock(location, 20);
                }

                Scheduler.runTaskLater(PVStarAPI.getPlugin(), 40, new Runnable() {

                    @Override
                    public void run() {
                        blockState.update(true);
                        _dropped.remove(location);
                    }
                });
            }

        });
    }



}
