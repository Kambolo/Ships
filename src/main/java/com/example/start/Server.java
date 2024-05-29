package com.example.start;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements GameOperations{
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private LabelUpdateCallback callback;
    private boolean roomAvailable;
    private boolean gameStatus;
    private final Lock lock = new ReentrantLock();
    private boolean gameAccepted;
    public Server(ServerSocket serverSocket, LabelUpdateCallback callback) {
        this.gameAccepted = false;
        this.setServerSocket(serverSocket);
        this.callback = callback;
        this.roomAvailable = true;
        this.gameStatus = true;

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Server IP: " + localHost.getHostAddress());
        }catch (UnknownHostException e){
            System.out.println("UnknowHost exception");
        }

        new Thread(new Runnable() {

            boolean locked;

            @Override
            public void run() {
                while(gameStatus) {
                    try {
                        //halt until client connects, and return socket that enables conection
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
                        //lock.unlock();

                    } catch (IOException e) {
                        //e.printStackTrace();
                        closeEverything();
                    } finally{
                        if(locked){
                            lock.unlock();
                            locked = false;
                        }
                    }
                }
            }
        }).start();

    }

    public void sendMessage(String messageToClient){
        if(getSocket().isConnected()){
            try{
                bufferedWriter.write(messageToClient);
                bufferedWriter.newLine(); //otherwise client would wait for more messages to come
                bufferedWriter.flush(); //sending message even if it is not full
            } catch(IOException e){
                e.printStackTrace();
                System.out.println("Error while sending message");
                closeEverything();
            }
        }
    }

    public void closeEverything(){
        //System.out.println("Closing Everything");
        gameStatus = false;

        try{
            if(getBufferedReader() != null){
                getBufferedReader().close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(getSocket() != null){
                getSocket().close();
            }
            if(getServerSocket() != null){
                getServerSocket().close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isRoomAvailable() {
        return roomAvailable;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}