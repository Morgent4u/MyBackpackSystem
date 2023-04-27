package com.base.ancestor;

import com.base.sys.Sys;

public abstract class Settings extends Objekt {

    private String pluginColoredPrefix = Sys.of_getPaket();

    public int of_initSystemServices() {
        return -1;
    }

    public void of_printStatusReport2Console() { }

    public void of_setPluginColoredPrefix(String coloredPrefix) {
        this.pluginColoredPrefix = coloredPrefix;
    }

    public String of_getPluginPrefix() {
        return pluginColoredPrefix;
    }
}
