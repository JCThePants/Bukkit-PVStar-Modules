package com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile;

import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnType;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChickenJockeySpawn extends SpawnType {

	private static final EntityType[] _types = new EntityType[] { EntityType.CHICKEN, EntityType.ZOMBIE };

    @Override
    public String getName() {
        return "ChickenJockey";
    }

    @Override
    public String getDescription() {
        return "Chicken jockey mob spawn point.";
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
        List<Entity> result = new ArrayList<>(count * 2);

        for (int i=0; i < count; i++) {
            Chicken chicken = location.getWorld().spawn(location, Chicken.class);
            Zombie zombie = location.getWorld().spawn(location, Zombie.class);

            zombie.setBaby(true);
            zombie.setVillager(false);

            chicken.setPassenger(zombie);

            result.add(chicken);
            result.add(zombie);
        }

        return result;
    }
}
