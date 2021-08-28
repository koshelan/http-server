package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        final var server = new Server(9999);

        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var path = request.getPath();
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                                             "HTTP/1.1 200 OK\r\n" +
                                                     "Content-Type: " + mimeType + "\r\n" +
                                                     "Content-Length: " + length + "\r\n" +
                                                     "Connection: close\r\n" +
                                                     "\r\n"
                                     ).getBytes());
                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        });

        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var path = request.getPath();
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                                             "HTTP/1.1 200 OK\r\n" +
                                                     "Content-Type: " + mimeType + "\r\n" +
                                                     "Content-Length: " + length + "\r\n" +
                                                     "Connection: close\r\n" +
                                                     "\r\n"
                                     ).getBytes());
                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        });

        server.run();
    }
}


