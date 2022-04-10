package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Util.Helpers.ChatSender;

/**
 * @author CAMM
 * This abstract class is used for events that occur in game
 */
public abstract class Action {
    protected ChatSender sender;


    public Action(){
        sender = ChatSender.getInstance();
    }

    public abstract void activate();
}
