package net.william278.schematicupload.upload;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class UploadManager {

    public static final HashMap<UUID, UploadCode> uploadAuthorizationCodes = new HashMap<>();

    public static ConsumptionResult consumeCode(String input) {
        if (input.length() != 8) {
            return new ConsumptionResult(false, Optional.empty());
        }
        UUID uuidToRemove = null;
        boolean consumed = false;
        for (UUID uuid : uploadAuthorizationCodes.keySet()) {
            UploadCode code = uploadAuthorizationCodes.get(uuid);
            if (code.getCode().equals(input)) {
                uuidToRemove = uuid;
                if (code.hasTimedOut()) {
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
        return new ConsumptionResult(consumed, player);
    }

    public static UploadCode addCode(UUID player) {
        UploadCode code = UploadCode.generateRandomCode();
        uploadAuthorizationCodes.put(player, code);
        return code;
    }

    public record ConsumptionResult(boolean consumed, Optional<UUID> user) {
    }

}
