package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader;

public class DragonSpawnAction extends GameActionPhysical {
    private final GameRunner runner;

    public DragonSpawnAction(GameRunner runner) {
        this.runner = runner;
        spent = false;
        header = ScoreBoardHeader.SUDDEN_DEATH_HEADER.getPhrase();
    }

    @Override
    public void activate() {
        if (spent)
            return;
        spent = true;
        runner.sendMessage("[DEBUG] Spawn dragons now!");
    }

    @Override
    String getHeader() {
        return header;
    }
}
