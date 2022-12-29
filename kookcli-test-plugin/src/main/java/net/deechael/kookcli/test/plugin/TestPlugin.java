package net.deechael.kookcli.test.plugin;

import net.deechael.kookcli.plugin.Plugin;

public class TestPlugin extends Plugin {

    @Override
    public void onEnable() {
        System.out.println("Loaded test plugin");
    }

}
