package me.camm.productions.bedwars.Files.FileCreators;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Util.Helpers.StringToolBox;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PlayerFileCreator extends StringToolBox //for reading inv files and creating a hotbar mngr
{
    private BattlePlayer player;
    private Arena arena;
    private File inventoryFile;
    private File managerFile;
    private File playerDirectory;  //the folder where the data is stored.
    private String mainPath;



    public PlayerFileCreator(BattlePlayer player, Arena arena)
    {
        super(arena.getPlugin());
        this.player = player;
        this.arena = arena;
        this.mainPath = mainPath;


    }

     public void createDirectory()
     {
         playerDirectory = new File(getPlayerPath(player.getRawPlayer()));
        if (create(playerDirectory, true))
            arena.getPlugin().getLogger().log(Level.INFO,"Completed file folder creation operations for player "+player.getRawPlayer().getName());
        else
            arena.getPlugin().getLogger().log(Level.WARNING,"Was unable to verify the file folder for player "+player.getRawPlayer().getName());
     }

     public void createInventoryFile()
     {

         inventoryFile = new File(getInventoryPath(player.getRawPlayer()));

         if (create(inventoryFile, false))
             arena.getPlugin().getLogger().log(Level.INFO,"Completed inventory file creation operations for player "+player.getRawPlayer().getName());
         else
             arena.getPlugin().getLogger().log(Level.WARNING,ChatColor.RED+"Was unable to verify the inventory file for player "+player.getRawPlayer().getName());

     }

     public void createHotBarFile()
     {
        managerFile = new File(getHotBarPath(player.getRawPlayer()));

        if (create(managerFile,false))
            arena.getPlugin().getLogger().log(Level.INFO,"Completed hotbar file creation operations for player "+player.getRawPlayer().getName());
        else
            arena.getPlugin().getLogger().log(Level.WARNING,"Was unable to verify the inventory file for player "+player.getRawPlayer().getName());
     }

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
