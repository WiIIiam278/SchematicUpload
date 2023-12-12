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

package net.william278.schematicupload.upload;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import net.william278.schematicupload.SchematicUpload;
import net.william278.schematicupload.config.Settings;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public class UploadManager {

    // Map of UUIDs to auth codes
    private final HashMap<UUID, UploadCode> uploadAuthorizationCodes = Maps.newHashMap();

    // Map of UUIDs to timestamps indicating the last file they uploaded (used for rate limiting)
    private final HashMap<UUID, LinkedList<OffsetDateTime>> userUploadQueues = Maps.newHashMap();

    private SchematicUpload plugin;

    public boolean canUpload(@NotNull UUID player) {
        final Settings.LimitSettings limits = plugin.getSettings().getLimitSettings();
        final int maxUploads = limits.getSchematicsPerPeriod();
        final Duration period = Duration.of(limits.getPeriodMinutes(), ChronoUnit.MINUTES);
        if (!userUploadQueues.containsKey(player)) {
            userUploadQueues.put(player, new LinkedList<>());
        }

        if (!userUploadQueues.get(player).isEmpty()) {
            if (OffsetDateTime.now().isAfter(userUploadQueues.get(player).getLast().plus(period))) {
                userUploadQueues.get(player).removeLast();
            }
            return userUploadQueues.get(player).size() <= maxUploads;
        }
        return true;
    }

    public void markAsUploaded(@NotNull UUID player) {
        final OffsetDateTime currentTimestamp = OffsetDateTime.now();
        if (!userUploadQueues.containsKey(player)) {
            userUploadQueues.put(player, new LinkedList<>());
        }
        userUploadQueues.get(player).addFirst(currentTimestamp);
    }

    @NotNull
    public ConsumptionResult consumeCode(String input) {
        String errorMessage = "";
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
                    errorMessage = "Invalid code; that code has expired";
                    break;
                }
                if (!canUpload(uuid)) {
                    errorMessage = String.format("You've reached the maximum uploads you can do in %s minutes",
                            plugin.getSettings().getLimitSettings().getPeriodMinutes());
                    break;
                }

                consumed = true;
                break;
            }
        }

        final Optional<UUID> player;
        if (uuidToRemove != null) {
            uploadAuthorizationCodes.remove(uuidToRemove);
            player = Optional.of(uuidToRemove);
        } else {
            player = Optional.empty();
        }
        return new ConsumptionResult(consumed, player, errorMessage);
    }

    @NotNull
    public UploadCode generateCode(@NotNull UUID player) {
        UploadCode code = UploadCode.generate();
        uploadAuthorizationCodes.put(player, code);
        return code;
    }

    public record ConsumptionResult(boolean consumed, Optional<UUID> user, @NotNull String errorMessage) {
    }

}
