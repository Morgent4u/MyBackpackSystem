package com.backpack.main;

import com.backpack.cmds.CMD_Backpack;
import com.backpack.events.ue_backpack;
import com.backpack.utils.MBS;
import com.base.sys.Sys;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

/**
 * @Created 02.04.2022
 * @Author Nihar
 * @Description
 * This class is an extended version of the JavaPlugin and
 * contains for this plugin important functions and instances.
 * <p/>
 * Hint:
 * This function is only used to register Bukkit.Events or Bukkit.Commands.
 * Other important components will be enabled or initializes in the object:
 * SETTINGS.
 * <p/>
 * Declared objects in this class will be initialized by the SETTINGS-object!
 *
 */
public class main extends JavaPlugin
{
    //	Instances of the system.
    public static Plugin PLUGIN;
    private static boolean ib_reload;

    /* ************************* */
    /* ENABLE */
    /* ************************* */

    /**
     * This function is called when the plugin is getting enabled.
     * This function contains a version check which is used to enable the plugin if
     * the 'version.yml' and the plugin-version are compatible.
     * <b>The server will be shutdown when no connection to the database can be established.</b>
     */
    @Override
    public void onEnable()
    {
        //	Initialize the plugin.
        PLUGIN = this;
        ib_reload = true;

        //  Check if the plugin is compatible with the version.
        boolean lb_continue = Sys.of_isSystemVersionCompatible(PLUGIN.getName(), "23.1.0.02", "23.1.0.02", "plugins");

        if(lb_continue)
        {
            boolean useDebugMode = Sys.of_isDebugModeEnabled();

            //  Disable the debugMode and enable it later to print all stored messages.
            Sys.of_setDebugMode(false);

            //  Load the default settings.
            int rc = MBS.of_getSettings().of_load();

            if(rc == 1)
            {
                Bukkit.getPluginManager().registerEvents(new ue_backpack(), this);

                //  Register some commands:
                Objects.requireNonNull(getCommand("Backpack")).setExecutor(new CMD_Backpack());

                if(MBS.of_getSettings().of_initSystemServices() == 1)
                {
                    MBS.of_getSettings().of_printStatusReport2Console();
                }
                else
                {
                    Sys.of_sendWarningMessage("Plugin has been disabled by the plugin. A required function or object is missing!");
                    onDisable();
                }
            }
            else
            {
                Sys.of_sendWarningMessage("Plugin has been disabled by 'settings.yml', no database connection or the db-update-service failed.");
                onDisable();
            }

            //  Print all stored messages.
            Sys.of_setDebugMode(useDebugMode);
        }

        ib_reload = false;
    }

    /* ************************* */
    /* DISABLE */
    /* ************************* */

    @Override
    public void onDisable()
    {
        //  If the plugin gets disabled we set the reload-flag.
        ib_reload = true;

        //  If the object has been initialized lets unload it.
        if(MBS.of_getSettings() != null)
        {
            MBS.of_getSettings().of_unload();
        }

        //  End.
        Sys.of_sendMessage("This plugin has been coded by Probl3mKind! Thank you for using this plugin!");

        //  Reset the reload-flag.
        ib_reload = false;
    }

    /* ************************* */
    /* GETTER */
    /* ************************* */

    public static Plugin of_getPlugin()
    {
        return PLUGIN;
    }

    /* ************************* */
    /* BOOLS */
    /* ************************* */

    public static boolean of_isReloading()
    {
        return ib_reload;
    }
}