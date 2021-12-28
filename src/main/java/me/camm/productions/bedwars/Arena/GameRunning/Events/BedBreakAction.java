package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader;

import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.ALL_BEDS_DESTROYED;

public class BedBreakAction extends GameActionPhysical {

    private final GameRunner runner;

    public BedBreakAction(GameRunner runner) {
        this.runner = runner;
        header = ScoreBoardHeader.BED_GONE_HEADER.getPhrase();
        spent = false;
    }

    @Override
    public void activate() {
        if (spent)
            return;
        spent = true;

        Arena arena = runner.getArena();

        runner.sendMessage(GameEventText.DESTROY_BEDS.getText());
        arena.getTeams().forEach((string, battleTeam) -> {

            if (!battleTeam.doesBedExist())
                battleTeam.sendTeamTitle(ALL_BEDS_DESTROYED.getMessage(), "",10,40,10);  //say that all beds have been destroyed

            battleTeam.putOnLastStand();
        });

    }


    @Override
    String getHeader() {
        return header;
    }
}
