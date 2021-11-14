package me.camm.productions.bedwars.Util.Helpers;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.IPlayerUtil;
import me.camm.productions.bedwars.Arena.Players.Managers.HotbarManager;
import me.camm.productions.bedwars.Items.ItemDatabases.*;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import me.camm.productions.bedwars.Util.PacketSound;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.camm.productions.bedwars.Items.ItemDatabases.BattleEnchantment.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.GameItem.SHEARS;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_END;
import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;
import static me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.LorePhrases.COST;
import static me.camm.productions.bedwars.Items.ItemDatabases.LorePhrases.SELL;
import static me.camm.productions.bedwars.Items.ItemDatabases.PotionData.*;
import static org.bukkit.Material.*;


/*
TODO: Add code to create the dream defender spawn egg formal [DONE]
 */
public class ItemHelper implements IPlayerUtil
{
  private static final ArrayList<GameItem> restrictedFileItems;
  private static final ArrayList<GameItem> inventoryItems;
  private static final ArrayList<TieredItem> tieredItemList;
  private static final ArrayList<TieredItem> lowestTiers;


 static {
         TieredItem[] tieredItems = TieredItem.values();
         GameItem[] inventory = GameItem.values();

         restrictedFileItems = new ArrayList<>();
         inventoryItems = new ArrayList<>();
         tieredItemList = new ArrayList<>();
         lowestTiers = new ArrayList<>();

         for (TieredItem item : tieredItems) {
             tieredItemList.add(item);

             if (item.getIndex() == 0)
                 lowestTiers.add(item);
         }

         inventoryItems.addAll(Arrays.asList(inventory));

         for (GameItem item : inventory) {
             if (item.category != NAV)
                 continue;
             restrictedFileItems.add(item);
         }

         for (TieredItem item : tieredItems) {
             if (item.getIsFileValid())
                 continue;

             restrictedFileItems.add(item.getItem());
         }
 }


    public static boolean isToolBetterThanPresent(TieredItem item, BattlePlayer player)
    {
        TieredCategory category = item.getCategory();
        TieredItem currentItem = null;
        switch (category) {
            case AXE:
                currentItem = player.getAxe();
                break;

            case PICK:
                currentItem = player.getPick();
                break;
        }
        if (currentItem == null)
        return true;
        else
            return item.getIndex() > currentItem.getIndex();

    }



    // sells an item. accounts for team enchants and inflation. also accounts for if the item is a placeholder.
    //unfinished. requires code for dream defender. ***CODE ADDED. PLEASE TEST.
    public static void sellItem(GameItem item, BattlePlayer player, boolean isInflated)
    {

        if (item==null)
            return;

        ItemCategory category = item.category;

        if (isPlaceHolder(category))
            return;

        if (category==ARMOR)
        {
            if (!isArmorBetter(getArmor(player.getRawPlayer()),item))
                return;
        }

        TieredItem possible = isTieredItem(item);

        HotbarManager manager = player.getBarManager();
        if (manager==null)
            return;

        if (possible != null)
        {
            if (!isToolBetterThanPresent(possible,player))
            {
                return;
            }
        }

        if (item==SHEARS && player.getShears() != null)
            return;


        if (!didPay(player,item,isInflated))
            return;


        ItemStack bought;

        switch (category)
        {
            case ARMOR:
            {
                ItemStack[] newArmor = inventoryItemToArmor(item, player);
                BattleEnchantment enchantment = player.getTeam().getArmorEnchant();

                if (newArmor==null)
                    return;

                if (enchantment!=null)
                {
                    for (int slot=0;slot< newArmor.length;slot++)
                        newArmor[slot] = enchant(newArmor[slot],enchantment);
                }

                player.setPurchasedArmor(possible == null ? player.getArmor() : possible);
                setArmor(newArmor, player.getRawPlayer());

            }
            break;

            //set(ItemStack item, ItemCategory category, Player player)
            case MELEE:
            {
                bought = toSoldItem(item,player);
                if (bought == null)
                    return;

                if (item== GameItem.STICK)
                {
                    enchant(bought, Enchantment.KNOCKBACK, 1);
                    manager.set(bought,category,player.getRawPlayer());
                   return;
                }
                BattleEnchantment enchantment = player.getTeam().getMeleeEnchant();
                if (enchantment==null)
                    manager.set(bought,category,player.getRawPlayer());
                else
                    manager.set(enchant(bought,enchantment),category,player.getRawPlayer());
            }
            break;

            default: {


                if (possible != null)
                {
                    TieredCategory toolCategory = possible.getCategory();

                    switch (toolCategory) {
                        case PICK:
                            player.sendMessage("[DEBUG]: SET PICK");
                            player.setPick(possible);
                            break;

                        case AXE:
                            player.sendMessage("[DEBUG]: SET AXE");
                            player.setAxe(possible);
                    }


                }

                if (item==SHEARS) {
                    player.sendMessage("[DEBUG]: SET SHEARS");
                    player.setShears();
                }

                manager.set(toSoldItem(item, player), category, player.getRawPlayer());
               }



            }
        }


