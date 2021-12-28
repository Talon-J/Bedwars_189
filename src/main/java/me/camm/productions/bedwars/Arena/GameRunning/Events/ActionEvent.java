package me.camm.productions.bedwars.Arena.GameRunning.Events;

public class ActionEvent {
    private final int time;
    private final IAction action;

    public ActionEvent(int time, IAction action) {
        this.action = action;
        this.time = time;
    }

    public void activateEvent() {
            action.activate();
    }

    public int getTime() {
        return time;
    }

    public String getHeader(){
        if (action instanceof GameActionPhysical)
            return ((GameActionPhysical)action).getHeader();
        return null;
    }

    public int getActivationTime(){
        return time;
    }

}
