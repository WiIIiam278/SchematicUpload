package net.william278.schematicupload.web;

import jakarta.servlet.MultipartConfigElement;
import net.william278.schematicupload.SchematicUpload;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;

public class WebServer {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();
    private Server jettyServer;

    private WebServer(int port) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final int maxThreads = 32;
            final int minThreads = 8;
            final int idleTimeout = 120;
            final QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

            plugin.getLogger().log(Level.INFO, "Starting the internal webserver on port " + port);
            jettyServer = new Server(threadPool);
            try (ServerConnector connector = new ServerConnector(jettyServer)) {
                connector.setPort(port);
                jettyServer.setConnectors(new Connector[]{connector});
            }
            initialize();
        });
    }

    // Copy files from the classpath resources folder to the plugin data folder
    public void copyWebFiles(final String source, final Path target) throws URISyntaxException, IOException {
        final URI resource = Objects.requireNonNull(getClass().getResource("")).toURI();
        try (FileSystem fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap())) {
            final Path jarPath = fileSystem.getPath(source);
            Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path currentTarget = target.resolve(jarPath.relativize(dir).toString());
                    Files.createDirectories(currentTarget);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

            });
        }
    }

    // Initialize the webserver
    private void initialize() {
        try {
            // Copy web resources if needed
            final File targetDir = new File(plugin.getDataFolder(), "web");
            if (!targetDir.exists()) {
                plugin.getLogger().log(Level.INFO, "Generating files for the webserver...");
                if (!targetDir.mkdirs()) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to create web directory");
                    return;
                }
                copyWebFiles("/web", Paths.get(targetDir.getPath()));

                // Create a file in the /web folder with the current version
                final File versionFile = new File(targetDir, "version.txt");
                if (!versionFile.exists()) {
                    if (!versionFile.createNewFile()) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create version file");
                        return;
                    }
                }
                Files.write(versionFile.toPath(), plugin.getDescription().getVersion().getBytes());
            }

            // Create resource handler to handle requests
            final ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setWelcomeFiles(new String[]{"index.html"});
            resourceHandler.setResourceBase(targetDir.getPath());
            resourceHandler.setDirectoriesListed(true);
            final ContextHandler staticHandler = new ContextHandler("/");
            staticHandler.setHandler(resourceHandler);

            // Create multipart upload handler directory
            final Path uploadTempDirectory = new File(plugin.getSettings().schematicDirectory.toFile(), ".temp").toPath();
            if (uploadTempDirectory.toFile().mkdirs()) {
                plugin.getLogger().log(Level.INFO, "Prepared temporary upload folder for the webserver...");
            }

            // Upload size limits
            final long maxFileSize = 2 * 1024 * 1024; // 2 MB
            final long maxRequestSize = 2 * 1024 * 1024; // 2 MB
            final int fileSizeThreshold = 64; // 64 bytes

            // Create multipart upload handler
            final MultipartConfigElement multipartConfig = new MultipartConfigElement(uploadTempDirectory.toString(),
                    maxFileSize, maxRequestSize, fileSizeThreshold);
            final FileUploadServlet saveUploadServlet = new FileUploadServlet(plugin.getSettings().schematicDirectory);
            final ServletHolder servletHolder = new ServletHolder(saveUploadServlet);
            servletHolder.getRegistration().setMultipartConfig(multipartConfig);

            // Register as a servlet
            final ServletHandler apiHandler = new ServletHandler();
            apiHandler.addServletWithMapping(servletHolder, "/api");

            // Set handlers, start server
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{staticHandler, apiHandler});
            jettyServer.setHandler(handlers);
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to start the internal webserver.", e);
        }
    }

    // Create a new WebServer and start it on the port
    public static WebServer start() {
        return new WebServer(plugin.getSettings().webServerPort);
    }

    // Gracefully close the internal webserver
    public void end() {
        try {
            plugin.getLogger().log(Level.INFO, "Shutting down the internal webserver.");
            jettyServer.stop();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to gracefully shutdown the internal webserver.", e);
        }
    }
}
