package com.example.start;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Represents a client that can connect to a server, send messages, and perform game operations.
 */
public class Client implements GameOperations {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    /**
     * Constructs a Client object with the specified socket.
     *
     * @param socket the socket to connect to the server
     */
    public Client(Socket socket) {
        try {
            this.setSocket(socket);
            this.setBufferedReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            this.setBufferedWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        } catch (IOException e) {
            System.out.println("Error joining server!");
            e.printStackTrace();
            closeEverything();
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param messageToServer the message to be sent to the server
     */
    public void sendMessage(String messageToServer) {
        try {
            getBufferedWriter().write(messageToServer);
            getBufferedWriter().newLine();
            getBufferedWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while sending message");
            closeEverything();
        }
    }

    /**
     * Attempts to connect to the server.
     *
     * @return true if the connection is successful, false otherwise
     */
    public boolean tryToConnect() {
        Callable<Boolean> thread = new Callable<>() {

            @Override
            public Boolean call() throws Exception {
                String msg;

                while (getSocket().isConnected()) {
                    try {
                        msg = getBufferedReader().readLine();
                        if (msg.equals("Server is full")) {
                            System.out.println("Server is full!!!!!");
                            closeEverything();
                            return false;
                        } else {
                            break;
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }
        };

        FutureTask<Boolean> result = new FutureTask<>(thread);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(result);
        executor.shutdown();

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the socket, buffered reader, and buffered writer.
     */
    public void closeEverything() {
        try {
            if (getBufferedReader() != null) {
                getBufferedReader().close();
            }
            if (getBufferedWriter() != null) {
                getBufferedWriter().close();
            }
            if (getSocket() != null) {
                getSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the socket connected to the server.
     *
     * @return the socket connected to the server
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets the socket to connect to the server.
     *
     * @param socket the socket to connect to the server
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets the buffered reader for reading messages from the server.
     *
     * @return the buffered reader
     */
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    /**
     * Sets the buffered reader for reading messages from the server.
     *
     * @param bufferedReader the buffered reader
     */
    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    /**
     * Gets the buffered writer for sending messages to the server.
     *
     * @return the buffered writer
     */
    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    /**
     * Sets the buffered writer for sending messages to the server.
     *
     * @param bufferedWriter the buffered writer
     */
    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }
}
