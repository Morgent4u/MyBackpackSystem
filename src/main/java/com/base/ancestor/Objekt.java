package com.base.ancestor;

import com.backpack.main.main;
import com.base.sys.Sys;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Created 20.03.2022
 * @Author Nihar
 * @Description
 * This object-class is used as the
 * ancestor-class of any object.
 * It contains useful default methods or properties
 * of an object.
 */
public abstract class Objekt
{
    //  Attributes:
    private int objectId;
    private String objectInfo;

    //  State-flags:
    private boolean ib_errorFlag;

    /* ************************* */
    /* LOADER */
    /* ************************* */

    /**
     * Ancestor for loading other objects.
     * @return 1 Success, -1 Error
     */
    public int of_load()
    {
        Sys.of_sendMessage(of_getObjectName() + ".of_load(); Needs to be overridden by the child-class!");
        return -1;
    }

    /**
     * Ancestor for loading other objects.
     * @param args Needed arguments for loading the objects.
     */
    public void of_load(String[] args)
    {
        Sys.of_sendMessage(of_getObjectName() + ".of_load(String[] args); Needs to be overridden by the child-class!");
    }

    /* ************************* */
    /* UN-LOADER */
    /* ************************* */

    /**
     * Unloads the object.
     */
    public void of_unload()
    {
        Sys.of_sendMessage(of_getObjectName() + ".of_unload(); Needs to be overridden by the child-class!");
    }

    /* ************************* */
    /* OBJECT METHODS */
    /* ************************* */

    /**
     * Is used to validate an object.
     * @return Gives back an error message or empty string for success.
     */
    public String of_validate()
    {
        Sys.of_sendMessage(of_getObjectName() + ".of_validate(); Needs to be overridden by the child-class!");

        //  This needs to be an empty-string!
        //  If you don't want to use an empty-string override this method!
        return "";
    }

    /**
     * Is used to save an object
     * into a database or file.
     * @param invoker The invoker name which calls this method.
     */
    public void of_save(String invoker)
    {
        Sys.of_sendMessage(of_getObjectName() + ".of_save(String); Needs to be overridden by the child-class!");
    }

    /**
     * This method is used to create
     * a new object-instance from the
     * instance it has been called from.
     * <p/>
     * It copies necessary attributes.
     * <b>This method needs to be overridden
     * by the child-class otherwise it returns
     * NULL.<b/>
     *
     * @return A copy of the object-instance on which it has been
     *         called. If an error occurred it returns NULL.
     */
    public Objekt of_copyObject()
    {
        return null;
    }

    /**
     * Overload function of of_save(String)
     */
    public void of_save()
    {
        of_save(of_getObjectName());
    }

    /* ************************* */
    /* DEBUG CENTER */
    /* ************************* */

    /**
     * Sends a debug information to the console.
     * @param invoker The name of the class which calls this function.
     */
    public void of_sendDebugInformation(String invoker)
    {
        if(Sys.of_isDebugModeEnabled() || main.of_isReloading())
        {
            //	Color-codes:
            String white = "\u001B[0m";
            String green = "\u001B[32m";
            String yellow = "\u001B[33m";
            String blue = "\u001B[36m";

            Sys.of_sendMessage("======================================");
            Sys.of_sendMessage(green+"[DEBUG] "+Sys.of_getPaket()+white+", Object: "+yellow+ of_getObjectName()+white);
            Sys.of_sendMessage(blue+"Invoker: "+white+invoker);
            Sys.of_sendMessage(white+"ObjectId: "+of_getObjectId());
            Sys.of_sendMessage(white+"ObjectInfoAttribute: "+of_getInfo());
            Sys.of_sendMessage(white+"HasAnError: " + of_hasAnError());
            Sys.of_sendMessage(yellow+"[Specific object-debug]:"+white);
            of_sendDebugDetailInformation();
            Sys.of_sendMessage("Time: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Sys.of_sendMessage("=====================================");
        }
    }

    /**
     * Define information which will be displayed in the console.
     * This function should be called by of_sendDebugInformation(String);
     * This needs to be defined in every child-class.
     */
    public void of_sendDebugDetailInformation()
    {
        Sys.of_sendMessage(of_getObjectName() + ".of_sendDebugDetailInformation(); Needs to be overridden by the child-class!");
    }

    /* ************************* */
    /* OBJECT ERROR HANDLING */
    /* ************************* */

    /**
     * Sends an error message to the console.
     * @param exception Exception if one exists otherwise type null.
     * @param invoker Classname which calls this function.
     * @param errorMessage A user defined error messages.
     */
    public void of_sendErrorMessage(Exception exception, String invoker, String errorMessage)
    {
        //	Color codes.
        String red = "\u001B[31m";
        String white = "\u001B[0m";
        String yellow = "\u001B[33m";
        String blue = "\u001B[36m";

        //  Set the error-flag.
        ib_errorFlag = true;

        //  Send the object-error-information to the console.
        Sys.of_sendMessage("=====================================");
        Sys.of_sendMessage(red+"[ERROR] "+Sys.of_getPaket()+white+", Object: "+yellow+ of_getObjectName()+white);
        Sys.of_sendMessage(blue+"Invoker: "+white+invoker);
        Sys.of_sendMessage(white+"ObjectId: "+of_getObjectId());
        Sys.of_sendMessage(white+"ObjectInfoAttribute: "+of_getInfo());
        Sys.of_sendMessage(white+"HasAnError: " + of_hasAnError());
        Sys.of_sendMessage(yellow+"[Specific object-debug]:"+white);
        of_sendDebugDetailInformation();
        Sys.of_sendMessage(blue+"Error:"+white);
        Sys.of_sendMessage(red+errorMessage+white);
        Sys.of_sendMessage("Time: "+new SimpleDateFormat("HH:mm:ss").format(new Date())+white);
        Sys.of_sendMessage("=====================================");

        //  Show the console the exception details.
        if(exception != null)
        {
            Sys.of_sendMessage("[Auto-generated exception]:");
            Sys.of_sendMessage(exception.getMessage());
        }
    }

    /* ************************* */
    /* SETTER */
    /* ************************* */

    public void of_setObjectId(int id)
    {
        objectId = id;
    }

    public void of_setInfo(String info)
    {
        this.objectInfo = info;
    }

    /* ************************* */
    /* GETTER */
    /* ************************* */

    public String of_getObjectName()
    {
        return getClass().getName();
    }

    public String of_getInfo()
    {
        return objectInfo;
    }

    public int of_getObjectId()
    {
        return objectId;
    }

    /* ************************* */
    /* BOOLS */
    /* ************************* */

    public boolean of_hasAnError()
    {
        return ib_errorFlag;
    }
}