    //takes an inventory item and returns the item that is actually sold
    //Does not account for armor.
    //can return null
    @SuppressWarnings("deprecation")
    public static ItemStack toSoldItem(GameItem item, BattlePlayer player)
    {
        if (item == null)
            return null;

        ItemStack stack = null;

        ItemCategory category = item.category;

        switch (category)
        {
            case ARMOR:
            case NONE:
            case NAV:
            case SEPARATOR:  //These invItems aren't placed into the player inventory, but instead do stuff important.
                break;

            case POTION:
                stack = getPotion(item);
                break;

            case RANGED:
                stack = toRangedItem(item);
                break;

            case BLOCK:
                byte color = (byte)player.getTeam().getColor().getValue();
                stack = addBlockColor(color,item);
                break;

            default: {
                if (item== GameItem.DREAM_DEFENDER)
                    stack = new ItemStack(item.sellMaterial,1,EntityType.IRON_GOLEM.getTypeId());
                    else
                stack = new ItemStack(item.sellMaterial, item.sellAmount);
            }
        }

        if (stack!=null&&(item.category==TOOLS||item.category==MELEE))
       stack = hideFlags(setUnbreakable(stack));

       if (item.keepName)
          return addName(stack,item);
       return stack;
    }




    //creates the item that is to be displayed in the shop, with price depending on player number
    @SuppressWarnings("deprecation")
    public ItemStack toDisplayItem(GameItem item, boolean isInflated)
    {
        ItemCategory category = item.category;

        ItemStack stack;

        switch (category)
        {
            case POTION:
                stack = addLore(addName(getPotion(item),item),item,isInflated);
                break;

            case NAV:
                stack = addName(new ItemStack(item.sellMaterial,item.sellAmount),item);
                break;

            case NONE:
            case SEPARATOR:
                stack =  addName(createGlassPane(category),item);
                break;

            default: {
               if (item== GameItem.DREAM_DEFENDER)
                   stack = addLore(addName(new ItemStack(item.sellMaterial,1, EntityType.IRON_GOLEM.getTypeId()),item),item,isInflated);
                else
                stack = addLore(addName(new ItemStack(item.sellMaterial, item.sellAmount), item), item, isInflated);
            }
        }
      return stack;
    }


    //creates an item based on the inv.item provided. returns null otherwise.
    //accounts for enchants.
    public static ItemStack toRangedItem(GameItem item)
    {
        ItemStack stack;
        if (item.category!=RANGED)
            return null;

        switch (item)
        {
            case BOW:
                stack = hideFlags(setUnbreakable(new ItemStack(item.sellMaterial,item.sellAmount)));
                break;

            case PUNCH_BOW:
                stack = enchant(enchant(hideFlags(setUnbreakable(new ItemStack(item.sellMaterial, item.sellAmount))),PUNCH_ONE),POWER_ONE);
                break;

            case POW_BOW:
                stack = enchant(hideFlags(setUnbreakable(new ItemStack(item.sellMaterial,item.sellAmount))),POWER_ONE);
                break;

            default:
                stack = new ItemStack(item.sellMaterial,item.sellAmount);
        }
        return stack;
    }

