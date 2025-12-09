package org.firstinspires.ftc.teamcode.utils;

import fi.iki.elonen.NanoHTTPD;
import java.io.*;

public class NanoFileServer extends NanoHTTPD {
    private final File rootDir;

    public NanoFileServer(int port, File rootDir) {
        super(port);
        this.rootDir = rootDir.getAbsoluteFile();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        File target = new File(rootDir, uri).getAbsoluteFile();

        // Prevent path traversal
        try {
            String rootPath = rootDir.getCanonicalPath();
            String targetPath = target.getCanonicalPath();
            if (!targetPath.startsWith(rootPath)) {
                return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_PLAINTEXT, "Forbidden");
            }
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "IO error");
        }

        if (!target.exists()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        }

        if (target.isDirectory()) {
            StringBuilder sb = new StringBuilder("<html><body>");
            sb.append("<h1>Index of ").append(uri).append("</h1><ul>");
            File[] kids = target.listFiles();
            if (kids != null) {
                for (File f : kids) {
                    String name = f.getName() + (f.isDirectory() ? "/" : "");
                    String href = uri.endsWith("/") ? uri + name : uri + "/" + name;
                    sb.append("<li><a href=\"").append(href).append("\">").append(name).append("</a></li>");
                }
            }
            sb.append("</ul></body></html>");
            return newFixedLengthResponse(Response.Status.OK, "text/html", sb.toString());
        }

        try {
            FileInputStream fis = new FileInputStream(target);
            return newChunkedResponse(Response.Status.OK, guessMime(target.getName()), fis);
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Read error");
        }
    }

    private static String guessMime(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".html") || n.endsWith(".htm")) return "text/html";
        if (n.endsWith(".txt") || n.endsWith(".log")) return "text/plain";
        if (n.endsWith(".json")) return "application/json";
        if (n.endsWith(".jpg") || n.endsWith(".jpeg")) return "image/jpeg";
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".gif")) return "image/gif";
        if (n.endsWith(".csv")) return "text/csv";
        if (n.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}