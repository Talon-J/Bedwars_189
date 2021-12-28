package me.camm.productions.bedwars.Arena.GameRunning.Events;

public abstract class GameActionPhysical implements IAction {
    protected boolean spent;
    protected String header;
    abstract String getHeader();
    //the header is the string to show on the scoreboard as the time approaches activation time.
}
