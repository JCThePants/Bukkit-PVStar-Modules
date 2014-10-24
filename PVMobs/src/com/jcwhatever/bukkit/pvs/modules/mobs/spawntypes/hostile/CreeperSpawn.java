package com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile;

import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnType;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CreeperSpawn extends SpawnType {
	
	private static final EntityType[] _types = new EntityType[] { EntityType.CREEPER };

    @Override
    public String getName() {
        return "Creeper";
    }

    @Override
    public String getDescription() {
        return "Creeper mob spawn point.";
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

    @Override
    public List<Entity> spawn(Arena arena, Location location, int count) {

        List<Entity> result = new ArrayList<>(count);

        for (int i=0; i < count; i++) {
            Entity creeper = location.getWorld().spawn(location, Creeper.class);
            result.add(creeper);
        }

        return result;
    }
}
