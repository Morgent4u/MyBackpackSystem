package com.base.manager;

import com.base.ancestor.Objekt;
import com.base.objects.SimpleFile;
import com.base.sys.Sys;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * @Created 16.04.2022
 * @Author Nihar
 * @Description
 * This class is used to handle permissions of the user.
 * The permissions can be edited in the permissions.yml file.
 */
public class PermissionManager extends Objekt
{
    //  Attributes:
    //  PermissionsKey - Permissions
    protected Map<String, String> permissions = new HashMap<>();
    public SimpleFile permissionsFile;

    /* ************************* */
    /* CONSTRUCTOR // LOADER */
    /* ************************* */

    public PermissionManager() {
        permissionsFile = new SimpleFile(Sys.of_getMainFilePath() + "//Others//permissions.yml");
    }

    /* **************************** */
    /* ADDER // SETTER // REMOVER */
    /* *************************** */

    public void of_addPermissions2Board(String permKey, String permission)  {
        permission = permissionsFile.of_getSetString(permKey, permission);
        permissions.put(permKey, permission);
    }

    /* **************************** */
    /* DEBUG CENTER */
    /* *************************** */

    @Override
    public void of_sendDebugDetailInformation()
    {
        Sys.of_sendMessage("Loaded permissions: " + permissions.size());
    }

    /* **************************** */
    /* GETTER */
    /* *************************** */

    /**
     * This method is used to get the
     * defined admin-permission which has been
     * set in the permissions.yml
     * @return The admin-permissions.
     */
    public String of_getAdminPermission()
    {
        return permissions.get("General.Admin.Permission");
    }

    /* **************************** */
    /* BOOLS */
    /* *************************** */

    /**
     * This method is used to identify the permissions by the given permissions-key.
     * The defined permissions are loaded from the permissions.yml file.
     * @param p Bukkit-player instance.
     * @param permKey The permissions-key.
     * @return true if the permissions-key is defined, false if not.
     */
    public boolean of_hasPermissions(Player p, String permKey)
    {
        String permission = permissions.get(permKey);

        //  If the permission is not found, use the admin permission.
        if(permission == null)
        {
            permission = of_getAdminPermission();
        }

        return p.hasPermission(permission);
    }

    /**
     * This method is used to check if the player has the given permission.
     * The special-part of this method is that it does not check the current permissions-list
     * which has been defined in the permissions.yml.
     * @param p The player to check.
     * @param permissions The permissions to check.
     * @return True if the player has the given permission, false if not.
     */
    public boolean of_hasPermissionsByDefault(Player p, String permissions)
    {
        if(permissions != null)
        {
            return p.hasPermission(permissions);
        }

        return false;
    }

    public boolean of_isAdmin(Player p)
    {
        return of_hasPermissionsByDefault(p, of_getAdminPermission());
    }
}
