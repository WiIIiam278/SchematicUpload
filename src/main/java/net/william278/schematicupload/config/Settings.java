package net.william278.schematicupload.config;

import net.william278.schematicupload.SchematicUpload;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Level;

public class Settings {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();

    public final String language;
    public final Path schematicDirectory;
    public String worldEditPlugin;

    public final int webServerPort;
    public final String webServerUrl;

    public final int maxFileSize;
    public final long uploadLimitPeriod;
    public final int uploadLimitCount;

    public Settings() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        this.language = config.getString("language", "en-gb");
        this.schematicDirectory = getSchematicDirectory();

        this.webServerPort = config.getInt("web_server.port", 2780);
        this.webServerUrl = config.getString("web_server.url", "").isBlank() ? "http://localhost:" + webServerPort : config.getString("web_server.url");

        this.maxFileSize = config.getInt("upload_limit.max_schematic_file_size", 1500000);
        this.uploadLimitPeriod = config.getLong("upload_limit.period_minutes", 60L);
        this.uploadLimitCount = config.getInt("upload_limit.schematics_per_period", 3);
    }

    // Determines the custom schematic directory
    private Path getSchematicDirectory() {
        String customSchematicDirectory = plugin.getConfig().getString("custom_schematic_directory", "");
        if (!customSchematicDirectory.isBlank()) {
            final File customDirectory = new File(customSchematicDirectory);
            if (customDirectory.mkdirs()) {
                plugin.getLogger().log(Level.CONFIG, "Created directories for schematic files");
            }
            return customDirectory.toPath();
        }
        String pluginInPath = plugin.getName();
        if (Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null) {
            pluginInPath = "FastAsyncWorldEdit";
            worldEditPlugin = pluginInPath;
        } else if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            pluginInPath = "WorldEdit";
            worldEditPlugin = pluginInPath;
            if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") != null) {
                worldEditPlugin += " + AsyncWorldEdit";
            }
        } else {
            worldEditPlugin = "None";
        }
        File schematicDirectoryFile = new File(Bukkit.getWorldContainer() + File.separator + "plugins" + File.separator
                + pluginInPath + File.separator + "schematics");
        if (schematicDirectoryFile.mkdirs()) {
            plugin.getLogger().log(Level.CONFIG, "Created directories for schematic files");
        }
        return schematicDirectoryFile.toPath();
    }

}
