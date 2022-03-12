package net.william278.schematicupload.upload;

import java.util.HashMap;
import java.util.UUID;

public class UploadManager {

    public static final HashMap<UUID, UploadCode> uploadAuthorizationCodes = new HashMap<>();

    public static boolean consumeCode(String input) {
        if (input.length() != 8) {
            return false;
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
        if (uuidToRemove != null) {
            uploadAuthorizationCodes.remove(uuidToRemove);
        }
        return consumed;
    }

    public static UploadCode addCode(UUID player) {
        UploadCode code = UploadCode.generateRandomCode();
        uploadAuthorizationCodes.put(player, code);
        return code;
    }

}
