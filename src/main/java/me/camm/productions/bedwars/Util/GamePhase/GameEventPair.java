package me.camm.productions.bedwars.Util.GamePhase;

import static me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeaders.*;
import static me.camm.productions.bedwars.Util.GamePhase.EventTime.*;
import static me.camm.productions.bedwars.Util.GamePhase.GameEventText.*;

public enum GameEventPair
{
    //text action: Message or text to display.
    //Time pair: The time when the event occurs.
    //Scoreboardheader: The header to put on the scoreboard.
    //skip: If the eventpair doesn't change the scoreboard

    DIAMOND_TWO(DIAMOND_UPGRADE_TWO,UPGRADE_DIAMONDS,EMERALD_TWO_HEADER.getPhrase(),false),
    EMERALD_TWO(EMERALD_UPGRADE_TWO,UPGRADE_EMERALDS,DIAMOND_THREE_HEADER.getPhrase(),false),

    DIAMOND_THREE(DIAMOND_UPGRADE_THREE,UPGRADE_DIAMONDS,EMERALD_THREE_HEADER.getPhrase(),false),
    EMERALD_THREE(EMERALD_UPGRADE_THREE,UPGRADE_EMERALDS, BED_GONE_HEADER.getPhrase(),false),

    BED_WARNING(BED_WARNING_TIME, BED_DESTROY_SCHEDULED_WARNING,BED_GONE_HEADER.getPhrase(),true),
    BED_DESTROY(BED_DESTROY_TIME,DESTROY_BEDS, SUDDEN_DEATH_HEADER.getPhrase(),false),

    DRAGON_WARNING(DRAGON_WARNING_TIME, DRAGON_SPAWN_SCHEDULED_WARNING, SUDDEN_DEATH_HEADER.getPhrase(),true),
    DRAGON_SPAWN(DRAGON_SPAWN_TIME,SPAWN_DRAGONS, GAME_END_HEADER.getPhrase(),false),

    GAME_END_WARNING(EventTime.GAME_END_WARNING, GAME_END_SCHEDULED_WARNING, GAME_END_HEADER.getPhrase(),true),
    GAME_END_DECLARE(TOTAL_GAME_TIME,GAME_END_TEXT, GAME_END_HEADER.getPhrase(),false);

    private final EventTime timePair;
    private final GameEventText textAction;
    private final String scoreBoardHeader;
    private final boolean skip;

    GameEventPair(EventTime timePair, GameEventText textAction, String scoreBoardHeader,boolean skip) {
        this.timePair = timePair;
        this.textAction = textAction;
        this.scoreBoardHeader = scoreBoardHeader;
        this.skip = skip;
    }

    public EventTime getTimePair() {
        return timePair;
    }

    public String getScoreBoardHeader() {
        return scoreBoardHeader;
    }

    public GameEventText getTextAction() {
        return textAction;
    }

    public boolean isSkip() {
        return skip;
    }
}
