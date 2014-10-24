package com.jcwhatever.bukkit.pvs.modules.mobs.spawners.proximity;

import com.jcwhatever.bukkit.generic.pathing.GroundPathCheck;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.utils.Scheduler.TaskHandler;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawnerSettings;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerInfo;
import com.jcwhatever.bukkit.pvs.modules.mobs.utils.DistanceUtils;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;

import java.util.List;

/**
 * Spawns mobs in an arena using the settings
 * specified in the mob manager.
 * 
 * @author JC The Pants
 *
 */
@SpawnerInfo(
        name="Proximity",
        description = "Spawn mobs from spawns that are in proximity to players."
)
public class ProximitySpawner implements ISpawner {

    private Arena _arena;
	private MobArenaExtension _manager;
    private ProximitySettings _settings;
    private List<Spawnpoint> _mobSpawns;

	private boolean _isRunning;
	private boolean _isPaused;
	private int _totalPlayers;
	private int _maxMobs;

	private ScheduledTask _spawnMobsTask;
	private ScheduledTask _despawnMobsTask;



	@Override
	public void init(MobArenaExtension manager) {
		PreCon.notNull(manager);

		_arena = manager.getArena();
		_manager = manager;
        _settings = new ProximitySettings(this);
	}

    @Override
    public ISpawnerSettings getSettings() {
        return _settings;
    }

    public MobArenaExtension getManager() {
        return _manager;
    }

	/**
	 * Starts the spawner. The spawner stops when the arena ends,
	 * there are no more players in the arena, or if there are no mob spawns.
	 */
    @Override
	public void run() {

		_isPaused = false;

		if (_arena == null || _isRunning || !_arena.getGameManager().isRunning() || _arena.getGameManager().getPlayerCount() == 0)
			return;

		if (!_manager.isEnabled())
			return;

		_isRunning = true;

		_totalPlayers = _arena.getGameManager().getPlayerCount();
		_maxMobs = Math.min(
                _manager.getMaxMobs(),
                _settings.getMaxMobsPerPlayer() * _totalPlayers);

        _mobSpawns = _manager.getMobSpawns();

		_spawnMobsTask = ArenaScheduler.runTaskRepeat(_arena, 5, 20 + (3 * _totalPlayers), new SpawnMobs());
		_despawnMobsTask = ArenaScheduler.runTaskRepeat(_arena, 10, 10, new DespawnMobs());
	}


    @Override
	public void pause() {
		_isPaused = true;
	}


	private void setMobTargets(List<LivingEntity> mobs) {

		for (LivingEntity entity : mobs) {

			if (!(entity instanceof Creature))
				continue;

			Creature creature = (Creature)entity;

			ArenaPlayer closest = DistanceUtils.getClosestPlayer(
                    _arena.getGameManager().getPlayers(), creature.getLocation(), _settings.getMaxMobDistanceSquared());

			if (closest == null) {
				_manager.removeMob(entity, DespawnMethod.REMOVE);
				continue;
			}

			creature.setTarget(closest.getHandle());

			if (creature instanceof PigZombie) {
				PigZombie pigZ = (PigZombie)creature;
				pigZ.setAngry(true);
			}
		}
	}


	/*
	 * Spawn mobs task
	 */
	class SpawnMobs extends TaskHandler {

		@Override
		public void run() {

			_manager.removeDead();

			if (_isPaused)
				return;

			List<ArenaPlayer> players = _arena.getGameManager().getPlayers();

			int maxMobsPerSpawn = _settings.getMaxMobsPerSpawn();

			if (_manager.getMobCount() < _maxMobs) {

				List<Spawnpoint> spawns = DistanceUtils.getClosestSpawns(
                        _arena, players, _mobSpawns, _settings.getMaxPathDistance());

				if (!spawns.isEmpty()) {

                    // TODO: exlude spawns

                    while (_manager.getMobCount() < _maxMobs) {

                        Spawnpoint spawn = Rand.get(spawns);
						List<LivingEntity> spawned = _manager.spawn(spawn, maxMobsPerSpawn);
                        if (spawned != null) {
                            setMobTargets(spawned);
                        }
					}
				}
			}
		}

		@Override
		protected void onCancel() {
			_manager.reset(DespawnMethod.REMOVE);
			_isRunning = false;
			_isPaused = false;
		}
	}


	class DespawnMobs implements Runnable {

		@Override
		public void run() {

			List<LivingEntity> mobs = _manager.getMobs();

			if (mobs.size() > 0) {

				LivingEntity mob = Rand.get(mobs);

                if (mob.isDead()) {
                    _manager.removeMob(mob, DespawnMethod.REMOVE);
                }
                else {
                    ArenaPlayer closest = DistanceUtils.getClosestPlayer(
                            _arena.getGameManager().getPlayers(), mob.getLocation(), _settings.getMaxMobDistanceSquared());

                    if (closest == null) {
                        _manager.removeMob(mob, DespawnMethod.REMOVE);
                        return;
                    }

                    if (!mob.hasLineOfSight(closest.getHandle())) {

                        GroundPathCheck astar = new GroundPathCheck();
                        astar.setMaxDropHeight(DistanceUtils.MAX_DROP_HEIGHT);
                        astar.setMaxRange(_settings.getMaxDistance());

                        int distance = astar.searchDistance(mob.getLocation(), closest.getLocation());

                        if (distance == -1 || distance > _settings.getMaxPathDistance())
                            _manager.removeMob(mob, DespawnMethod.REMOVE);
                    }
                }
			}
		}
	}


	
	@Override
	public void dispose() {
		if (_spawnMobsTask != null) {
			_spawnMobsTask.cancel();
			_spawnMobsTask = null;
		}
		
		if (_despawnMobsTask != null) {
			_despawnMobsTask.cancel();
			_despawnMobsTask = null;
		}
		
		_arena = null;
		_manager = null;
	}


}
