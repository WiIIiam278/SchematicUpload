package net.william278.schematicupload.upload;

import net.william278.schematicupload.SchematicUpload;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

public class UploadManager {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();

    // Map of UUIDs to auth codes
    private static final HashMap<UUID, UploadCode> uploadAuthorizationCodes = new HashMap<>();

    // Map of UUIDs to timestamps indicating the last file they uploaded (used for rate limiting)
    private static final HashMap<UUID, LinkedList<Long>> userUploadQueues = new HashMap<>();

    public static boolean canUpload(UUID player) {
        final int maxUploadsPerPeriod = plugin.getSettings().uploadLimitCount;
        final long periodLength = (plugin.getSettings().uploadLimitPeriod * 60L); // Make it minutes
        if (!userUploadQueues.containsKey(player)) {
            userUploadQueues.put(player, new LinkedList<>());
        }
        final long currentTimestamp = Instant.now().getEpochSecond();
        if (!userUploadQueues.get(player).isEmpty()) {
            if (currentTimestamp > userUploadQueues.get(player).getLast() + periodLength) {
                userUploadQueues.get(player).removeLast();
            }
            return userUploadQueues.get(player).size() <= maxUploadsPerPeriod;
        }
        return true;
    }

    public static void markAsUploaded(UUID player) {
        final long currentTimestamp = Instant.now().getEpochSecond();
        if (!userUploadQueues.containsKey(player)) {
            userUploadQueues.put(player, new LinkedList<>());
        }
        userUploadQueues.get(player).addFirst(currentTimestamp);
    }

    public static ConsumptionResult consumeCode(String input) {
        String errorCode = "";
        if (input.length() != 8) {
            return new ConsumptionResult(false, Optional.empty(), "Invalid code; wrong length");
        }
        UUID uuidToRemove = null;
        boolean consumed = false;
        for (UUID uuid : uploadAuthorizationCodes.keySet()) {
            UploadCode code = uploadAuthorizationCodes.get(uuid);
            if (code.getCode().equals(input)) {
                uuidToRemove = uuid;
                if (code.hasTimedOut()) {
                    errorCode = "Invalid code; that code has expired";
                    break;
                }
                if (!canUpload(uuid)) {
                    errorCode = "You've reached the maximum uploads you can do in " + plugin.getSettings().uploadLimitPeriod + " minutes";
                    break;
                }

                consumed = true;
                break;
            }
        }
        Optional<UUID> player;
        if (uuidToRemove != null) {
            uploadAuthorizationCodes.remove(uuidToRemove);
            player = Optional.of(uuidToRemove);
        } else {
            player = Optional.empty();
        }
        return new ConsumptionResult(consumed, player, errorCode);
    }

    public static UploadCode addCode(UUID player) {
        UploadCode code = UploadCode.generateRandomCode();
        uploadAuthorizationCodes.put(player, code);
        logToFile("Schematic code " + code.getCode() + " generated for " + player);
        return code;
    }

    public record ConsumptionResult(boolean consumed, Optional<UUID> user, String errorCode) {
    }

    public static void logToFile(String message) {
        File logFile = new File(SchematicUpload.getInstance().getDataFolder(), "schems.log");

        try (FileWriter fw = new FileWriter(logFile,true);
             PrintWriter pw = new PrintWriter(fw);) {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            pw.println(message);
        }
        catch (IOException e) {
            Bukkit.getLogger().warning(e.toString());
        }
    }
}
