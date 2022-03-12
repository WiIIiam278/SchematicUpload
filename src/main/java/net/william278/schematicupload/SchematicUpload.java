package net.william278.schematicupload;

import net.william278.schematicupload.command.UploadCommand;
import net.william278.schematicupload.config.Settings;
import net.william278.schematicupload.util.MessageManager;
import net.william278.schematicupload.web.WebServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SchematicUpload extends JavaPlugin {

    private WebServer webServer;

    private static SchematicUpload instance;

    public static SchematicUpload getInstance() {
        return instance;
    }

    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    // (Re)-load plugin settings
    public void loadSettings() {
        settings = new Settings();
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Load config and messages
        loadSettings();
        MessageManager.loadMessages(settings.language);

        // Register command & tab completer
        Objects.requireNonNull(getCommand("uploadschematic")).setExecutor(new UploadCommand());
        Objects.requireNonNull(getCommand("uploadschematic")).setTabCompleter(new UploadCommand());

        // Start web server
        webServer = WebServer.start();
    }

    @Override
    public void onDisable() {
        webServer.end();
    }
}
