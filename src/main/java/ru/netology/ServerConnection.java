package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements Runnable {

    private Socket socket;


    public ServerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {

            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            System.out.println(requestLine);
            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                // just close socket
                return;
            }
            String s;
            Request request = new Request(parts[0], parts[1], parts[2]);
            while (!(s = in.readLine()).isEmpty()) {
                request.addHeader(s);
            }
            if ("POST".equals(request.getMethod())) {
                s = in.readLine();
                request.setBody(s);
            }
            System.out.println(request);
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
}
