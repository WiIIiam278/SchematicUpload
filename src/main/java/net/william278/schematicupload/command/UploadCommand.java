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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.william278.desertwell.about.AboutMenu;
import net.william278.desertwell.util.Version;
import net.william278.schematicupload.SchematicUpload;
import net.william278.schematicupload.upload.UploadCode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UploadCommand implements TabExecutor {

    private static final List<String> TAB_COMPLETIONS = List.of("about", "reload");
    private final AboutMenu aboutMenu;
    private final SchematicUpload plugin;

    public UploadCommand(@NotNull SchematicUpload plugin) {
        this.plugin = plugin;
        this.aboutMenu = AboutMenu.builder()
                .title(Component.text("SchematicUpload"))
                .description(Component.text(Objects.requireNonNull(plugin.getDescription().getDescription())))
                .version(Version.fromString(plugin.getDescription().getVersion()))
                .credits("Author",
                        AboutMenu.Credit.of("William278").description("Click to visit website").url("https://william278.net"))
                .buttons(
                        AboutMenu.Link.of("https://github.com/WiIIiam278/SchematicUpload").text("Source").icon("⛏"),
                        AboutMenu.Link.of("https://github.com/WiIIiam278/SchematicUpload/issues").text("Issues").icon("❌").color(TextColor.color(0xff9f0f)),
                        AboutMenu.Link.of("https://discord.gg/tVYhJfyDWG").text("Discord").icon("⭐").color(TextColor.color(0x6773f5)))
                .build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 1) {
            switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "about" -> showAboutMenu(player);
                case "reload" -> reloadPlugin(player);
                default -> plugin.sendMessage(player, "error_invalid_syntax", command.getUsage());
            }
            return true;
        }

        generateUploadCode(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, String[] args) {
        if (args.length > 1 || !sender.hasPermission("schematicupload.command")) {
            return List.of();
        }

        final List<String> completions = Lists.newArrayList();
        StringUtil.copyPartialMatches(args[0], TAB_COMPLETIONS, completions);
        Collections.sort(completions);
        return completions;
    }

    private void generateUploadCode(@NotNull Player player) {
        if (!player.hasPermission("schematicupload.command")) {
            plugin.sendMessage(player, "error_no_permission");
            return;
        }

        final UploadCode code = plugin.getUploadManager().generateCode(player.getUniqueId());
        plugin.sendMessage(
                player,
                "schematic_upload_prompt",
                String.format(
                        "%s?upload_code=%s",
                        plugin.getSettings().getWebServerSettings().getUrl(),
                        code.getCode()
                ),
                plugin.getSettings().getWebServerSettings().getUrl(),
                code.getCode()
        );
    }

    private void reloadPlugin(@NotNull Player player) {
        if (!player.hasPermission("schematicupload.command.reload")) {
            plugin.sendMessage(player, "error_no_permission");
            return;
        }
        plugin.loadSettings();
        plugin.loadLocales();
        plugin.sendMessage(player, "config_reloaded");
    }

    private void showAboutMenu(@NotNull Player player) {
        if (!player.hasPermission("schematicupload.command.about")) {
            plugin.sendMessage(player, "error_no_permission");
            return;
        }
        plugin.getAudiences().player(player).sendMessage(aboutMenu.toComponent());
    }
}
