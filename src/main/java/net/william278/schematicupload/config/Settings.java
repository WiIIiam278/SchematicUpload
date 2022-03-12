package net.william278.schematicupload.config;

import net.william278.schematicupload.SchematicUpload;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();

    public final String language;
    public final String schematicDirectory;

    public final int webServerPort;
    public final String webServerUrl;

    public final int maxFileSize;
    public final long uploadLimitPeriod;
    public final int uploadLimitCount;

    public Settings() {
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        this.language = config.getString("language", "en-gb");
        this.schematicDirectory = config.getString("custom_schematic_directory", "");

        this.webServerPort = config.getInt("web_server.port", 2780);
        this.webServerUrl = config.getString("web_server.url", "").isBlank() ? "http://localhost:" + webServerPort : config.getString("web_server.url");

        this.maxFileSize = config.getInt("upload_limit.max_schematic_file_size", 1500000);
        this.uploadLimitPeriod = config.getLong("upload_limit.period_minutes", 60L);
        this.uploadLimitCount = config.getInt("upload_limit.schematics_per_period", 3);
    }

}
