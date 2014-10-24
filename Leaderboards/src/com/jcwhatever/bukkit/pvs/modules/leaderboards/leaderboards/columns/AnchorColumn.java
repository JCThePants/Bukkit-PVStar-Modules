package com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns;

import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Utils;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.block.Sign;

import java.util.UUID;

public class AnchorColumn extends AbstractColumn {

    private final Sign _anchorSign;
    private final String[] _lineFormats;

    public AnchorColumn(Leaderboard leaderboard, Sign anchorSign, String[] lineFormats) {
        super(leaderboard, anchorSign);
        _anchorSign = anchorSign;

        _lineFormats = lineFormats;
    }

    public Sign getAnchorSign() {
        return getHeaderSign();
    }

    @Override
    public Sign getHeaderSign() {
        return _anchorSign;
    }

    @Override
    public double getPlayerStatValue(String playerId) {
        return 0;
    }

    @Override
    protected String getPlayerStatDisplay(int signLine, String playerId) {
        PreCon.notNullOrEmpty(playerId);

        UUID playerUniqueId;
        PreCon.isValid((playerUniqueId = Utils.getId(playerId)) != null);

        String format = _lineFormats[signLine];

        return (format != null ? format : "") + PlayerHelper.getPlayerName(playerUniqueId);
    }
}
