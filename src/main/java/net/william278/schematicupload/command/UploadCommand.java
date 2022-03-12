package net.william278.schematicupload.command;

import de.themoep.minedown.MineDown;
import net.william278.schematicupload.SchematicUpload;
import net.william278.schematicupload.upload.UploadCode;
import net.william278.schematicupload.upload.UploadManager;
import net.william278.schematicupload.util.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadCommand implements CommandExecutor, TabExecutor {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();
    private static final StringBuilder PLUGIN_INFORMATION = new StringBuilder()
            .append("[SchematicUpload](#00fb9a bold) [| Version ").append(plugin.getDescription().getVersion()).append("](#00fb9a)\n")
            .append("[").append(plugin.getDescription().getDescription()).append("](gray)\n")
            .append("[• Author:](white) [William278](gray show_text=&7Click to visit website open_url=https://william278.net)\n")
            .append("[• Report Issues:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://github.com/WiIIiam278/SchematicUpload/issues)\n")
            .append("[• Support Discord:](white) [[Link]](#00fb9a show_text=&7Click to join open_url=https://discord.gg/tVYhJfyDWG)");

    private final String[] tabCompletions = {"about", "reload"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "about" -> {
                        if (!player.hasPermission("schematicupload.command.about")) {
                            MessageManager.sendMessage(player, "error_no_permission");
                            return true;
                        }
                        player.spigot().sendMessage(new MineDown(PLUGIN_INFORMATION.toString()).toComponent());
                    }
                    case "reload" -> {
                        if (!player.hasPermission("schematicupload.command.reload")) {
                            MessageManager.sendMessage(player, "error_no_permission");
                            return true;
                        }
                        plugin.loadSettings();
                        MessageManager.loadMessages(plugin.getSettings().language);
                        sender.spigot().sendMessage(new MineDown("[SchematicUpload](#00fb9a bold) &#00fb9a&| Reloaded config & message files.").toComponent());
                    }
                    default -> MessageManager.sendMessage(player, "error_invalid_syntax", command.getUsage());
                }
            } else {
                if (!player.hasPermission("schematicupload.command")) {
                    MessageManager.sendMessage(player, "error_no_permission");
                    return true;
                }
                final UploadCode code = UploadManager.addCode(player.getUniqueId());
                MessageManager.sendMessage(player, "schematic_upload_prompt",
                        plugin.getSettings().webServerUrl + "?upload_code=" + code.getCode(),
                        plugin.getSettings().webServerUrl, code.getCode());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final Player player = (Player) sender;
        if (!player.hasPermission("schematicupload.command")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            final List<String> tabCompletions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], tabCompletions, tabCompletions);
            Collections.sort(tabCompletions);
            return tabCompletions;
        }
        return Collections.emptyList();
    }
}
