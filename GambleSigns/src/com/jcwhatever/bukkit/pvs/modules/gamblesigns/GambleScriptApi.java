package com.jcwhatever.bukkit.pvs.modules.gamblesigns;

import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.ScriptApi;
import com.jcwhatever.bukkit.pvs.modules.gamblesigns.events.GambleTriggeredEvent;

import java.util.List;

public class GambleScriptApi extends ScriptApi implements GenericsEventListener {

    @Override
    public String getVariableName() {
        return "_gamble";
    }

    @Override
    protected IScriptApiObject onCreateApiObject(Arena arena, EvaluatedScript script) {

        ApiObject apiObject = new ApiObject();

        arena.getEventManager().register(apiObject);

        return apiObject;
    }

    public static class ApiObject implements IScriptApiObject, GenericsEventListener {

        private MultiValueMap<String, GambleHandler> _gambleHandlers = new MultiValueMap<>(25);

        public void addWinHandler(String eventName, GambleHandler handler) {
            _gambleHandlers.put(eventName, handler);
        }

        public void removeWinHandler(String eventName, GambleHandler handler) {
            _gambleHandlers.removeValue(eventName, handler);
        }

        @Override
        public void reset() {
            _gambleHandlers.clear();
        }

        @GenericsEventHandler
        private void onGambleTriggered(GambleTriggeredEvent event) {
            String eventName = event.getEventName();

            List<GambleHandler> handlers = _gambleHandlers.getValues(eventName);
            if (handlers == null)
                return;

            for (GambleHandler handler : handlers) {
                handler.onCall(event.getPlayer(), eventName, event.getSignContainer());
            }
        }
    }

    public static interface GambleHandler {

        public void onCall(ArenaPlayer signClicker, String eventName, SignContainer sign);

    }
}
