package com.example.start.Server;

import com.example.start.GameOperations;
import com.example.start.LabelUpdateCallback;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A server class implementing the GameOperations interface.
 */
public class Server implements GameOperations {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private LabelUpdateCallback callback;
    private boolean roomAvailable;
    private boolean gameStatus;
    private final Lock lock = new ReentrantLock();
    private boolean gameAccepted;
    private static String ipAddress = null;

    /**
     * Constructor for the Server class.
     *
     * @param serverSocket the ServerSocket for the server
     * @param callback     the callback to update label
     */
    public Server(ServerSocket serverSocket, LabelUpdateCallback callback) {
        this.gameAccepted = false;
        this.setServerSocket(serverSocket);
        this.callback = callback;
        this.roomAvailable = true;
        this.gameStatus = true;

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ipAddress = localHost.getHostAddress();
            System.out.println("Server IP: " + ipAddress);
        } catch (UnknownHostException e) {
            System.out.println("UnknowHost exception");
        }

        new Thread(new Runnable() {

            boolean locked;

            @Override
            public void run() {
                while (gameStatus) {
                    try {
                        // halt until client connects, and return socket that enables conection
                        setSocket(serverSocket.accept());
                        lock.lock();
                        locked = true;

                        if (isRoomAvailable()) {
                            callback.updateLabel("Player Connected");
                            setBufferedReader(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));
                            bufferedWriter = new BufferedWriter(new OutputStreamWriter(getSocket().getOutputStream()));

                            bufferedWriter.write("Server is open");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            roomAvailable = false;

                        } else {
                            BufferedWriter delegator = new BufferedWriter(new OutputStreamWriter(getSocket().getOutputStream()));
                            delegator.write("Server is full");
                            delegator.newLine();
                            delegator.flush();
                            delegator.close();
                        }
                    } catch (IOException e) {
                        closeEverything();
                    } finally {
                        if (locked) {
                            lock.unlock();
                            locked = false;
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Sets the IP address for the server.
     *
     * @param ip the IP address to set
     */
    public static void setIP(String ip) {
        ipAddress = ip;
    }

    /**
     * Gets the IP address of the server.
     *
     * @return the IP address of the server
     */
    public static String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sends a message to the client.
     *
     * @param messageToClient the message to send
     */
    public void sendMessage(String messageToClient) {
        if (getSocket().isConnected()) {
            try {
                bufferedWriter.write(messageToClient);
                bufferedWriter.newLine(); // otherwise client would wait for more messages to come
                bufferedWriter.flush(); // sending message even if it is not full
            } catch (IOException e) {
                System.out.println("Error while sending message");
                closeEverything();
            }
        }
    }

    /**
     * Closes all connections and resources associated with the server.
     */
    public void closeEverything() {
        gameStatus = false;

        try {
            if (getBufferedReader() != null) {
                getBufferedReader().close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (getSocket() != null) {
                getSocket().close();
            }
            if (getServerSocket() != null) {
                getServerSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the room is available.
     *
     * @return true if the room is available, otherwise false
     */
    public boolean isRoomAvailable() {
        return roomAvailable;
    }

    /**
     * Gets the socket.
     *
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets the socket.
     *
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets the BufferedReader.
     *
     * @return the BufferedReader
     */
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    /**
     * Sets the BufferedReader.
     *
     * @param bufferedReader the BufferedReader to set
     */
    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    /**
     * Gets the ServerSocket.
     *
     * @return the ServerSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Sets the ServerSocket.
     *
     * @param serverSocket the ServerSocket to set
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
