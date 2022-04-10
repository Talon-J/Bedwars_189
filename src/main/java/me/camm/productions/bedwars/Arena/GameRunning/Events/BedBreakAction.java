package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader;


import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.ALL_BEDS_DESTROYED;


/**
 * @author CAMM
 * This class models the action of breaking the bed at a given time
 */
public class BedBreakAction extends GameActionPhysical {

    private final GameRunner runner;

    //constructor
    public BedBreakAction(GameRunner runner) {
        super();
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

        sender.sendMessage(GameEventText.DESTROY_BEDS.getText());
        arena.getTeams().forEach((string, battleTeam) -> {

            if (!battleTeam.doesBedExist())
                battleTeam.sendTeamTitle(ALL_BEDS_DESTROYED.getMessage(), "",10,40,10);  //say that all beds have been destroyed
                //10, 40, and 10 are just arbitrary values. We could have them as any positive int. It's just that these values
            //work out well

            battleTeam.putOnLastStand();
        });

    }


    @Override
    String getHeader() {
        return header;
    }
}
