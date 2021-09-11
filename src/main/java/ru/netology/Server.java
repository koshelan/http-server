package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int NUMBER_OF_THREADS = 64;
    private static volatile Server server = null;
    private final int portNumber;
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server(int portNumber) {
        this.portNumber = portNumber;
        server = this;
        fillINBasicHandlers();
    }

    public static Server getInstance() {
        return server;
    }

    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        try (final var serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    executorService.execute(new ServerConnection(socket));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    public void handlerRequest(Request request, BufferedOutputStream out) {
        try {
            if (handlers.containsKey(request.getMethod())) {
                if (handlers.get(request.getMethod()).containsKey(request.getPath())) {
                    handlers.get(request.getMethod()).get(request.getPath()).handle(request, out);
                    return;
                }
            }
            handlers.get("").get("").handle(request, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

    void fillINBasicHandlers() {
        addHandler("", "", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) throws IOException {
                out.write((
                                  "HTTP/1.1 404 Not Found\r\n" +
                                          "Content-Length: 0\r\n" +
                                          "Connection: close\r\n" +
                                          "\r\n"
                          ).getBytes());
                out.flush();
            }
        });

        Handler handler = new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) throws IOException {
                final var path = request.getPath();
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                out.write((
                                  "HTTP/1.1 200 OK\r\n" +
                                          "Content-Type: " + mimeType + "\r\n" +
                                          "Content-Length: " + length + "\r\n" +
                                          "Connection: close\r\n" +
                                          "\r\n"
                          ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            }
        };

        addHandler("GET", "/index.html", handler);
        addHandler("GET", "/spring.svg", handler);
        addHandler("GET", "/spring.png", handler);
        addHandler("GET", "/resources.html", handler);
        addHandler("GET", "/styles.css", handler);
        addHandler("GET", "/app.js", handler);
        addHandler("GET", "/links.html", handler);
        addHandler("GET", "/forms.html", handler);
        addHandler("GET", "/events.html", handler);
        addHandler("GET", "/events.js", handler);

        addHandler("GET", "/classic.html", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) throws IOException {
                final var path = request.getPath();
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                                  "HTTP/1.1 200 OK\r\n" +
                                          "Content-Type: " + mimeType + "\r\n" +
                                          "Content-Length: " + content.length + "\r\n" +
                                          "Connection: close\r\n" +
                                          "\r\n"
                          ).getBytes());
                out.write(content);
                out.flush();
            }
        });
    }

}
