package me.verwelius.treasurefix.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

    private final Plugin plugin;

    private FileConfiguration bukkitConfig;

    public boolean logging;

    public String treasureFound;
    public String mapCreated;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        bukkitConfig = plugin.getConfig();
        parse();
    }

    private void parse() {
        logging = bukkitConfig.getBoolean("logging", false);

        treasureFound = bukkitConfig.getString("treasure-found");
        mapCreated = bukkitConfig.getString("map-created");
    }

}
