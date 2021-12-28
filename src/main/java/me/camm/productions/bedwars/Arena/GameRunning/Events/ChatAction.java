package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;

public class ChatAction implements IAction
{
    private final String text;
    private final GameRunner runner;
    private boolean spent;


    public ChatAction(String text, GameRunner runner) {
        this.runner = runner;
        this.text = text;
        spent = false;
    }

    @Override
    public void activate() {
        if (!spent)
        runner.sendMessage(text);
        spent = true;
    }
}
