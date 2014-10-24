package com.jcwhatever.bukkit.pvs.modules.mobs;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

public class ArenaMob {

	private static Map<Entity, Arena> _mobs = new WeakHashMap<>(50);
	
	public static void registerEntity(Entity entity, Arena arena) {
		PreCon.notNull(entity);
		PreCon.notNull(arena);
		
		_mobs.put(entity, arena);
	}
	
	public static void registerLivingEntities(Collection<LivingEntity> entities, Arena arena) {
		PreCon.notNull(entities);
		PreCon.notNull(arena);
		
		for (LivingEntity entity : entities)
			_mobs.put(entity, arena);
	}
	
	public static void registerEntities(Collection<Entity> entities, Arena arena) {
		PreCon.notNull(entities);
		PreCon.notNull(arena);
		
		for (Entity entity : entities)
			_mobs.put(entity, arena);
	}
	
	public static Arena getEntityArena(Entity entity) {
		return _mobs.get(entity);
	}
		
}
