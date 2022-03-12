package net.william278.schematicupload.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.william278.schematicupload.SchematicUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class WebResourceServlet extends HttpServlet {

    private static final SchematicUpload plugin = SchematicUpload.getInstance();

    private final String targetResource;

    public WebResourceServlet(String targetResource) {
        this.targetResource = targetResource;
    }

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        StringBuilder htmlResponseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(plugin.getResource("web/" + targetResource)), StandardCharsets.UTF_8))) {
            String fileLine;
            while ((fileLine = reader.readLine()) != null) {
                htmlResponseBuilder.append(fileLine).append("\n");
            }
        }
        String htmlResponse = htmlResponseBuilder.toString();
        servletResponse.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter printWriter = servletResponse.getWriter()) {
            printWriter.write(htmlResponse);
            printWriter.flush();
        }
    }

}
