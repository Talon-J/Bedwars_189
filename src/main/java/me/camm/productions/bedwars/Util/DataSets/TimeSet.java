package me.camm.productions.bedwars.Util.DataSets;

import me.camm.productions.bedwars.Arena.Teams.BattleTeam;

public class TimeSet
{
    private final BattleTeam team;
    private final long millis;

    public TimeSet(BattleTeam team, long millis) {
        this.team = team;
        this.millis = millis;
    }

    public BattleTeam getTeam() {
        return team;
    }

    public long getMillis() {
        return millis;
    }
}
