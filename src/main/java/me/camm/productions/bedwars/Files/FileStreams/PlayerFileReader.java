package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Arena.Players.Managers.HotbarManager;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Util.DataSets.ShopItemSet;
import me.camm.productions.bedwars.Util.Helpers.StringHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty.HOT_BAR_END;

public class PlayerFileReader extends StringHelper
{
    private final Plugin plugin;
    private final boolean isInflated;

    private final File inventoryFile;
    private final File barFile;


    public PlayerFileReader(Plugin plugin, Player player, boolean isInflated)
    {
        super(plugin);
        this.plugin = plugin;
        this.isInflated = isInflated;

        this.inventoryFile = new File(getInventoryPath(player));
        this.barFile = new File(getHotBarPath(player));
    }

    public HotbarManager readBarFile()
    {
        HotbarManager manager;

        try (BufferedReader reader = new BufferedReader(new FileReader(barFile)))
        {
            ArrayList<String> lines = new ArrayList<>();
            String current = reader.readLine();

            while (current!=null)
            {
                current = checkForComments(current);
                lines.add(current);
                current = reader.readLine();
            }
            reader.close();

            ItemCategory[] set = new ItemCategory[HOT_BAR_END.getValue()];

            int currentSlot = 0;
            for (String string: lines)
            {

               try {
                   string = string.trim();
                   ItemCategory currentCategory = ItemCategory.valueOf(string);
                   if (currentCategory==ItemCategory.NONE||currentCategory==ItemCategory.NAV) {
                       set[currentSlot] = null;
                       currentSlot++;
                       continue;
                   }

                   set[currentSlot] = currentCategory;
                   currentSlot++;

                   if (currentSlot==set.length-1)
                       break;
               }
               catch (RuntimeException e)
               {
                   set[currentSlot] = null;
                   currentSlot++;
               }
            }
            manager = new HotbarManager(set);
            return manager;
        }
        catch (IOException e)
        {
            manager = new HotbarManager();
            return manager;
        }
    }

    public PlayerInventoryManager readInvFile()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(inventoryFile)))
        {

            //deal with the possibility of restricted items in the manager.
            ArrayList<ShopItemSet> items = new ArrayList<>();
            String current = reader.readLine();


            while (current!=null)
            {
                //check for comments. returns null if entire line is a comment
                current = checkForComments(current);

                //if the entire line is a comment
                if (current==null) {
                   current = reader.readLine();
                    continue;
                }

                StringTokenizer tk = new StringTokenizer(current);
                String key;
                String slotNumber;

                //skip the key
                if (tk.hasMoreTokens())
                   key = tk.nextToken();
                else
                {
                    current = reader.readLine();
                    continue;
                }

                if (tk.hasMoreTokens())
                   slotNumber = tk.nextToken();
                else
                {
                    current = reader.readLine();
                    continue;
                }

               try
               {
                   int slot = Integer.parseInt(slotNumber);
                   ShopItem item = ShopItem.valueOf(key);
                   items.add(new ShopItemSet(item,slot));
               }
               catch (RuntimeException ignored)
               {

               }

                current = reader.readLine();
            }

            return new PlayerInventoryManager(items, isInflated);
        }
        catch (IOException e)
        {
            return new PlayerInventoryManager(isInflated);
        }

    }

}
