package com.jcwhatever.bukkit.pvs.modules.mobs.spawngroups;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpawnGroup extends Spawnpoint {

    private MobArenaExtension _manager;
    private List<Spawnpoint> _spawns = new ArrayList<>(10);

    public SpawnGroup(MobArenaExtension manager, Spawnpoint primary) {
        super(primary.getName(), primary.getSpawnType(), primary.getTeam(), primary.getWorld(), primary.getX(), primary.getY(), primary.getZ(), primary.getYaw(), primary.getPitch());
        _manager = manager;
        _spawns.add(primary);
    }

    public void addSpawn(Spawnpoint spawnpoint) {
        _spawns.add(spawnpoint);
    }

    public void addSpawns(Collection<Spawnpoint> groupSpawns) {
        _spawns.addAll(groupSpawns);
    }

    public List<Spawnpoint> getSpawns() {
        return new ArrayList<>(_spawns);
    }

    @Nullable
    @Override
    public List<Entity> spawn(Arena arena, int count) {
        PreCon.notNull(arena);
        PreCon.greaterThanZero(count);
        PreCon.isValid(arena.equals(_manager.getArena()), "Can only spawn for arena: " + _manager.getArena().getName());

        List<Entity> result = new ArrayList<>(count * _spawns.size());

        for (Spawnpoint spawn : _spawns) {

            int max = _manager.getMaxMobs() - _manager.getMobCount();

            if (max <= 0)
                break;

            List<Entity> mobs = spawn.spawn(arena, Math.min(max, count));
            if (mobs == null)
                continue;

            result.addAll(mobs);
        }

        return result;
    }


}
