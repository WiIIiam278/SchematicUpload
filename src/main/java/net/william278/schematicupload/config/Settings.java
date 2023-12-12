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

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Settings {

    static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃   SchematicUpload - Config   ┃
            ┃    Developed by William278   ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┗╸ Information: https://william278.net/project/schematicupload/""";

    @Comment("Locale of the default language file to use")
    private String language = Locales.DEFAULT_LOCALE;

    @Comment("Whether to automatically check for plugin updates on startup")
    private boolean checkForUpdates = true;

    @Comment("Whether to automatically download and install plugin updates")
    private String customSchematicDirectory = "";

    @Comment("Settings for the built-in web server")
    private WebServerSettings webServerSettings = new WebServerSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WebServerSettings {
        @Comment("URL to use for the web server (e.g. http://localhost)")
        private String url = "http://localhost";

        @Comment("Port to use for the web server (e.g. 2780)")
        private int port = 2780;
    }

    @Comment("Settings for the built-in web server")
    private LimitSettings limitSettings = new LimitSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LimitSettings {
        @Comment("Maximum file size (in bytes) for schematic uploads")
        private long maxFileSize = 1500000L;
        @Comment("Maximum number of schematics that can be uploaded per period")
        private long periodMinutes = 60L;
        @Comment("Maximum number of schematics that can be uploaded per period")
        private int schematicsPerPeriod = 3;
    }

}
