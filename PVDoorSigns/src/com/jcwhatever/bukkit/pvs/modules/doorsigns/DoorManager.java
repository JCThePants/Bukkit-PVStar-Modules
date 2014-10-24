package com.jcwhatever.bukkit.pvs.modules.doorsigns;

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.signs.SignManager;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.ArenaStartedEvent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoorManager implements GenericsEventListener {

    private Map<String, DoorBlocks> _doorsBySign = new HashMap<>(20);
    private MultiValueMap<Arena, DoorBlocks> _doorsByArena = new MultiValueMap<Arena, DoorBlocks>();

    public DoorManager() {
        PVStarAPI.getEventManager().register(this);
    }

    public void addArenaDoorBlocks(Arena arena, DoorBlocks doorBlocks) {
        _doorsByArena.put(arena, doorBlocks);
    }

    public void removeArenaDoorBlocks(String doorBlocksId) {

        DoorBlocks doorBlocks = _doorsBySign.remove(doorBlocksId);
        if (doorBlocks == null)
            return;

        doorBlocks.setOpen(false);

        _doorsByArena.removeValue(doorBlocks);
    }

    @Nullable
    public DoorBlocks findDoors(SignHandler handler, SignContainer signContainer) {
        PreCon.notNull(handler);
        PreCon.notNull(signContainer);

        Sign sign = signContainer.getSign();

        Arena arena = PVStarAPI.getArenaManager().getArena(sign.getLocation());
        if (arena == null)
            return null;

        int locationX = sign.getX() - 3;
        int locationY = sign.getY() - 3;
        int locationZ = sign.getZ() - 3;
        World world = sign.getWorld();
        ArrayList<Block> doorBlocks = new ArrayList<>(4);

        int xEnd = locationX + 6;
        for (int x = locationX; x < xEnd; x++) {

            int yEnd = locationY + 6;
            for (int y = locationY; y < yEnd; y++) {

                int zEnd = locationZ + 6;
                for (int z = locationZ; z < zEnd; z++) {

                    Block searchBlock = world.getBlockAt(x, y, z);

                    if (searchBlock.getType() != Material.IRON_DOOR_BLOCK)
                        continue;

                    doorBlocks.add(searchBlock);
                }
            }
        }

        if (doorBlocks.size() == 0) {
            return null;
        }

        return getDoorBlocks(arena, handler, signContainer, doorBlocks);
    }

    private DoorBlocks getDoorBlocks(Arena arena, SignHandler handler, SignContainer sign, List<Block> doorBlocks) {

        String doorId = SignManager.getSignNodeName(sign.getLocation());

        if (_doorsBySign.containsKey(doorId)) {
            return _doorsBySign.get(doorId);
        }

        DoorBlocks door = new DoorBlocks(arena, handler, sign, doorBlocks);

        _doorsBySign.put(doorId, door);
        _doorsByArena.put(arena, door);
        return door;
    }

    private void closeDoors(Arena arena) {

        List<DoorBlocks> doorBlocks = _doorsByArena.remove(arena);
        if (doorBlocks == null)
            return;

        for (DoorBlocks doorBlock : doorBlocks) {
            doorBlock.setOpen(false);

            PVStarAPI.getSignManager().restoreSign(
                    doorBlock.getSignHandler().getName(),
                    doorBlock.getSignContainer().getLocation());
        }
    }

    @GenericsEventHandler
    private void onArenaStarted(ArenaStartedEvent event) {
        closeDoors(event.getArena());
    }

    @GenericsEventHandler
    private void onArenaEnded(ArenaEndedEvent event) {
        closeDoors(event.getArena());
    }

}