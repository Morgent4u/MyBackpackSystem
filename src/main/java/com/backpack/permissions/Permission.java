package com.backpack.permissions;

import com.base.manager.PermissionManager;

public class Permission extends PermissionManager {

    @Override
    public int of_load() {
        //  Load general Permissions:
        of_addPermissions2Board("General.Admin.Permission", "mbs.general.admin");
        of_addPermissions2Board("General.Default.Permission", "mbs.general.default");
        of_addPermissions2Board("MBS.BackpackInteraction.Permission", "mbs.backpack.interaction");

        return permissionsFile.of_save("of_load();");
    }
}
