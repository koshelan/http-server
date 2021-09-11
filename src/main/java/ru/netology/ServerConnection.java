package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class ServerConnection implements Runnable {

    final static int LIMIT = 4096;
    private final Socket socket;


    public ServerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            in.mark(LIMIT);

            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            System.out.println(requestLine);
            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                // just close socket
                return;
            }
            Request request = requestCreation(parts);
            String s;
            while (!(s = in.readLine()).isEmpty()) {
                request.addHeader(s);
            }
            if ("POST".equals(request.getMethod())) {
                final var contentLength = request.getHeaderByName("Content-Length");
                if (contentLength.isPresent()) {
                    final var length = Integer.parseInt(contentLength.get());
                    final var bodyBytes = new char[length];
                    in.read(bodyBytes,0,length);
                    request.setBody(new String(bodyBytes));

                }
            }
            System.out.println(request);
            System.out.println(request.getQueryParams());
            System.out.println(request.getQueryParam("value"));
            Server.getInstance().handlerRequest(request, out);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Request requestCreation(String[] parts) {
        if ((!parts[1].isEmpty()) && (parts[1].contains("?"))) {
            final String[] pathSeparations = parts[1].split("\\?");
            return new Request(parts[0], pathSeparations[0], pathSeparations[1], parts[2]);
        } else {
            return new Request(parts[0], parts[1], parts[2]);
        }
    }


}
