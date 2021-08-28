package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int NUMBER_OF_THREADS = 64;
    private int portNumber;

    public Server(int portNumber) {
        this.portNumber= portNumber;
    }

    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        try (final var serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try  {
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
}
