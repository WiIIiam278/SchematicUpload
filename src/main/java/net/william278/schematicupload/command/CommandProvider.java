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

package net.william278.schematicupload.command;

import net.william278.schematicupload.SchematicUpload;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface CommandProvider {

    default void loadCommand() {
        // Register upload command
        final UploadCommand uploadCommand = new UploadCommand(getPlugin());
        final PluginCommand bukkitUpload = Objects.requireNonNull(
                getPlugin().getCommand("schematicupload"),
                "Upload command not registered in CommandMap"
        );
        bukkitUpload.setExecutor(uploadCommand);
        bukkitUpload.setTabCompleter(uploadCommand);

        // Register download command
        final DownloadCommand downloadCommand = new DownloadCommand(getPlugin());
        final PluginCommand bukkitDownload = Objects.requireNonNull(
                getPlugin().getCommand("schematicdownload"),
                "Download command not registered in CommandMap"
        );
        bukkitDownload.setExecutor(downloadCommand);
        bukkitDownload.setTabCompleter(downloadCommand);
    }

    @NotNull
    SchematicUpload getPlugin();
}