    //adds color to blocks if they can be colored.
    public static ItemStack addBlockColor(byte color, GameItem item)
    {
        ItemStack stack;

        switch (item)
        {
            case WOOL:
            case STAINED_GLASS:
            case HARDENED_CLAY:
                stack = new ItemStack(item.sellMaterial,item.sellAmount,color);
                break;

            default:
                stack = new ItemStack(item.sellMaterial, item.sellAmount);

        }
        return stack;

    }



    //creates a glass pane depending on category. returns null otherwise.
    @SuppressWarnings("deprecation")
    public static ItemStack createGlassPane(ItemCategory category)
    {

        if (category==NONE)
        {
            return new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.RED.getData());
        }
        else if (category==SEPARATOR)
        {
            return new ItemStack(Material.STAINED_GLASS_PANE,1,DyeColor.BLACK.getData());
        }
        else
             return null;
    }

    //returns colored armor along with any enchantments a battle team has. does not set armor for the player
    public static ItemStack[] inventoryItemToArmor(GameItem item, BattlePlayer player)
    {
        if (item.category!=ARMOR)
            return null;

        Color color = player.getTeam().getColor().getColor();

        ItemStack head;
        ItemStack chest;
        ItemStack legs;
        ItemStack boots;


        head = hideFlags(enchant(setColor(setUnbreakable(new ItemStack(Material.LEATHER_HELMET)),color),AQUA));
        chest = hideFlags(setColor(setUnbreakable(new ItemStack(Material.LEATHER_CHESTPLATE)), color));


        //set the armor to unbreakable first before adding enchants or color.

        switch (item)
        {
            case LEATHER_ARMOR:
                legs = hideFlags(setColor(setUnbreakable(new ItemStack(Material.LEATHER_LEGGINGS)), color));
                boots = hideFlags(setColor(setUnbreakable(new ItemStack(Material.LEATHER_BOOTS)), color));
                break;

            case CHAIN_MAIL:
                legs = hideFlags(setUnbreakable(new ItemStack(Material.CHAINMAIL_LEGGINGS)));
                boots = hideFlags(setUnbreakable(new ItemStack(Material.CHAINMAIL_BOOTS)));
                break;

            case IRON_ARMOR:
                legs  = hideFlags(setUnbreakable(new ItemStack(Material.IRON_LEGGINGS)));
                boots = hideFlags(setUnbreakable(new ItemStack(Material.IRON_BOOTS)));
                break;

            //diamond armor
            default:
                legs = hideFlags(setUnbreakable(new ItemStack(Material.DIAMOND_LEGGINGS)));
                boots = hideFlags(setUnbreakable(new ItemStack(Material.DIAMOND_BOOTS)));
        }


        return addArmorEnchants(orderArmor(head,chest,legs,boots),player.getTeam().getArmorEnchant());
    }


    public static boolean isArmor(Material mat)
    {
        switch (mat)
        {
            case LEATHER_BOOTS:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:

            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:

            case IRON_BOOTS:
            case IRON_CHESTPLATE:
            case IRON_HELMET:
            case IRON_LEGGINGS:

            case DIAMOND_BOOTS:
            case DIAMOND_LEGGINGS:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_HELMET:

            case GOLD_BOOTS:
            case GOLD_CHESTPLATE:
            case GOLD_HELMET:
            case GOLD_LEGGINGS:
                return true;
        }
        return false;
    }



    public static ItemStack getPotion(GameItem item)
    {
        if (item.category!=ItemCategory.POTION)
            return null;

        Potion potion;
        ItemStack stack;
        switch (item)
        {
            case INVIS_POT: {
                potion = new Potion(PotionType.INVISIBILITY);
                stack = potion.toItemStack(item.sellAmount);
                PotionMeta meta = (PotionMeta) stack.getItemMeta();
                meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, INVIS_DURATION.getValue(), INVIS_LEVEL.getValue(), true), true);
                stack.setItemMeta(meta);
            }
            break;

            case JUMP_POT:
            {
                potion = new Potion(PotionType.JUMP);
                stack = potion.toItemStack(item.sellAmount);
                PotionMeta meta = (PotionMeta)stack.getItemMeta();
                meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, JUMP_DURATION.getValue(), JUMP_LEVEL.getValue(),true),true);
                stack.setItemMeta(meta);
            }
            break;

            default:
            {
                potion = new Potion(PotionType.FIRE_RESISTANCE);
                stack = potion.toItemStack(item.sellAmount);
                PotionMeta meta = (PotionMeta)stack.getItemMeta();
                meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_DURATION.getValue(), SPEED_LEVEL.getValue(),true),true);
                stack.setItemMeta(meta);
            }

        }
        return stack;
    }

    /*
    Removes an item from the list if it shouldn't be contained in the file, or if the slot value is invalid.
    @param original
     */
    public static ArrayList<ItemSet> filter(ArrayList<ItemSet> original)
    {
        original.removeIf(item -> restrictedFileItems.contains(item.getItem()));
        original.removeIf(item -> !isInQuickBuyRange(item.getSlot()));
        original.removeIf(Objects::isNull);
        return original;
    }



    public static boolean isInQuickBuyRange(int slot)
    {
        return slot>=QUICK_INV_BORDER_START.getValue()&&slot<= QUICK_INV_BORDER_END.getValue();
    }

    public ItemStack transferEnchantments(ItemStack from, ItemStack to)
    {
        if (isItemInvalid(from)||isItemInvalid(to))
            return to;
        to.addUnsafeEnchantments(from.getEnchantments());
        return to;
    }



    public GameItem getInventoryItem(String name)
    {
        for (GameItem item: inventoryItems)
        {
            if (item.name.equalsIgnoreCase(name))
                return item;
        }
        return null;
    }

    //adds a specific enchantment to all items in the array
    public static ItemStack[] addArmorEnchants(ItemStack[] values, BattleEnchantment enchantment)
    {
        for (int slot=0;slot<values.length;slot++)
        {
            if (isItemInvalid(values[slot]))
                continue;
            values[slot] = enchant(values[slot],enchantment);
        }
        return values;
    }


    public static boolean isPlaceHolder(ItemCategory category)
    {
        return category==NONE||category==NAV||category==SEPARATOR;
    }

    public static boolean isItemInvalid(ItemStack item)
    {
        return item==null||item.getItemMeta()==null;
    }



    //adds lore for cost and sell amount to an item
    public static ItemStack addLore(ItemStack item, GameItem id, boolean isInflated)
    {
        if (isItemInvalid(item))
            return item;

        if (isPlaceHolder(id.category))
            return item;

        List<String> lore = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();

        lore.add(ChatColor.GOLD+COST.getPhrase()+" "+ (isInflated?id.inflatedPrice:id.cost)+" "+getPriceName(id));
        lore.add(ChatColor.AQUA+SELL.getPhrase()+" "+id.sellAmount);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    /*
    Returns true if the item is not allowed to appear in configuration files, otherwise returns false.
     */
    public static boolean isFileRestricted(GameItem item)
    {
       TieredItem tier = isTieredItem(item);
       if (tier==null)
           return false;
      return !tier.getIsFileValid();
    }

    public static ItemStack addName(ItemStack item, GameItem id)
    {
        if (isItemInvalid(item))
            return item;

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(id.name);
        item.setItemMeta(meta);
        return item;
    }


    public static TieredItem isTieredItem(GameItem item)
    {
        for (TieredItem tier: tieredItemList)
        {
            if (tier.getItem()==item)
                return tier;
        }
        return null;
    }


    public static String getPriceName(GameItem item)
    {
       return getPriceName(item.costMaterial);
    }

    public static String getPriceName(Material mat)
    {
        String name;
        switch (mat)
        {
            case IRON_INGOT:
                name = "Iron Ingot";
                break;

            case GOLD_INGOT:
                name = "Gold Ingot";
                break;

            case DIAMOND:
                name = "Diamond";
                break;

            case EMERALD:
                name = "Emerald";
                break;

            default:
                name = "Umm... Something... I dunno :/";
        }
        return name;
    }


    public static net.minecraft.server.v1_8_R3.ItemStack toNMSItem(ItemStack item)
    {
        return CraftItemStack.asNMSCopy(item);
    }

    public static ItemStack toBukkitItem(net.minecraft.server.v1_8_R3.ItemStack item)
    {
        return CraftItemStack.asCraftMirror(item);
    }

    public static boolean isArmorBetter(GameItem current, GameItem isBetter)
    {
       TieredItem currentArmor = isTieredItem(current);
       TieredItem nextArmor = isTieredItem(isBetter);

       if (currentArmor==null||nextArmor==null)
           return false;
       return isArmorBetter(currentArmor,nextArmor);
    }


    public static boolean isArmorBetter(TieredItem current, TieredItem isBetter)
    {
        if (current.getCategory()!=isBetter.getCategory())
            return false;
        return isBetter.getIndex()>current.getIndex();
    }


    public static GameItem getArmor(Player player)
    {
        StringBuilder complexity = new StringBuilder();
        ItemStack[] items = player.getInventory().getArmorContents();
        for (ItemStack item: items)
        {
            if (isItemInvalid(item))
                continue;

            Material mat = item.getType();

            if (isLeather(mat)) {
                complexity.append("l");
                continue;
            }

            if (isChain(mat)) {
                complexity.append("c");
                continue;
            }

            if (isIron(mat)) {
                complexity.append("i");
                continue;
            }

            if (isDiamond(mat))
                complexity.append("d");
        }
        return analyzeComplexity(complexity.toString());
    }

    private static GameItem analyzeComplexity(String string)
    {
        if (string.contains("d"))
            return GameItem.DIAMOND_ARMOR;
        else if (string.contains("i"))
            return GameItem.IRON_ARMOR;
        else if (string.contains("c"))
            return GameItem.CHAIN_MAIL;
        else
            return GameItem.LEATHER_ARMOR;
    }

    private static boolean isLeather(Material mat)
    {
       switch (mat)
       {
           case LEATHER_BOOTS:
           case LEATHER_HELMET:
           case LEATHER_LEGGINGS:
           case LEATHER_CHESTPLATE:
               return true;
       }
       return false;
    }

    private static boolean isIron(Material mat)
    {
        switch (mat)
        {
            case IRON_BOOTS:
            case IRON_LEGGINGS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
                return true;
        }
        return false;
    }

    private static boolean isChain(Material mat)
    {
        switch (mat)
        {
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_CHESTPLATE:
                return true;
        }
        return false;
    }

    private static boolean isDiamond(Material mat)
    {
        switch (mat)
        {
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return true;
        }
        return false;
    }


    public static ItemStack[] orderArmor(ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots)
    {
        return new ItemStack[] {boots, legs, chest, head};
    }

    public static boolean isCurrencyItem(ItemStack stack)
    {
        if (stack == null)
            return false;

        Material mat = stack.getType();
        return mat == IRON_INGOT || mat == GOLD_INGOT || mat == EMERALD || mat == DIAMOND;
    }

    public static void setArmor(ItemStack[] items, Player player)
    {
       player.getInventory().setArmorContents(items);
    }

    public TieredItem getNextTier(TieredItem current)
    {
        for (TieredItem item: tieredItemList)
        {
            if (item.getCategory()==current.getCategory()&&item.getIndex()==(current.getIndex()+1))
                return item;
        }
        return null;
    }

    public static TieredItem getPreviousTier(TieredItem current)
    {
        for (TieredItem item: tieredItemList)
        {
            if (item.getCategory()==current.getCategory()&&item.getIndex()==(current.getIndex()-1))
                return item;
        }
        return null;
    }

    public static ItemStack hideFlags(ItemStack item)
    {
        if (isItemInvalid(item))
            return item;

       ItemMeta meta = item.getItemMeta();
       meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
       meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
       item.setItemMeta(meta);
       return item;
    }

    /*
    Enchants an item with a battle enchantment. Accounts for if it is null.
     */
    public static ItemStack enchant(ItemStack stack, BattleEnchantment enchantment)
    {
        return enchantment == null? stack: enchant(stack, enchantment.getEnchantmentType(), enchantment.getMagnitude());
    }

    public static ItemStack enchant(ItemStack stack, Enchantment enchantment, int level)
    {
        if (isItemInvalid(stack))
            return stack;

        stack.addUnsafeEnchantment(enchantment,level);
        return stack;
    }

    public static ItemStack setColor(ItemStack stack, Color color)
    {
        if (isItemInvalid(stack)) {
            return stack;
        }

        try {
            LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
            meta.setColor(color);
            stack.setItemMeta(meta);
            return stack;
        }
        catch (ClassCastException e)
        {
            return stack;
        }
    }





    public TieredItem getLowestTier(TieredItem item)
    {
        for (TieredItem current: lowestTiers)
        {
            if (item.getCategory()==current.getCategory())
                return current;
        }
        return item;
    }

    private static ItemStack setUnbreakable(ItemStack item)
    {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = toNMSItem(item);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("Unbreakable",true);
        nmsStack.setTag(compound);
        item = toBukkitItem(nmsStack);
        return item;
    }

    //attempts to do a transaction with a player.
    public static boolean didPay(BattlePlayer current, GameItem item, boolean isInflated)
    {
        return didPay(current,item.costMaterial,isInflated?item.inflatedPrice:item.cost);
    }

    public static boolean didPay(BattlePlayer current, Material priceMaterial, int price)
    {
        Player player = current.getRawPlayer();
        ItemStack airHold = new ItemStack(Material.AIR, 1);

        if (player.getInventory().firstEmpty()==-1) //if player has space in inv
        {
            playSound(false,player);
            player.sendMessage(ChatColor.RED+"Your inventory is full!");
            player.closeInventory();
            return false;
        }

        if (!player.getInventory().contains(priceMaterial, price)) //if the inv contains the price
        {
            int below = 0;
            for (int slot = 0; slot < player.getInventory().getSize(); slot++)
            {
                if (isItemInvalid(player.getInventory().getItem(slot)))
                    continue;

                ItemStack currentItem = player.getInventory().getItem(slot);

                if (currentItem.isSimilar(new ItemStack(priceMaterial,1)))
                    below = below + currentItem.getAmount();
            }

            playSound(false,player);
            player.sendMessage(ChatColor.RED+"Don't have enough "+getPriceName(priceMaterial)+"! Need "+(price-below)+" more!");
            return false;
        }

            int paid = 0;
            for (int slot = 0; slot < player.getInventory().getSize(); slot++)
            {
                if (isItemInvalid(player.getInventory().getItem(slot)))
                    continue;
                    ItemStack currentItem = player.getInventory().getItem(slot);
                if (!currentItem.isSimilar(new ItemStack(priceMaterial,1)))
                 continue;

                int amount = currentItem.getAmount();

                while (amount > 0) {
                    paid++;
                    amount--;
                    currentItem.setAmount(amount);

                    if (amount >= 1)
                        player.getInventory().setItem(slot, currentItem);
                    else
                        player.getInventory().setItem(slot, airHold);

                    if (paid >= price) {
                        playSound(true,player);
                        return true;

                    }
                }
            }
            return false;
    }


    private static void playSound(boolean didTrade, Player player)
    {
        PacketSound soundPair = didTrade ? PacketSound.PLING : PacketSound.ENDERMAN;
        Location loc = player.getLocation();
        PacketPlayOutNamedSoundEffect effect = new PacketPlayOutNamedSoundEffect(soundPair.getSoundName(),loc.getX(),loc.getY(),loc.getZ(),1, soundPair.getPitch());
        sendPacket(player,effect);



    }

    private static void sendPacket(Player player, PacketPlayOutNamedSoundEffect packet)
    {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public void dropItem(ItemStack item, Location loc, Plugin plugin)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                World w = loc.getWorld();
                w.dropItemNaturally(loc, item);
               cancel();
            }
        }.runTask(plugin);
    }
}
