package com.jcwhatever.bukkit.pvs.modules.mobs;

import com.jcwhatever.bukkit.generic.collections.EntryCounter;
import com.jcwhatever.bukkit.generic.collections.EntryCounter.RemovalPolicy;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.EnumUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.ArenaStartedEvent;
import com.jcwhatever.bukkit.pvs.api.events.spawns.AddSpawnEvent;
import com.jcwhatever.bukkit.pvs.api.events.spawns.RemoveSpawnEvent;
import com.jcwhatever.bukkit.pvs.api.spawns.SpawnType;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerManager;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.proximity.ProximitySpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawngroups.SpawnGroupGenerator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ArenaExtensionInfo(
        name="PVMobs",
        description = "Adds mob spawning support to an arena.")
public class MobArenaExtension extends ArenaExtension implements GenericsEventListener {

    public static final String NAME = "PVMobs";

    private SpawnGroupGenerator _groups;
    private ISpawner _spawner;

    private boolean _allowMobDrops = false;
    private int _maxMobs = 15;

    private List<LivingEntity> _mobs = new ArrayList<LivingEntity>(100);

    // stores max spawn limit for an entity type
    private Map<EntityType, Integer> _mobLimits = new EnumMap<>(EntityType.class);

    // count the number of entities of each type spawned
    private EntryCounter<EntityType> _mobCounter = new EntryCounter<EntityType>(RemovalPolicy.KEEP_COUNTING);

