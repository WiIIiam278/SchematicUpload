package net.william278.schematicupload.upload;

import java.time.Instant;

public class UploadCode {

    // Code options
    public final static long CODE_TIMEOUT = 300L;
    public final static String RANDOM_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public final static int CODE_LENGTH = 8;

    private final String code;
    private final long generationTimestamp;

    public String getCode() {
        return code;
    }

    public boolean hasTimedOut() {
        return Instant.now().getEpochSecond() >= generationTimestamp + CODE_TIMEOUT;
    }

    private UploadCode(String code, long currentTime) {
        this.code = code;
        this.generationTimestamp = currentTime;
    }

    // Generates a random 8 letter code
    public static UploadCode generateRandomCode() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            result.append(RANDOM_CHARACTERS.charAt((int) Math.floor(Math.random() * RANDOM_CHARACTERS.length())));
        }
        return new UploadCode(result.toString(), Instant.now().getEpochSecond());
    }

}
