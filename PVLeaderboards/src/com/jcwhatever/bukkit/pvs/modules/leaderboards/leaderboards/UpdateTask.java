package com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards;

import com.jcwhatever.bukkit.generic.performance.queued.QueueTask;
import com.jcwhatever.bukkit.generic.performance.queued.TaskConcurrency;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;

public class UpdateTask extends QueueTask {

    private final Leaderboard _leaderboard;

    public UpdateTask(Leaderboard leaderboard) {
        super(PVStarAPI.getPlugin(), TaskConcurrency.MAIN_THREAD);

        _leaderboard = leaderboard;
    }

    @Override
    protected void onRun() {
        _leaderboard.update();
        complete();
    }
}
