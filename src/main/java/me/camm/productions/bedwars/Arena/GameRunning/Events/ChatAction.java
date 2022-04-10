package me.camm.productions.bedwars.Arena.GameRunning.Events;


/**
 * @author CAMM
 * This class models non physical events (These events only have to do with
 * scoreboards or the chat)
 */
public class ChatAction extends Action
{
    private final String text;

    private boolean spent;


    public ChatAction(String text) {
        this.text = text;
        spent = false;
    }

    @Override
    public void activate() {
        if (!spent)
        sender.sendMessage(text);
        spent = true;
    }
}
