package com.jcwhatever.bukkit.pvs.modules.mobs.spawners;

import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;

public interface ISpawner {
	
	/**
	 * Called when instantiated. Should only be called once.
	 */
	public void init(MobArenaExtension manager);


    public ISpawnerSettings getSettings();
	
	/**
	 * run or resume the spawner
	 */
	public void run();	
	
	/**
	 * Pause spawning of mobs.
	 */
	public void pause();
	
	/**
	 * Called when no longer needed. Cleans up resources, breaks down
	 * association with arena and stops all internal tasks.
	 */
	public void dispose();
}
