package me.camm.productions.bedwars.Util.Helpers;

import me.camm.productions.bedwars.Files.FileKeywords.DataSeparatorKeys;
import me.camm.productions.bedwars.Files.FileKeywords.FilePaths;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class StringHelper
{

    private final Plugin plugin;
    private final String deliminator;

    public StringHelper(Plugin plugin)
    {
        this.plugin = plugin;
        this.deliminator = getSlashes();
    }

    //gets the string with comments (#abc) accounted for
    //returns null if the entire string is a comment
    public String checkForComments(String original)
    {
        if (original == null)
            return null;

        int index = original.indexOf(DataSeparatorKeys.COMMENT.getKey());
        if (index==-1)
            return original;
        original = (original.substring(0,index)).trim();
        if (original.length()==0)
            return null;
        return original;
    }


    //returns the section of a string after the ":" keyword, trimmed.
    //returns null if: ":" dne or the number of ":" > 1
    public String getInfoSection(String original)
    {
        if (original == null)
            return null;


        char comparator = DataSeparatorKeys.DECLARATION.getKey().charAt(0);
        int occurrences = 0;
        for (int position=0;position<original.length();position++)
        {
            if (original.charAt(position)==comparator)
                occurrences++;
        }
        if (occurrences!=1)
            return null;

        int index = original.indexOf(comparator);
        return (original.substring(index+1)).trim();
    }

    //for the reading of the inv and hotbar files, b/c 0 does not mean selected
    public Double getNumber(String original)
    {
        original = getInfoSection(original);
        return toNullableNumber(original);
    }

    private @Nullable Double toNullableNumber(String processed)
    {
        try
        {
            return Double.parseDouble(processed);
        }
        catch (NumberFormatException | NullPointerException e)
        {
            printParseError(processed);
            return null;
        }
    }

    private @Nullable Double toNullableNumber(String processed, String context){
        try
        {
            return Double.parseDouble(processed);
        }
        catch (RuntimeException e){
            printParseError(processed, context);
            return null;
        }
    }


    //returns the part of the string before the ":".
    //Returns null if ":" dne or if # of ":" > 1
    public String getKey(String commentChecked)
    {
        if (commentChecked==null)
            return null;

        int occurrences = 0;
      for (int position=0;position<commentChecked.length();position++)
      {
          if (commentChecked.charAt(position)== DataSeparatorKeys.DECLARATION.getKeyAsChar())
               occurrences++;
      }
      if (occurrences>1||occurrences==0)
          return null;

     return (commentChecked.substring(0,commentChecked.indexOf(DataSeparatorKeys.DECLARATION.getKeyAsChar()))).trim();
    }




    //takes a string, returns a numerical array depending on the context
    //if runs into error --> returns null
    public double[] getNumbers(String original)
    {
        original = getInfoSection(original);
        if (original==null) {
            return null;
        }
        ArrayList<Double> numbers = new ArrayList<>();
        StringTokenizer separatorCutter = new StringTokenizer(original, DataSeparatorKeys.SEPARATOR.getKey());

        while (separatorCutter.hasMoreTokens())
        {
            String value = separatorCutter.nextToken();
            StringTokenizer commaCutter = new StringTokenizer(value, DataSeparatorKeys.COMMA.getKey());
            while (commaCutter.hasMoreTokens())
                numbers.add(toNumber(commaCutter.nextToken()));
        }

        double[] processed = new double[numbers.size()];
        for (int slot=0;slot<numbers.size();slot++) {
            processed[slot] = numbers.get(slot);
        }
        return processed;

    }


    //takes a double array and converts it into an int array
    public int[] doubleToIntArray(double @NotNull [] values)
    {
        int[] processed = new int[values.length];
        for (int slot=0;slot<values.length;slot++) {
            processed[slot] = (int) values[slot];
        }
        return processed;
    }

    //changes a string to a double. Returns 0 if fails
    public double toNumber(String format)
    {
        try
        {
            return Double.parseDouble(format);
        }
        catch (NumberFormatException e)
        {
            printParseError(format);
            return 0;
        }
    }

    //gets the plugin folder of the server.
    public String getServerFolder()
    {
        return plugin.getDataFolder().getParentFile().getAbsolutePath();
    }

    public String getMainFolderPath()
    {
        return getServerFolder()+deliminator+ FilePaths.MAIN.getValue();
    }
    //gets the path of the world txt file
    public String getWorldPath()
    {
       return getMainFolderPath()+deliminator+ FilePaths.WORLD.getValue();
    }

    public String getTeamPath()
    {
        return getMainFolderPath()+deliminator+ FilePaths.TEAMS.getValue();
    }

    public String getCreditsPath()
    {
        return getMainFolderPath()+deliminator+ FilePaths.CREDITS.getValue();
    }

    public String getInstructionsPath()
    {
        return getMainFolderPath()+deliminator+ FilePaths.INSTRUCTIONS.getValue();
    }

    public String getPlayerFolderPath()
    {
        return getMainFolderPath()+deliminator+ FilePaths.PLAYER.getValue();
    }

    public String getHotBarPath(Player player)
    {
        return getPlayerPath(player)+deliminator+ FilePaths.HOTBAR.getValue();
    }

    //gets the specified player folder
    public String getPlayerPath(Player player)
    {
        String uuid = player.getUniqueId().toString();
        return getPlayerFolderPath()+deliminator+uuid;
    }

    public String getInventoryPath(Player player)
    {
        return getPlayerPath(player)+deliminator+ FilePaths.INVENTORY.getValue();
    }


    private String getSlashes()
    {
        String path = getServerFolder();
        if (path.contains(FilePaths.FORWARD_SLASH.getValue()))
            return FilePaths.FORWARD_SLASH.getValue();
        else
            return FilePaths.BACKSLASH.getValue();
    }


    public void printParseError(String value)
    {
       Logger logger =  plugin.getLogger();
       logger.warning(ChatColor.RED+"[WARNING] Attempted to parse "+value+" to a number. Defaulted to 0.");
    }

    public void printParseError(String value, String context)
    {
        Logger logger =  plugin.getLogger();
        logger.warning(ChatColor.RED+"================================");
        logger.warning(ChatColor.RED+"[WARNING] Attempted to parse "+value+" to a number. Defaulted to 0.");
        logger.warning(ChatColor.RED+"[WARNING] Context: "+context);
        logger.warning(ChatColor.RED+"================================");
    }
}
