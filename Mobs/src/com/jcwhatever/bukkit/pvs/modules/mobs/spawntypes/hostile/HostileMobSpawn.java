package com.jcwhatever.bukkit.pvs.modules.mobs.spawntypes.hostile;

import com.jcwhatever.bukkit.generic.extended.EntityTypeExt;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HostileMobSpawn extends SpawnType {
	
	private static final EntityType[] _types = new EntityType[] { 
							EntityType.BLAZE,      EntityType.CAVE_SPIDER, EntityType.CREEPER,  EntityType.ENDER_DRAGON, 
							EntityType.ENDERMAN,   EntityType.GHAST,       EntityType.GIANT,    EntityType.MAGMA_CUBE, 
							EntityType.PIG_ZOMBIE, EntityType.SILVERFISH,  EntityType.SKELETON, EntityType.SLIME,
							EntityType.SPIDER,     EntityType.WITCH,       EntityType.WITHER,   EntityType.WOLF,
							EntityType.ZOMBIE
							};

    @Override
    public String getName() {
        return "HostileMob";
    }

    @Override
    public String getDescription() {
        return "Represents or spawns a hostile mob.";
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

            Class<? extends Entity> entityClass = Rand.get(_types).getEntityClass();
            Entity entity = location.getWorld().spawn(location, entityClass);
            result.add(entity);
        }

        return result;
    }

	@Override
	public boolean equals(Object obj) {

        if (!(obj instanceof SpawnType))
			return false;
		
		SpawnType spawnType = (SpawnType)obj;

        if (!spawnType.isSpawner())
            return false;

        EntityType[] types = spawnType.getEntityTypes();

        for (EntityType type : types) {
            EntityTypeExt typeExt = EntityTypeExt.from(type);

            if (!typeExt.isHostile())
                return false;
        }

        return true;
	}
}
