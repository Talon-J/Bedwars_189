package me.camm.productions.bedwars.Items.ItemDatabases;

public enum LorePhrases
{
    COST("Cost:"),
    SELL("Amount:");

    private final String phrase;
    LorePhrases(String phrase)
    {
        this.phrase = phrase;
    }

    public String getPhrase()
    {
        return phrase;
    }
}
