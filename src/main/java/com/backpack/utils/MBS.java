package com.backpack.utils;

import com.backpack.inventory.InventoryService;
import com.backpack.messages.Message;
import com.backpack.permissions.Permission;
import com.backpack.services.BackpackService;
import com.backpack.services.PPlayerService;
import com.backpack.settings.Settings;
import com.base.extern.MySQL;
import com.base.objects.Conversion;

public class MBS {
    /* ************************************* */
    /* INSTANCES */
    /* ************************************* */

    private static final Settings _SETTINGS = new Settings();
    private static final MySQL _SQL = new MySQL("SQL");
    private static final Permission _PERMISSION = new Permission();
    private static final InventoryService _INVENTORY = new InventoryService();
    private static final Message _MESSAGE = new Message();
    private static final BackpackService _BACKPACKSERVICE = new BackpackService();
    private static final PPlayerService _PPLAYERSERVICE = new PPlayerService();
    private static final Conversion _CONVERSION = new Conversion();


    /* ************************************* */
    /* GETTER OF INSTANCES */
    /* ************************************* */

    public static Settings of_getSettings() {
        return _SETTINGS;
    }
    public static MySQL of_getSQL() {
        return _SQL;
    }
    public static Permission of_getPermission() {
        return _PERMISSION;
    }
    public static Message of_getMessage() {
        return _MESSAGE;
    }
    public static InventoryService of_getInventoryService() {
        return _INVENTORY;
    }
    public static BackpackService of_getBackpackService() {
        return _BACKPACKSERVICE;
    }
    public static PPlayerService of_getPPlayerService() {
        return _PPLAYERSERVICE;
    }
    public static Conversion of_getConversion() {
        return _CONVERSION;
    }
}
