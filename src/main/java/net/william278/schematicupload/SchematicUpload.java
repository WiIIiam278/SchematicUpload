package net.william278.schematicupload;

import net.william278.schematicupload.command.UploadCommand;
import net.william278.schematicupload.web.WebServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SchematicUpload extends JavaPlugin {

    private WebServer webServer;

    private static SchematicUpload instance;

    public static SchematicUpload getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Register command
        Objects.requireNonNull(getCommand("uploadschematic")).setExecutor(new UploadCommand());

        // Start web server
        webServer = WebServer.start();
    }

    @Override
    public void onDisable() {
        webServer.end();
    }
}
