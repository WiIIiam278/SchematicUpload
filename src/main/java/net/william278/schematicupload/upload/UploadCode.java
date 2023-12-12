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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.OffsetDateTime;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class UploadCode {

    // Code options
    public static final Duration CODE_TIMEOUT = Duration.ofMinutes(5);
    public static final String RANDOM_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int CODE_LENGTH = 8;

    @Getter
    private final String code;
    private final OffsetDateTime generationTimestamp;

    // Returns true if the code has timed out
    public boolean hasTimedOut() {
        return generationTimestamp.plus(CODE_TIMEOUT).isBefore(OffsetDateTime.now());
    }

    // Generates a random 8 character code
    public static UploadCode generate() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            result.append(RANDOM_CHARACTERS.charAt((int) Math.floor(Math.random() * RANDOM_CHARACTERS.length())));
        }
        return new UploadCode(result.toString(), OffsetDateTime.now());
    }

}
