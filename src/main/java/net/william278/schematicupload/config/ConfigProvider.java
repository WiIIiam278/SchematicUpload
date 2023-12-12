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

package net.william278.schematicupload.config;

import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import de.exlll.configlib.YamlConfigurations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.william278.schematicupload.SchematicUpload;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Level;

public interface ConfigProvider {

    @NotNull
    YamlConfigurationProperties.Builder<?> YAML_CONFIGURATION_PROPERTIES = YamlConfigurationProperties
            .newBuilder().setNameFormatter(NameFormatters.LOWER_UNDERSCORE);

    @NotNull
    Settings getSettings();

    void setSettings(@NotNull Settings settings);

    default void loadSettings() {
        setSettings(YamlConfigurations.update(
                getConfigDirectory().resolve("config.yml"),
                Settings.class,
                YAML_CONFIGURATION_PROPERTIES.header(Settings.CONFIG_HEADER).build()
        ));
    }

    @NotNull
    Locales getLocales();

    void setLocales(@NotNull Locales locales);

    default void sendMessage(@NotNull Player player, @NotNull String message, @NotNull String... placeholders) {
        getLocales().getLocale(message, placeholders)
                .ifPresent(locale -> getAudiences().player(player)
                        .sendMessage(locale.toComponent()));
    }

    default void loadLocales() {
        final YamlConfigurationStore<Locales> store = new YamlConfigurationStore<>(
                Locales.class, YAML_CONFIGURATION_PROPERTIES.header(Locales.CONFIG_HEADER).build()
        );
        try (InputStream input = getResource(String.format("locales/%s.yml", getSettings().getLanguage()))) {
            final Locales locales = store.read(input);
            store.save(
                    locales,
                    getConfigDirectory().resolve(String.format("messages-%s.yml", getSettings().getLanguage()))
            );
            setLocales(locales);
        } catch (Throwable e) {
            getPlugin().log(Level.SEVERE, "An error occurred whilst loading the locales file", e);
        }
    }

    @NotNull
    BukkitAudiences getAudiences();

    void setAudiences(@NotNull BukkitAudiences audiences);

    default void loadAudiences() {
        setAudiences(BukkitAudiences.create(getPlugin()));
    }

    default void endAudiences() {
        getAudiences().close();
    }

    @NotNull
    Path getConfigDirectory();

    InputStream getResource(@NotNull String name);

    @NotNull
    default WorldEditType getWorldEditType() {
        final PluginManager manager = getPlugin().getServer().getPluginManager();
        for (WorldEditType type : WorldEditType.values()) {
            if (manager.getPlugin(type.getPluginName()) != null) {
                return type;
            }
        }
        return WorldEditType.NONE;
    }

    @NotNull
    default Path getSchematicDirectory() {
        return getWorldEditType().getSchematicPath(getPlugin());
    }


    @AllArgsConstructor
    enum WorldEditType {
        FAST_ASYNC_WORLD_EDIT(
                "FastAsyncWorldEdit",
                (plugin) -> Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(),
                        "plugins", "FastAsyncWorldEdit", "schematics")
        ),
        ASYNC_WORLD_EDIT(
                "AsyncWorldEdit",
                (plugin) -> Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(),
                        "plugins", "WorldEdit", "schematics")
        ),
        WORLD_EDIT(
                "WorldEdit",
                (plugin) -> Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(),
                        "plugins", "WorldEdit", "schematics")
        ),
        NONE(
                "None",
                (plugin) -> plugin.getServer().getWorldContainer().toPath().resolve(
                        plugin.getSettings().getCustomSchematicDirectory())
        );

        @Getter
        private final String pluginName;
        private final Function<SchematicUpload, Path> schematicPath;

        @NotNull
        public Path getSchematicPath(@NotNull SchematicUpload plugin) {
            return schematicPath.apply(plugin);
        }

    }

    @NotNull
    SchematicUpload getPlugin();

}
