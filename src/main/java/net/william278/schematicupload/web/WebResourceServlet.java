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

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.william278.schematicupload.SchematicUpload;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class WebResourceServlet extends HttpServlet {

    private SchematicUpload plugin;
    private String targetResource;

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        StringBuilder htmlResponseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(plugin.getResource("web/" + targetResource)),
                StandardCharsets.UTF_8
        ))) {
            String fileLine;
            while ((fileLine = reader.readLine()) != null) {
                htmlResponseBuilder.append(fileLine).append("\n");
            }
        }

        final String htmlResponse = htmlResponseBuilder.toString();
        servletResponse.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter printWriter = servletResponse.getWriter()) {
            printWriter.write(htmlResponse);
            printWriter.flush();
        }
    }

}
