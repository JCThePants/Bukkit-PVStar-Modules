package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.regions.BuildMethod;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.BlockUtils;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaMoveEvent;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RegionTypeInfo(
        name="crumble",
        description="A region of blocks that are removed moments after being walked on by players.")
public class CrumbleFloorRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("affected-blocks", ValueType.ITEMSTACK, "Set the blocks affected by the region. Null or air affects all blocks.")
        ;
    }

    private ItemStack[] _affectedBlocks;
    private Set<Location> _dropped;

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
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _affectedBlocks = dataNode.getItemStacks("affected-blocks");
        _dropped = new HashSet<Location>((int)getVolume());
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onCoordsChanged(Location p1, Location p2) throws IOException {
        super.onCoordsChanged(p1, p2);

        if (!this.canRestore()) {
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

        breakBlock(block, event.getPlayer());
    }


    private void breakBlock(final Block block, ArenaPlayer breaker) {

        ItemStack[] affected = _affectedBlocks;
        if (affected == null)
            return;

        if (affected.length > 0) {
            boolean isFound = false;
            for (ItemStack item : affected) {
                if (block.getState().getData().equals(item.getData())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound)
                return;
        }

        ArenaScheduler.runTaskLater(getArena(), 5, new Runnable() {

            @Override
            public void run() {

                Block below = block.getLocation().clone().add(0, -1, 0).getBlock();

                if (below.getType() != Material.AIR) {
                    block.setType(Material.AIR);
                } else {
                    BlockUtils.dropRemoveBlock(block.getLocation(), 20);
                }
            }

        });
    }

}
