package me.camm.productions.bedwars.Files.FileCreators;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Util.Helpers.ChatSender;
import me.camm.productions.bedwars.Util.Helpers.StringHelper;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author CAMM
 * This class is used for creating player-specific data files
 */
public class PlayerFileCreator extends StringHelper
{
    private final BattlePlayer player;

    private final ChatSender sender;



    public PlayerFileCreator(BattlePlayer player, Arena arena)
    {
        super(arena.getPlugin());
        this.player = player;
        sender = ChatSender.getInstance();


    }

    //creates the player directory where their data is stored
     public void createDirectory()
     {
         //the folder where the data is stored.
         File playerDirectory = new File(getPlayerPath(player.getRawPlayer()));
        if (create(playerDirectory, true))
            sender.sendConsoleMessage("Completed file folder creation operations for player "+player.getRawPlayer().getName(),Level.INFO);
        else
            sender.sendConsoleMessage("Was unable to verify the file folder for player "+player.getRawPlayer().getName(),Level.WARNING);
     }

     //creates the inv configuration file
     public void createInventoryFile()
     {

         File inventoryFile = new File(getInventoryPath(player.getRawPlayer()));

         if (create(inventoryFile, false))
             sender.sendConsoleMessage("Completed inventory file creation operations for player "+player.getRawPlayer().getName(),Level.INFO);
         else
             sender.sendConsoleMessage(ChatColor.RED+"Was unable to verify the inventory file for player "+player.getRawPlayer().getName(),Level.WARNING);

     }

     //creates the hotbar config file
     public void createHotBarFile()
     {
         File managerFile = new File(getHotBarPath(player.getRawPlayer()));

        if (create(managerFile,false))
           sender.sendConsoleMessage("Completed hot bar file creation operations for player "+player.getRawPlayer().getName(),Level.INFO);
        else
            sender.sendConsoleMessage("Was unable to verify the inventory file for player "+player.getRawPlayer().getName(),Level.INFO);
     }


     //method for creating files
     private boolean create(File file, boolean isDir)
     {
         if (!file.exists())
         {
             try {
                 if (isDir)
                     file.mkdir();
                file.createNewFile();
             }
             catch (IOException e)
             {
                 return false;
             }


         }
         return true;
     }

}
