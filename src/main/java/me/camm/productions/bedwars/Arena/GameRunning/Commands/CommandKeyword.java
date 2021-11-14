package me.camm.productions.bedwars.Arena.GameRunning.Commands;

public enum CommandKeyword
{
    SETUP("setup"),
    REGISTER("register"),
    START("start");

    private final String word;

    CommandKeyword(String word)
    {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
