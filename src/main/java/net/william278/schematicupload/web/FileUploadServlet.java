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

package net.william278.schematicupload.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.william278.schematicupload.SchematicUpload;
import net.william278.schematicupload.upload.UploadManager;
import net.william278.schematicupload.util.GZipUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FileUploadServlet extends HttpServlet {

    private final SchematicUpload plugin;

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            // Handle the multipart form upload
            processParts(servletRequest, servletResponse, plugin.getSchematicDirectory());
        } catch (IOException | ServletException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to process upload", e);
            sendReply(servletResponse, 500, "An error occurred on the server");
        }
    }

    private void processParts(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Path outputDir) throws ServletException, IOException {
        // Process and check the code
        Part codePart = servletRequest.getPart("input-code");
        StringBuilder codeBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(codePart.getInputStream(), StandardCharsets.UTF_8))) {
            int readerHead;
            while ((readerHead = reader.read()) != -1) {
                codeBuilder.append((char) readerHead);
            }
        }
        final String code = codeBuilder.toString();
        final UploadManager.ConsumptionResult consumptionResult = plugin.getUploadManager().consumeCode(code);
        if (!consumptionResult.consumed()) {
            sendReply(servletResponse, 403, "Invalid or expired code");
            return;
        }

        // Process and validate the file
        Part filePart = servletRequest.getPart("file-upload");
        final String fileName = code + "-" + filePart.getSubmittedFileName();
        if (StringUtil.isBlank(fileName)) {
            sendReply(servletResponse, 400, "Invalid file name (empty)");
            return;
        }
        if (fileName.length() >= 48) {
            sendReply(servletResponse, 400, "Invalid file name (too long)");
            return;
        }
        if (!(fileName.endsWith(".schem") || fileName.endsWith(".schematic"))) {
            sendReply(servletResponse, 400, "Invalid file type extension");
            return;
        }

        // Encode the file name and set the output file
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        Path outputFile = outputDir.resolve(encodedFileName);
        try (InputStream inputStream = filePart.getInputStream();
            OutputStream outputStream = Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            final long maxSize = plugin.getSettings().getLimitSettings().getMaxFileSize();
            if (inputStream.available() > maxSize) {
                Files.delete(outputFile);
                sendReply(servletResponse, 400, "Invalid schematic; too large. (Max size: " + (maxSize / 1024) + "KiB)");
                return;
            }
            if (!GZipUtil.isGZipped(inputStream)) {
                Files.delete(outputFile);
                sendReply(servletResponse, 400, "Invalid schematic format.");
                return;
            }
            IO.copy(inputStream, outputStream);
        }

        // Send confirmation back to the site and to the user if they are in-game still
        consumptionResult.user().ifPresent(user -> {
            plugin.getUploadManager().markAsUploaded(user); // Mark them as uploaded to rate limit
            Player player = Bukkit.getServer().getPlayer(user);
            if (player != null) {
                plugin.sendMessage(player, "schematic_upload_complete",
                        String.format("//schem load %s", fileName));
            }
        });
        sendReply(servletResponse, 200, fileName);
    }

    // Send a reply back to the web client
    private void sendReply(HttpServletResponse response, int replyCode, String replyMessage) {
        try {
            response.setContentType("application/json");
            response.setStatus(replyCode);
            response.getWriter().println("{\"message\":\"" + replyMessage + "\"}");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "IOException when trying to send HTTP reply", e);
        }
    }

}
