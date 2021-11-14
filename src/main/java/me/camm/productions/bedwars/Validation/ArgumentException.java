package me.camm.productions.bedwars.Validation;


import org.bukkit.ChatColor;

//Thrown when a string in a file is processed for data and no corresponding
//value can be found.
public class ArgumentException extends Exception
{
    private final String fileOrigin;
    private final String attemptedParse;

    public ArgumentException(String fileOrigin, String attemptedParse)
    {
        this.fileOrigin = fileOrigin;
        this.attemptedParse = attemptedParse;

    }

    @Override
    public String toString()
    {
        return ChatColor.RED+"BedWarsParameterException: Attempted to process the value: \n"+attemptedParse+ChatColor.RED+"\n " +
                ChatColor.RED+"from the file path: \n"+fileOrigin+", but found not corresponding value.";
    }
}

