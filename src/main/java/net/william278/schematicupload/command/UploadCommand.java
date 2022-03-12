package net.william278.schematicupload.command;

import de.themoep.minedown.MineDown;
import net.william278.schematicupload.upload.UploadCode;
import net.william278.schematicupload.upload.UploadManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UploadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            final UploadCode code = UploadManager.addCode(player.getUniqueId());
            player.spigot().sendMessage(new MineDown("[To upload your schematic,](gray) [[click here]](aqua open_url=http://localhost:2780?upload_code=" + code.getCode() + ")[.](gray)\n[Upload code:](gray) [" + code.getCode() + "](aqua)").toComponent());
            return true;
        }
        return false;
    }

}
