package net.william278.schematicupload.web;

import jakarta.servlet.MultipartConfigElement;
import net.william278.schematicupload.SchematicUpload;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

public class WebServer {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();

    private static final Path pluginDirectory = new File(Bukkit.getWorldContainer() + File.separator + "plugins" + File.separator + "FastAsyncWorldEdit" + File.separator + "schematics").toPath();

    private Server jettyServer;
    private final int port;

    private WebServer(int port) {
        this.port = port;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final int maxThreads = 32;
            final int minThreads = 8;
            final int idleTimeout = 120;
            QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

            jettyServer = new Server(threadPool);
            ServerConnector connector = new ServerConnector(jettyServer);
            connector.setPort(port);
            jettyServer.setConnectors(new Connector[]{connector});

            initialize();
        });
    }

    private void initialize() {
        try {
            ServletContextHandler contextHandler = new ServletContextHandler();

            // Web page servlets
            String[] webResources = new String[]{"index.html", "style.css", "uploader.js"};
            for (String resourcePath : webResources) {
                WebResourceServlet requestHandler = new WebResourceServlet(resourcePath);
                ServletHolder servletHolder = new ServletHolder(requestHandler);
                contextHandler.addServlet(servletHolder, "/" + (resourcePath.equals("index.html") ? "" : resourcePath));
            }

            // Multipart Upload configuration
            Path multipartTmpDir = new File(pluginDirectory.toFile(), ".temp").toPath();
            if (multipartTmpDir.toFile().mkdirs()) {
                plugin.getLogger().log(Level.INFO, "Preparing for upload...");
            }
            String location = multipartTmpDir.toString();

            // Upload size limits
            final long maxFileSize = 2 * 1024 * 1024; // 2 MB
            final long maxRequestSize = 2 * 1024 * 1024; // 2 MB
            final int fileSizeThreshold = 64; // 64 bytes

            MultipartConfigElement multipartConfig = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
            FileUploadServlet saveUploadServlet = new FileUploadServlet(pluginDirectory);
            ServletHolder servletHolder = new ServletHolder(saveUploadServlet);
            servletHolder.getRegistration().setMultipartConfig(multipartConfig);
            contextHandler.addServlet(servletHolder, "/api");

            // Set handlers, start server
            jettyServer.setHandler(contextHandler);
            jettyServer.start();
            jettyServer.join();

            plugin.getLogger().log(Level.INFO, "Successfully started the internal webserver on port " + port + ".");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to start the internal webserver.", e);
        }
    }

    // Create a new WebServer and start it on the port
    public static WebServer start() {
        return new WebServer(2780);
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
