package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader;

public class GameEndAction extends GameActionPhysical
{
    private final GameRunner runner;

    public GameEndAction(GameRunner runner) {
        this.runner = runner;
        header = ScoreBoardHeader.GAME_END_HEADER.getPhrase();
        spent = false;
    }

    @Override
    public void activate() {
        if (spent)
            return;

        spent = true;
        runner.sendMessage(GameEventText.GAME_END_TEXT.getText());
        runner.endGame(null);
    }

    @Override
    String getHeader() {
        return header;
    }
}
