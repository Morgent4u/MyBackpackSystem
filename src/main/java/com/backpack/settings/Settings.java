package com.backpack.settings;

import com.backpack.inventory.InventoryBackpack;
import com.backpack.utils.MBS;
import com.base.extern.UPDService;
import com.backpack.main.main;
import com.base.objects.SimpleFileExtended;
import com.base.sys.Sys;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Settings extends com.base.ancestor.Settings {
    private SimpleFileExtended settingsFile;

    private InventoryBackpack backpackInventory;
    private InventoryBackpack backpackOtherInventory;

    private String rootSectionKey;
    private String externalSection;
    private String coloredPluginPrefix;

    private boolean useAlwaysSync;
    private boolean useMySQL;
    private boolean useOtherBackpack;
    private boolean useSmartDisconnect;

    /* ************************************* */
    /* CONSTRUCTOR */
    /* ************************************* */

    public Settings() {
        settingsFile = new SimpleFileExtended(Sys.of_getMainFilePath()+"settings.yml");
        rootSectionKey = Sys.of_getPaket();
        coloredPluginPrefix = Sys.of_getPaket();
    }

    /* ************************************* */
    /* OBJECT-METHODS */
    /* ************************************* */

    @Override
    public int of_load() {
        boolean usePlugin = settingsFile.of_getSetBoolean(rootSectionKey + ".Enabled", true);

        if(usePlugin) {
            /* ********************* */
            /* SETTINGS-SECTION */
            /* ********************* */
            String settingsSection = rootSectionKey + ".Settings.";

            useAlwaysSync = settingsFile.of_getSetBoolean(settingsSection + "AlwaysSync", false);

            /* ********************* */
            /* BACKPACK-SECTION */
            /* ********************* */
            String backpackSettingsSection = settingsSection + "Backpack";

            backpackInventory = new InventoryBackpack();
            backpackInventory.of_setInventoryName(settingsFile.of_getSetString(backpackSettingsSection + ".InventoryName", "&8[&cYour backpack&8]").replace("&", "§"));
            backpackInventory.of_setInventorySize(settingsFile.of_getSetInt(backpackSettingsSection + ".InventorySize", 27));
            ItemStack backpackItemStack = MBS.of_getInventoryService().of_createItemStack(Material.EMERALD, "§8[§cBackpack§8]", new String[] {"§fSerialnumber:§a %serialNumber%"}, 1);
            backpackItemStack = settingsFile.of_getSetItemStack(backpackSettingsSection + ".Item", backpackItemStack);
            backpackInventory.of_setBackPackItemStack(backpackItemStack);

            //  Validate current backpackInventory and item.
            String validationErrorMsg = backpackInventory.of_validate();

            if(validationErrorMsg != null) {
                of_sendErrorMessage(null, "Settings.of_load();", "There was an error while loading the section: " + backpackSettingsSection + ". Error:\n"+validationErrorMsg);
                return -1;
            }

            /* ********************* */
            /* OTHER BACKPACK-SECTION */
            /* ********************* */
            String otherBackpackSettingSection = settingsSection + "OtherBackpack";
            useOtherBackpack = settingsFile.of_getSetBoolean(otherBackpackSettingSection + ".Use", true);

            if(of_isUsingOtherBackpack()) {
                backpackOtherInventory = backpackInventory.of_copyObject();
                backpackOtherInventory.of_setInventoryName(settingsFile.of_getSetString(otherBackpackSettingSection + ".InventoryName", "&8[&cBackpack of %p%&8]").replace("&", "§"));
                ItemStack backpackOtherItemStack = MBS.of_getInventoryService().of_createItemStack(Material.EMERALD_BLOCK, "§8[§cBackpack of %p%§8]", new String[] {"§fSerialnumber:§a %serialNumber%"}, 1);
                backpackOtherItemStack = settingsFile.of_getSetItemStack(otherBackpackSettingSection + ".ItemOnDeath", backpackOtherItemStack);
                backpackOtherInventory.of_setBackPackItemStack(backpackOtherItemStack);

                //  Validate current backpackInventory and item.
                validationErrorMsg = backpackOtherInventory.of_validate();

                if(validationErrorMsg != null) {
                    of_sendErrorMessage(null, "Settings.of_load();", "There was an error while loading the section: " + otherBackpackSettingSection + ". Error:\n"+validationErrorMsg);
                    return -1;
                }
            }

            /* ********************* */
            /* EXTERNAL-SECTION */
            /* ********************* */
            externalSection = rootSectionKey + ".External.";
            useMySQL = settingsFile.of_getSetBoolean(externalSection + "MySQL.Use", true);

            String hostName = settingsFile.of_getSetString(externalSection + "MySQL.Host", "localhost");
            String database = settingsFile.of_getSetString(externalSection + "MySQL.Database", "database");
            String username = settingsFile.of_getSetString(externalSection + "MySQL.Username", "user");
            String password = settingsFile.of_getSetString(externalSection + "MySQL.Password", "pwd");
            useSmartDisconnect = settingsFile.of_getSetBoolean(externalSection + "MySQL.SmartDisconnect", true);

            settingsFile.of_save("Settings.of_load();");

            if(useMySQL) {
                if(of_establishDatabaseConnection(hostName, database, username, password) == -1) {
                    return -1;
                }
            }

            return 1;
        }

        return -1;
    }

    @Override
    public void of_unload() {
        if(MBS.of_getPPlayerService() != null) {
            MBS.of_getPPlayerService().of_unload();
        }

        if(of_isUsingSQL()) {
            MBS.of_getSQL().of_closeConnection();
        }
    }

    @Override
    public int of_initSystemServices() {
        MBS.of_getPermission().of_load();
        MBS.of_getMessage().of_load();
        MBS.of_getPPlayerService().of_load();
        return 1;
    }

    private int of_establishDatabaseConnection(String hostName, String database, String username, String password) {
        MBS.of_getSQL().of_setServer(hostName);
        MBS.of_getSQL().of_setDbName(database);
        MBS.of_getSQL().of_setUserName(username);
        MBS.of_getSQL().of_setPassword(password);
        MBS.of_getSQL().of_setUpdateKeyTableAndColumns("mbs_key", "lastKey", "tableName");

        if(MBS.of_getSQL().of_createConnection() == 1)
        {
            //  Store the connection-state into the settings.yml
            settingsFile.of_set(externalSection + "MySQL.Status", Sys.of_getTimeStamp(true) + " - Connected.");
            settingsFile.of_save("Settings.of_load();");

            //  Use the UPDService to update the database-version if it's necessary.
            UPDService.of_getInstance().of_load();
            String errorMessage = UPDService.of_getInstance().of_runUPD();

            if(errorMessage == null)  {
                if(of_isUsingSmartDisconnect()) {
                    Sys.of_debug("We're using db-smart-disconnect. Close db-connection...");
                    MBS.of_getSQL().of_closeConnection();
                }

                return 1;
            }

            UPDService.of_getInstance().of_sendErrorMessage(null, "Settings.of_load();", "Disabling this plugin... there was an error while updating the database! Error-message: " + errorMessage);
            main.of_getPlugin().onDisable();
        } else {
            //  Store the connection-state into the settings.yml
            settingsFile.of_set(externalSection + "MySQL.Status", Sys.of_getTimeStamp(true) + " - No connection.");
            settingsFile.of_save("Settings.of_load();");
        }

        return -1;
    }

    /* ************************************* */
    /* GENERIC-METHODS */
    /* ************************************* */

    @Override
    public void of_printStatusReport2Console() {
        Sys.of_sendMessage(Sys.COLOR_WHITE+"|============================");
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| PLUGIN: " + Sys.COLOR_RED + "My" + Sys.COLOR_GREEN + "Backpack" + Sys.COLOR_YELLOW + "System" + Sys.COLOR_WHITE);
        Sys.of_sendMessage(Sys.COLOR_WHITE+"|============================");
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| VERSION: " + Sys.COLOR_CYAN + Sys.of_getVersion() + Sys.COLOR_WHITE);
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| DEVELOPED BY: " + Sys.COLOR_CYAN + "Probl3mKind" + Sys.COLOR_WHITE);
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| HOTFIX: " + Sys.COLOR_CYAN + ( Sys.of_isHotfix() ? "Yes" : "No" + Sys.COLOR_WHITE));
        Sys.of_sendMessage(Sys.COLOR_WHITE+"|============================");
        Sys.of_sendMessage(Sys.COLOR_WHITE+"|"+Sys.COLOR_PURPLE+" > Settings:" + Sys.COLOR_WHITE);
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| MySQL-Enabled: " + ( of_isUsingSQL() ? "Yes" : "No" ));
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| MySQL-Connected: " + (MBS.of_getSQL().of_isConnected() ? "Yes" : "No"));
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| MySQL-SmartDisconnect: " + (of_isUsingSmartDisconnect() ? "Yes" : "No"));
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| Use always sync: " + (of_isUsingAlwaysSync() ? "Yes" : "No"));
        Sys.of_sendMessage(Sys.COLOR_WHITE+"| Use other backpack: " + (of_isUsingOtherBackpack() ? "Yes" : "No"));
        Sys.of_sendMessage(Sys.COLOR_WHITE+"|============================");
    }

    /* ************************************* */
    /* SETTER */
    /* ************************************* */

    public void of_setColoredPluginPrefix(String coloredPluginPrefix) {
        this.coloredPluginPrefix = coloredPluginPrefix;
    }

    /* ************************************* */
    /* GETTER */
    /* ************************************* */

    public String of_getColoredPluginPrefix() {
        return coloredPluginPrefix;
    }

    public InventoryBackpack of_getBackpackInventory() {
        return backpackInventory;
    }

    public InventoryBackpack of_getBackpackOtherInventory() {
        return backpackOtherInventory;
    }

    /* ************************************* */
    /* BOOLS */
    /* ************************************* */

    public boolean of_isUsingSQL() {
        return useMySQL;
    }

    public boolean of_isUsingAlwaysSync() {
        return useAlwaysSync;
    }

    public boolean of_isUsingOtherBackpack() {
        return useOtherBackpack;
    }

    public boolean of_isUsingSmartDisconnect() {
        return useSmartDisconnect;
    }
}
