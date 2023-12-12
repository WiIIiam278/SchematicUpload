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

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.william278.schematicupload.SchematicUpload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static net.william278.schematicupload.SchematicUpload.ALLOWED_EXTENSIONS;

@AllArgsConstructor
public class DownloadCommand implements TabExecutor {

    public static final String DOWNLOAD_DIRECTORY = "download";
    private static final int DOWNLOAD_EXPIRY_MINUTES = 20;

    private final SchematicUpload plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0 || !getAvailableFiles().contains(args[0])) {
            plugin.sendMessage(player, "error_invalid_syntax", command.getUsage());
            return true;
        }

        downloadSchematic(player, args[0]);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length > 1 || !sender.hasPermission("schematicupload.command.download")) {
            return List.of();
        }
        final List<String> completions = Lists.newArrayList();
        StringUtil.copyPartialMatches(args[0], getAvailableFiles(), completions);
        Collections.sort(completions);
        return completions;
    }

    private void downloadSchematic(@NotNull Player player, @NotNull String fileName) {
        final Path downloadsFolder = plugin.getConfigDirectory().resolve("web").resolve(DOWNLOAD_DIRECTORY);
        try {
            Files.createDirectories(downloadsFolder);
            final Path toCopy = plugin.getSchematicDirectory().resolve(fileName);

            // If too large, don't copy
            if (Files.size(toCopy) > plugin.getSettings().getLimitSettings().getMaxFileSize()) {
                plugin.sendMessage(player, "error_download_too_big");
                return;
            }

            // Copy file to the downloads folder with a random name
            final String downloadFile = String.format("%s-%s", UUID.randomUUID(), toCopy.getFileName());
            Files.copy(toCopy, downloadsFolder.resolve(downloadFile));
            plugin.sendMessage(
                    player, "schematic_download_prompt",
                    String.format(
                            "%s/%s/%s",
                            plugin.getSettings().getWebServerSettings().getUrl(),
                            DOWNLOAD_DIRECTORY,
                            downloadFile
                    ),
                    Integer.toString(DOWNLOAD_EXPIRY_MINUTES)
            );

            // Expire the download after a set amount of time
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, (a) -> {
                try {
                    Files.delete(downloadsFolder.resolve(downloadFile));
                } catch (Throwable e) {
                    plugin.log(Level.WARNING, "Failed to expire downloaded schematic", e);
                }
            }, Duration.ofMinutes(DOWNLOAD_EXPIRY_MINUTES).getSeconds() * 20L);
        } catch (Throwable e) {
            plugin.sendMessage(player, "error_download_failed");
            plugin.log(Level.WARNING, "Failed to copy schematic for downloading", e);
        }
    }

    @NotNull
    private List<String> getAvailableFiles() {
        return List.of(Objects.requireNonNull(
                plugin.getSchematicDirectory().toFile().list(
                        (dir, name) -> ALLOWED_EXTENSIONS.stream().anyMatch(name::endsWith)
                ),
                "Failed to list schematics in directory"
        ));
    }

}
