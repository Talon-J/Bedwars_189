package me.camm.productions.bedwars.Arena.GameRunning.Commands;

public enum CommandKeyword
{
    SETUP("setup"),
    REGISTER("register"),
    SHOUT("shout"),
    UNREGISTER("unregister"),
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