    @Override
    protected void onEnable() {

        String spawnerName = getDataNode().getString("spawner", "proximity");
        setSpawner(spawnerName);

        loadSettings();
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onArenaStart(ArenaStartedEvent event) {

        // TODO: Only run spawner if there are mob spawns

        // run spawner
        _spawner.run();
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {

    }

    @GenericsEventHandler
    private void onAddSpawn(AddSpawnEvent event) {
        loadSettings();
    }

    @GenericsEventHandler
    private void onRemoveSpawn(RemoveSpawnEvent event) {
        loadSettings();
    }

    public SpawnGroupGenerator getGroupGenerator() {
        return _groups;
    }

    public List<Spawnpoint> getMobSpawns() {
        return _groups.getSpawnGroups();
    }

    public boolean isDropsAllowed() {
        return _allowMobDrops;
    }

    public void setIsDropsAllowed(boolean isDropsAllowed) {
        _allowMobDrops = isDropsAllowed;

        IDataNode settings = getDataNode();

        settings.set("allow-drops", isDropsAllowed);
        settings.saveAsync(null);
    }

    public int getMaxMobs() {
        return _maxMobs;
    }

    public void setMaxMobs(int value) {
        _maxMobs = value;

        IDataNode settings = getDataNode();

        settings.set("max-mobs", value);
        settings.saveAsync(null);
    }

    public ISpawner getSpawner() {
        return _spawner;
    }

    public void setSpawner(Class<? extends ISpawner> spawnerClass) {

        if (_spawner != null) {
            _spawner.dispose();
            _spawner = null;
        }

        try {
            _spawner = spawnerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (_spawner != null) {
            _spawner.init(this);
        }
    }

    public void setSpawner(String spawnerName) {
        PreCon.notNullOrEmpty(spawnerName);

        Class<? extends ISpawner> spawnerClass = SpawnerManager.getSpawnerClass(spawnerName);

        if (spawnerClass == null) {
            spawnerClass = ProximitySpawner.class;
        }

        getDataNode().set("spawner", spawnerName.toLowerCase());
        getDataNode().saveAsync(null);

        setSpawner(spawnerClass);
    }

    @Nullable
    public String getSpawnerName() {
        return getDataNode().getString("spawner");
    }


    /**
     * Determine if a spawn type is allowed to spawn.
     * @param type  The spawn type.
     */
    public boolean canSpawnType(SpawnType type) {
        PreCon.notNull(type);

        // check to see if limits have been imposed
        if (_mobLimits.size() == 0) {
            return true; // no limits, can spawn
        }

        // make sure the spawn type hasn't reached its spawn limit
        EntityType[] entityTypes = type.getEntityTypes();
        if (entityTypes == null)
            return false;

        // check each entity type to see if its limit is reached.
        for (EntityType entityType : entityTypes) {

            Integer limit = _mobLimits.get(entityType);
            if (limit == null || limit < 0) {
                continue;
            }

            if (limit == 0) {
                return false;
            }

            int count = _mobCounter.getCount(entityType);

            if (count >= limit) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the limit for an entity type. Returns -1
     * if there is no limit.
     *
     * @param type  The entity type.
     */
    public int getMobLimit(EntityType type) {
        Integer value = _mobLimits.get(type);
        return (value != null) ? value : -1;
    }

    /**
     * Set the limit for an entity type.
     *
     * @param type   The entity type.
     * @param limit  The max allowed to spawn.
     */
    public void setMobLimit(EntityType type, int limit) {
        PreCon.notNull(type);

        _mobLimits.put(type, limit);

        IDataNode settings = getDataNode();

        if (limit >= 0)
            settings.set("limits." + type.name(), limit);
        else
            settings.remove("limits." + type.name());

        settings.saveAsync(null);
    }


    /**
     * Get number of spawned mobs.
     */
    public int getMobCount() {
        return _mobs.size();
    }


    /**
     * Get a list of the spawned mobs
     */
    public List<LivingEntity> getMobs() {
        return new ArrayList<LivingEntity>(_mobs);
    }


    public boolean isAtSpawnLimit() {
        return getMaxMobs() - _mobs.size() <= 0;
    }

    /**
     *
     * @param spawn
     * @return
     */
    @Nullable
    public List<LivingEntity> spawn(Spawnpoint spawn, int count) {
        PreCon.notNull(spawn);

        // make sure more spawns are allowed.
        if (isAtSpawnLimit())
            return null;

        // make sure the type hasn't reached its limit
        if (!canSpawnType(spawn.getSpawnType()))
            return null;

        // spawn the entity
        List<Entity> entities = spawn.spawn(getArena(), count);
        if (entities == null)
            return null;

        List<LivingEntity> result = new ArrayList<>(entities.size());

        // record each spawned entity and place into LivingEntity result list
        for (Entity entity : entities) {
            if (entity == null)
                throw new NullPointerException("Entity array has a null entry.");

            if (!(entity instanceof LivingEntity)) {
                entity.remove();
                continue;
            }

            result.add((LivingEntity) entity);
            _mobs.add((LivingEntity)entity);

            // TODO:
            ArenaMob.registerEntity(entity, getArena());

            _mobCounter.add(entity.getType());
        }

        return result;
    }


    public void reset(DespawnMethod method) {


        for (LivingEntity entity : _mobs) {
            _mobCounter.subtract(entity.getType());

            if (method == DespawnMethod.KILL)
                entity.damage(entity.getMaxHealth());
            else
                entity.remove();
        }

        _mobs.clear();
    }

    public void removeMob(LivingEntity entity, DespawnMethod method) {
        PreCon.notNull(entity);

        _mobs.remove(entity);
        _mobCounter.subtract(entity.getType());

        if (method == DespawnMethod.KILL)
            entity.damage(entity.getMaxHealth());
        else
            entity.remove();
    }


    public void removeDead() {
        ArrayList<LivingEntity> mobs = new ArrayList<LivingEntity>(_mobs);
        for (LivingEntity entity : mobs) {
            if (entity.isDead()) {
                _mobs.remove(entity);
                _mobCounter.subtract(entity.getType());
                entity.remove();
            }
        }
    }




    // get game spawns that are alive
    private List<Spawnpoint> getGameMobSpawns() {
        List<Spawnpoint> spawns = getArena().getSpawnManager().getSpawns();

        Iterator<Spawnpoint> iterator = spawns.iterator();

        while(iterator.hasNext()) {
            Spawnpoint spawn = iterator.next();

            if (!spawn.getSpawnType().isSpawner() || !spawn.getSpawnType().isAlive())
                iterator.remove();
        }

        return spawns;
    }

    private void loadSettings() {
        IDataNode settings = getDataNode();

        _allowMobDrops = settings.getBoolean("allow-drops", _allowMobDrops);
        _maxMobs = settings.getInteger("max-mobs", _maxMobs);
        _groups = new SpawnGroupGenerator(this, getGameMobSpawns());

        // entity type limits
        _mobLimits.clear();
        Set<String> entityNames = settings.getSubNodeNames("limits");
        if (entityNames != null && !entityNames.isEmpty()) {

            for (String entityName : entityNames) {

                EntityType type = EnumUtils.getEnum(entityName, EntityType.class);
                if (type == null)
                    continue;

                int limit = settings.getInteger("limits." + entityName, -1);
                if (limit < 0)
                    continue;

                _mobLimits.put(type, limit);
            }
        }

    }
}
