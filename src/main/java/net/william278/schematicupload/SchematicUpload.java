/*
 * This file is part of SchematicUpload, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.schematicupload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.william278.schematicupload.command.CommandProvider;
import net.william278.schematicupload.config.ConfigProvider;
import net.william278.schematicupload.config.Locales;
import net.william278.schematicupload.config.Settings;
import net.william278.schematicupload.upload.UploadManager;
import net.william278.schematicupload.upload.UploadProvider;
import net.william278.schematicupload.util.MetaProvider;
import net.william278.schematicupload.util.MetricsProvider;
import net.william278.schematicupload.web.WebServer;
import net.william278.schematicupload.web.WebServerProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

@Getter
@Setter
@NoArgsConstructor
public class SchematicUpload extends JavaPlugin implements ConfigProvider, CommandProvider, WebServerProvider,
        MetricsProvider, UploadProvider, MetaProvider {

    public static final List<String> ALLOWED_EXTENSIONS = List.of(".schem", ".schematic", ".litematic");

    private Settings settings;
    private Locales locales;
    private UploadManager uploadManager;
    private WebServer webServer;
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        log(Level.INFO, "Enabling SchematicUpload v" + getPluginVersion());
        loadAudiences();
        loadSettings();
        loadLocales();
        loadCommand();
        loadWebServer();
        loadUploadManager();
        loadMetrics();
        log(Level.INFO, "SchematicUpload v" + getPluginVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        endWebServer();
        endAudiences();
    }

    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        if (exceptions.length > 0) {
            getLogger().log(level, message, exceptions[0]);
            return;
        }
        getLogger().log(level, message);
    }

    @Override
    @NotNull
    public Path getConfigDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    @NotNull
    public SchematicUpload getPlugin() {
        return this;
    }
}
