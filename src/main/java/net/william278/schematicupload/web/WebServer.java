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

import jakarta.servlet.MultipartConfigElement;
import net.william278.schematicupload.SchematicUpload;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Level;

public class WebServer {

    private static final String[] WEB_FILES = new String[]{"index.html", "style.css", "uploader.js"};

    private final SchematicUpload plugin;
    private Server jettyServer;
    private final int port;

    private WebServer(@NotNull SchematicUpload plugin) {
        this.plugin = plugin;
        this.port = plugin.getSettings().getWebServerSettings().getPort();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final int maxThreads = 32;
            final int minThreads = 8;
            final int idleTimeout = 120;
            QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

            plugin.log(Level.INFO, "Starting the internal webserver on port " + port);
            jettyServer = new Server(threadPool);

            try (ServerConnector connector = new ServerConnector(jettyServer)) {
                connector.setPort(port);
                jettyServer.setConnectors(new Connector[]{connector});
            }
        });
    }

    private void initialize() {
        try {
            ServletContextHandler contextHandler = new ServletContextHandler();

            // Web page servlets
            for (String resourcePath : WEB_FILES) {
                WebResourceServlet requestHandler = new WebResourceServlet(plugin, resourcePath);
                ServletHolder servletHolder = new ServletHolder(requestHandler);
                contextHandler.addServlet(servletHolder, "/" + (resourcePath.equals("index.html") ? "" : resourcePath));
            }

            // Multipart Upload configuration
            Path multipartTmpDir = new File(plugin.getSchematicDirectory().toFile(), ".temp").toPath();
            if (multipartTmpDir.toFile().mkdirs()) {
                plugin.log(Level.INFO, "Preparing for upload...");
            }
            String location = multipartTmpDir.toString();

            // Upload size limits
            final long maxFileSize = 2 * 1024 * 1024; // 2 MB
            final long maxRequestSize = 2 * 1024 * 1024; // 2 MB
            final int fileSizeThreshold = 64; // 64 bytes

            MultipartConfigElement multipartConfig = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
            FileUploadServlet saveUploadServlet = new FileUploadServlet(plugin);
            ServletHolder servletHolder = new ServletHolder(saveUploadServlet);
            servletHolder.getRegistration().setMultipartConfig(multipartConfig);
            contextHandler.addServlet(servletHolder, "/api");

            // Set handlers, start server
            jettyServer.setHandler(contextHandler);
            jettyServer.start();
            jettyServer.join();
        } catch (Throwable e) {
            plugin.log(Level.SEVERE, "Failed to start the internal webserver.", e);
        }
    }

    // Gracefully terminate the webserver
    public void end() {
        try {
            plugin.log(Level.INFO, "Shutting down the internal webserver.");
            jettyServer.stop();
        } catch (Throwable e) {
            plugin.log(Level.SEVERE, "Failed to gracefully shutdown the internal webserver.", e);
        }
    }


    // Create a new WebServer and start it on the port
    @NotNull
    public static WebServer createAndStart(@NotNull SchematicUpload plugin) {
        final WebServer server = new WebServer(plugin);
        server.initialize();
        return server;
    }

}
