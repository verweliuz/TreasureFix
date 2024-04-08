package me.verwelius.treasurefix;

import me.verwelius.treasurefix.algorithm.Finder;
import me.verwelius.treasurefix.command.MainCommand;
import me.verwelius.treasurefix.config.Config;
import me.verwelius.treasurefix.listener.LootGenerationListener;
import me.verwelius.treasurefix.util.NmsCaster;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TreasureFix extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        NmsCaster nmsCaster = new NmsCaster(getCraftPackage());

        Config config = new Config(this);
        Finder finder = new Finder(nmsCaster, getLogger(), config);

        registerListener(new LootGenerationListener(nmsCaster, finder));
        registerCommand("treasurefix", new MainCommand(config));
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void registerCommand(String name, TabExecutor handler) {
        PluginCommand command = getCommand("treasurefix");
        if(command == null) return;

        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }

    private String getCraftPackage() {
        String path = null;

        for (Package aPackage : Package.getPackages()) {
            if(path != null) break;
            if(aPackage.getName().startsWith("org.bukkit.craftbukkit.v1_")) {
                path = aPackage.getName();
            }
        }

        if(path == null) return null;

        String[] strings = path.split("\\.");
        for(String string : strings) {
            if(string.startsWith("v1_")) return "org.bukkit.craftbukkit." + string;
        }
        return null;
    }

}
