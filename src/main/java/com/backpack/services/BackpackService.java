package com.backpack.services;

import com.backpack.inventory.InventoryBackpack;
import com.backpack.main.main;
import com.backpack.objects.PPlayer;
import com.backpack.utils.MBS;
import com.base.ancestor.Objekt;
import com.base.sys.Sys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BackpackService extends Objekt {
    private HashMap<String, Inventory> storedInventories = new HashMap<>();

    /* ************************************* */
    /* OBJECT METHODS */
    /* ************************************* */

    public void of_storeInventoryToDatabase(PPlayer p, String serialNumber, boolean removeAsStoredInv) {
        Inventory inv = storedInventories.get(serialNumber);

        if(inv != null) {
            if(removeAsStoredInv) {
                of_removeInventoryFromList(serialNumber);
            }

            ItemStack[] items = inv.getStorageContents();

            if(items.length > 0)  {
                int backpackId = of_getBackpackDbId(serialNumber);

                if(backpackId != -1) {
                    //  Delete all entries to ID:
                    String sqlDelete = "DELETE FROM mbs_user_backpack_item WHERE mbs_user_backpack = " + backpackId;
                    MBS.of_getSQL().of_run_update_suppress(sqlDelete);

                    for(int slot = 0; slot < items.length; slot++)  {
                        ItemStack item = items[slot];

                        if(item != null)  {
                            Object[] attributes = MBS.of_getConversion().of_convertItemStack2DatabaseItemStackInformation(item);

                            if(attributes != null && attributes.length == 6)  {
                                //  Insert a new entry.
                                String sqlStatement = "INSERT INTO mbs_user_backpack_item ( mbs_user_backpack, slot, type, amount, display, lore, enchant, durability ) VALUES ( " + backpackId + ", " + slot + ", '" + attributes[0] + "', " + attributes[4] + ", '" + attributes[1] + "', '" + attributes[2] +"', '" + attributes[3] +"', "+attributes[5]+" );";

                                if(!MBS.of_getSQL().of_run_update(sqlStatement)) {
                                    of_sendErrorMessage(null, "BackpackService.of_storeInventoryToDatabase();", "There was an error while storing data for a player as a backpack! \nSQL:\n"+sqlStatement);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<ItemStack> of_checkForBackpackItemOnDeathItems(PPlayer p, List<ItemStack> itemStackList) {
        if(MBS.of_getPermission().of_hasPermissions(p.of_getPlayer(), "MBS.Backpack.Interaction")) {
            for(int i = 0; i < itemStackList.size(); i++) {
                ItemStack item = itemStackList.get(i);

                if(item != null) {
                    itemStackList.set(i, of_getOtherBackpackItemByBackpack(p, item));
                }
            }
        }

        return itemStackList;
    }

    public ItemStack of_getOtherBackpackItemByBackpack(PPlayer p, ItemStack item) {
        if(!MBS.of_getPermission().of_hasPermissions(p.of_getPlayer(), "MBS.Backpack.Interaction")) {
            return item;
        }

        String serialNumber = of_getBackpackSerialNumberFromItemStack(MBS.of_getSettings().of_getBackpackInventory(), item);

        if(serialNumber != null) {
            //  Rename ItemStack...
            ItemStack backpackItem = MBS.of_getInventoryService().of_copyItemStack(MBS.of_getSettings().of_getBackpackOtherInventory().of_getBackpackItemStack());
            backpackItem = MBS.of_getInventoryService().of_replaceItemStackValues(backpackItem, "%p%", p.of_getName());
            backpackItem = MBS.of_getInventoryService().of_replaceItemStackValues(backpackItem, "%serialNumber%", serialNumber);

            //  Change inventory name as well!
            Inventory inv = storedInventories.get(serialNumber);

            if(inv != null) {
                String inventoryName = MBS.of_getSettings().of_getBackpackOtherInventory().of_getInventoryName().replace("%p%", p.of_getName());
                inventoryName = inventoryName.replace("%serialNumber%", serialNumber);

                inv = MBS.of_getInventoryService().of_copyInv(inv, inventoryName);
                of_updateBackpackInventory(serialNumber, inv);
            }

            return backpackItem;
        }

        return item;
    }

    private void of_createBackpackDatabaseEntry(PPlayer p, String serialNumber) {
        int backpackId = MBS.of_getSQL().of_updateKey("mbs_user_backpack");
        String sqlInsert = "INSERT INTO mbs_user_backpack ( mbs_user_backpack, playerName, serialNumber ) VALUES( "+backpackId+", '"+p.of_getName()+"', '"+serialNumber+"' )";

        if(!MBS.of_getSQL().of_run_update_suppress(sqlInsert)) {
            of_sendErrorMessage(null, "BackpackService.of_storeInventoryToDatabase();", "There was an error while creating a backpack-entry! \nSQL:\n" + sqlInsert);
        }
    }

    /* ************************************* */
    /* SETTER // ADDER // REMOVER */
    /* ************************************* */

    public void of_addInventoryToList(String serialNumber, Inventory inv) {
        storedInventories.put(serialNumber, inv);
    }

    public void of_updateBackpackInventory(String backpackSerialNumber, Inventory inv)  {
        storedInventories.put(backpackSerialNumber, inv);
    }

    public void of_removeInventoryFromList(String serialNumber) {
        storedInventories.remove(serialNumber);
    }

    /* ************************************* */
    /* GETTER */
    /* ************************************* */

    public Inventory of_getInventory(InventoryBackpack inventoryBackpack, PPlayer p, String backpackNumber) {
        Inventory inv = storedInventories.get(backpackNumber);

        if(inv == null) {
            String inventoryTitle = inventoryBackpack.of_getInventoryName().replace("%p%", p.of_getName());
            inv = Bukkit.createInventory(null, inventoryBackpack.of_getInventorySize(), inventoryTitle);

            String sqlSelect =  "SELECT playerName, mbs_user_backpack \n"+
                                "  FROM mbs_user_backpack \n"+
                                " WHERE serialNumber = '"+backpackNumber+"'";

            ResultSet resultSet = MBS.of_getSQL().of_getResultSet_suppress(sqlSelect, true);
            String playerName = null;
            int backpackId = -1;

            if(resultSet != null) {
                try {
                    playerName = resultSet.getString("playerName");
                    backpackId = resultSet.getInt("mbs_user_backpack");
                } catch (Exception ignored) { }
            }

            //  Load existing items to the inventory...
            if(playerName != null && backpackId != -1) {
                inventoryTitle = inventoryBackpack.of_getInventoryName().replace("%p%", playerName);
                inv = Bukkit.createInventory(null, inventoryBackpack.of_getInventorySize(), inventoryTitle);
                inv.setStorageContents(of_getItemStacksFromDatabaseToBackpack(backpackId, inv.getSize()));
            } else {
                of_createBackpackDatabaseEntry(p, backpackNumber);
            }

            of_addInventoryToList(backpackNumber, inv);
        }

        return inv;
    }

    private ItemStack[] of_getItemStacksFromDatabaseToBackpack(int backpackId, int invSize) {
        String sqlSelect = "SELECT slot, type, amount, display, lore, enchant, durability \n" + "" +
                            "  FROM mbs_user_backpack_item \n" +
                            " WHERE mbs_user_backpack = " + backpackId;
        ResultSet resultSet = MBS.of_getSQL().of_getResultSet_suppress(sqlSelect, false);
        ItemStack[] itemStacks = new ItemStack[invSize];

        if(resultSet != null) {
            try {
                itemStacks = new ItemStack[invSize];

                while (resultSet.next()) {
                    //  Getting data from the SQL-Select.
                    String materialType = resultSet.getString("type");
                    String displayText = resultSet.getString("display");
                    String loreTagFormat = resultSet.getString("lore");
                    String enchantTagFormat = resultSet.getString("enchant");

                    int slot = resultSet.getInt("slot");
                    int amount = resultSet.getInt("amount");
                    int durability = resultSet.getInt("durability");

                    if(slot <= ( invSize - 1)) {
                        itemStacks[slot] = MBS.of_getConversion().of_convertDatabaseItemStack2ItemStack(materialType, displayText, loreTagFormat, enchantTagFormat, amount, durability);
                    }
                }
            } catch (Exception ignored) { }
        }

        return itemStacks;
    }

    public String of_getBackpackSerialNumberByAnyInventoryBackpackStructure(ItemStack item) {
        String serialNumber = of_getBackpackSerialNumberFromItemStack(MBS.of_getSettings().of_getBackpackInventory(), item);

        if(serialNumber == null && MBS.of_getSettings().of_isUsingOtherBackpack()) {
            serialNumber = of_getBackpackSerialNumberFromItemStack(MBS.of_getSettings().of_getBackpackOtherInventory(), item);
        }

        return serialNumber;
    }

    private String of_getBackpackSerialNumberFromItemStack(InventoryBackpack usedInventoryBackpack, ItemStack item) {
        ItemStack itemBackpack = usedInventoryBackpack.of_getBackpackItemStack();

        if(item.getType() == itemBackpack.getType()) {
            //  For example: §fSerialnumber: §a%serialNumber%
            String patternSegment = usedInventoryBackpack.of_getSerialNumberPatternSegment();
            String textForPlaceholder = patternSegment.replace("%serialNumber%", "");

            if(textForPlaceholder.length() > 0) {
                String stringWithBackpackNumber = MBS.of_getInventoryService().of_getItemStackStringSegmentWithSpecificPattern(item, textForPlaceholder);

                if(stringWithBackpackNumber != null) {
                    return Sys.of_getStringWithoutPlaceholder(patternSegment, "%", stringWithBackpackNumber);
                }
            }
        }

        return null;
    }

    public String of_getGeneratedRandomCodeByLength(int codeLength) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            int index = rand.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            codeBuilder.append(randomChar);
        }

        return codeBuilder.toString();
    }

    private int of_getBackpackDbId(String serialNumber) {
        String sqlSelect =  "SELECT mbs_user_backpack \n"+
                            "  FROM mbs_user_backpack \n"+
                            " WHERE serialNumber = '"+serialNumber+"'";

        ResultSet resultSet = MBS.of_getSQL().of_getResultSet_suppress(sqlSelect, true);
        int backpackId = -1;

        if(resultSet != null) {
            try {
                backpackId = resultSet.getInt("mbs_user_backpack");
            } catch (Exception ignored) {}
        }

        return backpackId;
    }

    public ItemStack of_getBackpackItem(InventoryBackpack usedInventoryBackpack, Player p) {
        ItemStack item = MBS.of_getInventoryService().of_copyItemStack(usedInventoryBackpack.of_getBackpackItemStack());
        item = MBS.of_getInventoryService().of_replaceItemStackValues(item, "%p%", p.getName());
        item = MBS.of_getInventoryService().of_replaceItemStackValues(item, "%serialNumber%", of_getGeneratedRandomCodeByLength(5));
        return item;
    }

    /* ************************************* */
    /* BOOLS */
    /* ************************************* */

    public boolean of_isBackpackInventory(String invTitle) {
        if(!of_check4InventoryTitleByBackpackInventoryModel(MBS.of_getSettings().of_getBackpackInventory(), invTitle)) {
            return of_check4InventoryTitleByBackpackInventoryModel(MBS.of_getSettings().of_getBackpackOtherInventory(), invTitle);
        }

        return true;
    }

    private boolean of_check4InventoryTitleByBackpackInventoryModel(InventoryBackpack inventoryBackpack, String invTitle) {
        String invName = inventoryBackpack.of_getInventoryName();

        if(invName.contains("%")) {
            invName = invName.split("%")[0];
            invTitle = invTitle.substring(0, invName.length());
        }

        return invTitle.equalsIgnoreCase(invName);
    }

    public boolean of_check4BackpackInteractionItem(PPlayer p, ItemStack playerItem) {
        InventoryBackpack usedInventoryBackpack = MBS.of_getSettings().of_getBackpackInventory();

        //  Is not NULL if we already found the serialnumber while using the of_getBackpackInventory-Pattern!
        String backpackNumber = of_getBackpackSerialNumberFromItemStack(usedInventoryBackpack, playerItem);

        if(backpackNumber == null) {
            usedInventoryBackpack = MBS.of_getSettings().of_getBackpackOtherInventory();
            backpackNumber = of_getBackpackSerialNumberFromItemStack(usedInventoryBackpack, playerItem);
        }

        if(backpackNumber != null) {
            if(MBS.of_getPermission().of_hasPermissions(p.of_getPlayer(), "MBS.BackpackInteraction.Permission")) {
                Inventory inv = of_getInventory(usedInventoryBackpack, p, backpackNumber);

                if(inv != null) {
                    if(!p.of_hasCoolDown()) {
                        p.of_setCoolDown(true);
                        p.of_addInventorySerialNumber(backpackNumber);
                        p.of_setLastBackpackSerialNumber(backpackNumber);
                        p.of_getPlayer().openInventory(inv);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.of_setCoolDown(false);
                            }
                        }.runTaskLater(main.of_getPlugin(), 20L);
                        return true;
                    }
                }
            } else {
                MBS.of_getMessage().of_sendMessage2PlayerByKey(p.of_getPlayer(), "Messages.MBS.BackpackNoInteractionPermission");
                return true;
            }
        }

        return false;
    }
}