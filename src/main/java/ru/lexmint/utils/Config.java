package ru.lexmint.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.lexmint.HSClans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class describing config which extends bukkit's YamlConfiguration and makes
 * work with config files easier.
 * When creating Config instance, it creates Config file in plugin's data folder.
 */
public class Config extends YamlConfiguration {
    /**
     * File of the config on disk.
     */
    private final File configFile;

    /**
     * Creates new config file on disk with given configName.
     * @param configName
     */
    public Config(String configName) {
        super();
        configName = configName.replace("/", File.separator);

        configFile = new File(HSClans.instance.getDataFolder() + File.separator + configName);
        HSClans.instance.getDataFolder().mkdirs();

        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();

            }
            load(configFile);

            InputStream defInputStream = HSClans.instance.getResource(configName);
            if (defInputStream != null) {
                YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(defInputStream);
                setDefaults(defaultConfiguration);
                options().copyDefaults(true);
                save(configFile);
            }
        } catch (IOException e) {
            HSClans.debug.error("Can't load configuration " + configName + " (IOException): " + e.getMessage());
        } catch (InvalidConfigurationException e) {
            HSClans.debug.error("Can't load configuration " + configName + " (Invalid configuration): " + e.getMessage());
        }
    }

    /**
     * Saves config file to the disk.
     */
    public void saveConfig() {
        try {
            save(configFile);
        } catch (IOException e) {
            HSClans.debug.error("Error while saving config " + configFile.getPath() + " " + e.getMessage());
        }
    }

    /**
     * Removes key (or whole configuration section) from config.
     * @param path
     */
    public void removeKey(String path) {
        set(path, null);
    }
}
