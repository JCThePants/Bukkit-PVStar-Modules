/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.pvs.modules.citizens.scripts;

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.events.EventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventManager;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.CitizensModule;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.AbstractNPCEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCDespawnEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCSpawnEvent;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.api.ai.GoalController.GoalEntry;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.MobType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class ScriptNPC {

    private static Map<String, Class<? extends AbstractNPCEvent>> _registeredEvents = new HashMap<>(250);
    private static Map<NPC, ScriptNPC> _npcMap = new WeakHashMap<>(50);


    /**
     * Get an existing {@code ScriptNPC} from the specified NPC.
     *
     * @param npc  The NPC to check.
     */
    @Nullable
    public static ScriptNPC get(NPC npc) {
        return _npcMap.get(npc);
    }

    /**
     * Register an NPC event to be used in scripts.
     */
    public static void registerNPCEvent(Class<? extends AbstractNPCEvent> event) {
        _registeredEvents.put(event.getSimpleName().toLowerCase(), event);
        _registeredEvents.put(CitizensModule.getModule().getName().toLowerCase()
                + ':' + event.getSimpleName().toLowerCase(), event);
    }


    private final Arena _arena;
    private final NPC _npc;
    private final GenericsEventManager _eventManager;
    private final MultiValueMap<Class<?>, EventHandler> _registeredHandlers
                    = new MultiValueMap<>(15);

    private boolean _isDisposedOnDeath = true;

    /**
     * Constructor.
     *
     * @param arena      The owning arena.
     * @param registry   The arena {@code NPCRegistry}.
     * @param name       The name of the NPC.
     * @param type       The entity type of the NPC.
     */
    public ScriptNPC(Arena arena, NPCRegistry registry, String name, EntityType type) {
        PreCon.notNull(arena);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(type);

        _arena = arena;
        _npc = registry.createNPC(type, name);
        _eventManager = new GenericsEventManager(arena.getEventManager());

        _npcMap.put(_npc, this);
    }

    /**
     * Get the NPC's owning arena.
     */
    public Arena getArena() {

        return _arena;
    }

    /**
     * Get the NPC's event manager.
     */
    public GenericsEventManager getEventManager() {

        return _eventManager;
    }

    /**
     * Get the NPC's name
     */
    public String getName() {

        return _npc.getName();
    }

    /**
     * Get the NPC's full name.
     */
    public String getFullName() {

        return _npc.getFullName();
    }

    /**
     * Get the NPC's Id.
     */
    public int getId() {

        return _npc.getId();
    }

    /**
     * Get the npc entity.
     */
    public Entity getEntity() {

        return _npc.getEntity();
    }

    /**
     * Get the spawned npc entities location.
     */
    public Location getLocation() {

        return _npc.getEntity().getLocation();
    }

    /**
     * Get the npc entity type.
     */
    public EntityType getEntityType() {

        MobType type = _npc.getTrait(MobType.class);
        return type.getType();
    }

    /**
     * Determine if the npc is spawned.
     */
    public boolean isSpawned() {

        return _npc.isSpawned();
    }

    /**
     * Spawns the NPC. If the npc is already spawned,
     * teleports NPC to specified location.
     *
     * @param location  The spawn location.
     */
    public boolean spawn(Location location) {

        NPCSpawnEvent event = new NPCSpawnEvent(getArena(), this);

        getEventManager().call(event);

        if (event.isCancelled())
            return false;

        if (_npc.isSpawned()) {
            _npc.teleport(location, TeleportCause.PLUGIN);
            return true;
        }
        else if (_npc.spawn(location)) {

            NPCEntityRegistry.registerNPCEntity(getArena(), _npc.getEntity(), _npc);
            return true;
        }

        return false;
    }

    /**
     * Despawn the NPC using PLUGIN as the despawn reason.
     */
    public boolean despawn() {

        return despawn(DespawnReason.PLUGIN);
    }

    /**
     * Despawn the NPC using the specified reason.
     *
     * @param reason  The reason the NPC is being despawned.
     */
    public boolean despawn(DespawnReason reason) {

        if (!_npc.isSpawned())
            return false;

        NPCEntityRegistry.unregisterNPCEntity(_npc.getEntity());

        NPCDespawnEvent event = new NPCDespawnEvent(getArena(), this);

        getEventManager().call(event);

        return !event.isCancelled() && _npc.despawn(reason);
    }

    /**
     * Dispose the NPC and remove from
     * registry.
     */
    public void dispose() {

        _npc.destroy();

        Set<Class<?>> events = _registeredHandlers.keySet();

        for (Class<?> event : events) {

            List<EventHandler> handlers = _registeredHandlers.getValues(event);
            if (handlers == null)
                continue;

            for (EventHandler handler : handlers) {
                _arena.getEventManager().unregister(event, handler);
            }
        }

        _registeredHandlers.clear();

        getEventManager().dispose();
    }

    /**
     * Determine if the NPC is disposed.
     */
    public boolean isDisposed() {

        return _npc.getOwningRegistry() == null ||
               _npc.getOwningRegistry().getById(getId()) == null;
    }

    /**
     * Determine if the NPC is automatically disposed
     * when it dies.
     */
    public boolean isDisposedOnDeath() {

        return _isDisposedOnDeath;
    }

    /**
     * Set the NPC is disposed when it dies.
     *
     * @param isDisposed  True to dispose on death.
     */
    public void setDisposedOnDeath(boolean isDisposed) {

        _isDisposedOnDeath = isDisposed;
    }

    /**
     * Get the NPC's owning registry.
     */
    public NPCRegistry getRegistry() {

        return _npc.getOwningRegistry();
    }

    /**
     * Get the NPC's navigator.
     */
    public Navigator getNavigator() {

        return _npc.getNavigator();
    }

    /**
     * Get the NPC's default goal controller.
     */
    public GoalController getDefaultGoalController() {

        return _npc.getDefaultGoalController();
    }

    /**
     * Add a goal to the NPC.
     *
     * @param priority  The goal priority.
     * @param goal      The goal to add.
     */
    public void addGoal(int priority, Goal goal) {

        _npc.getDefaultGoalController().addGoal(goal, priority);
    }

    /**
     * Remove a goal from the NPC.
     *
     * @param goal  The goal to remove.
     */
    public void removeGoal(Goal goal) {

        _npc.getDefaultGoalController().removeGoal(goal);
    }

    /**
     * Get a list of NPC goals.
     */
    public List<Goal> getGoals() {

        List<Goal> goals = new ArrayList<Goal>(20);

        for (GoalEntry goal : _npc.getDefaultGoalController())
            goals.add(goal.getGoal());

        return goals;
    }

    /**
     * Determine if the NPC is invulnerable to harm.
     */
    public boolean isInvulnerable() {

        return _npc.isProtected();
    }

    /**
     * Set the NPC's invulnerability.
     *
     * @param isInvulnerable  True to make the NPC invulnerable.
     */
    public void setInvulnerable(boolean isInvulnerable) {

        _npc.setProtected(isInvulnerable);
    }

    /**
     * Determine if the NPC can fly.
     */
    public boolean isFlying() {

        return _npc.isFlyable();
    }

    /**
     * Set the NPC fly status.
     *
     * @param isFlying  True to make the NPC fly.
     */
    public void setFlying(boolean isFlying) {

        _npc.setFlyable(isFlying);
    }

    /**
     * Get NPC traits.
     */
    public Iterable<Trait> getTraits() {

        return _npc.getTraits();
    }

    /**
     * Determine if an NPC has the specified trait.
     *
     * @param traitClass  The trait class.
     */
    public boolean hasTrait(Class<? extends Trait> traitClass) {

        return _npc.hasTrait(traitClass);
    }

    /**
     * Get a trait instance from the NPC.
     *
     * @param traitClass  The trait type.
     * @param <T>         The trait type.
     */
    @Nullable
    public <T extends Trait> T getTrait(Class<T> traitClass) {

        return _npc.getTrait(traitClass);
    }

    /**
     * Remove a trait from the NPC.
     *
      * @param trait  The trait type.
     */
    public void removeTrait(Class<? extends Trait> trait) {

        _npc.removeTrait(trait);
    }

    /**
     * Add a handler to call when the NPC receives damage.
     *
     * @param handler  The handler.
     */
    public void on(String eventName, String priority, final NPCEventHandler handler) {
        PreCon.notNullOrEmpty(eventName);
        PreCon.notNullOrEmpty(priority);
        PreCon.notNull(handler);

        GenericsEventPriority eventPriority = GenericsEventPriority.NORMAL;

        try {
            eventPriority = GenericsEventPriority.valueOf(priority.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void call(Object event) {
                handler.call(event);
            }
        };

        Class<? extends AbstractNPCEvent> eventClass = _registeredEvents.get(eventName.toLowerCase());
        PreCon.notNull(eventClass);

        getEventManager().register(eventClass, eventPriority, eventHandler);

        _registeredHandlers.put(eventClass, eventHandler);
    }

    /**
     * Get the wrapped Citizens NPC object.
     */
    NPC getHandle() {

        return _npc;
    }

    public static interface NPCEventHandler {

        void call(Object event);
    }

}
