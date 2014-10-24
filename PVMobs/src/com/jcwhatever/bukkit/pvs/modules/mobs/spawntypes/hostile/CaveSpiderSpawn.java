package com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile;

import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnType;
import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CaveSpiderSpawn extends SpawnType {
	
	private static final EntityType[] _types = new EntityType[] { EntityType.CAVE_SPIDER };

    @Override
    public String getName() {
        return "CaveSpider";
    }

    @Override
    public String getDescription() {
        return "Cave spider mob spawn point.";
    }

    @Override
    public boolean isSpawner() {
        return true;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isHostile() {
        return true;
    }

    @Override
	public EntityType[] getEntityTypes() {
		return _types;
	}

    @Nullable
    @Override
    public List<Entity> spawn(Arena arena, Location location, int count) {
        List<Entity> result = new ArrayList<>(count);

        for (int i=0; i < count; i++) {
            Entity entity = location.getWorld().spawn(location, CaveSpider.class);
            result.add(entity);
        }

        return result;
    }

}
