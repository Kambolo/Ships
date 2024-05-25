package com.example.start;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server{
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private LabelUpdateCallback callback;
    private boolean roomAvailable;
    private boolean gameStatus;
    private final Lock lock = new ReentrantLock();

    public Server(ServerSocket serverSocket, LabelUpdateCallback callback) {
        this.serverSocket = serverSocket;
        this.callback = callback;
        this.roomAvailable = true;
        this.gameStatus = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(gameStatus) {
                    try {
                        //halt until client connects, and return socket that enables conection
                        socket = serverSocket.accept();
                        lock.lock();
                        if (isRoomAvailable()) {
                            callback.updateLabel("Player Connected");
                            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                            bufferedWriter.write("Server is open");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            roomAvailable = false;
                        } else {
                            BufferedWriter delegator = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            delegator.write("Server is full");
                            delegator.newLine();
                            delegator.flush();
                            delegator.close();
                        }
                        lock.unlock();

                    } catch (IOException e) {
                        //e.printStackTrace();
                        closeEverything();
                    }
                }
            }
        }).start();

    }

    public void sendMessageToClient(String messageToClient){
        if(socket.isConnected()){
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

    //making a recevieve message func in thread because we dont want the program to stop while waiting for message
    public void receiveMessageFromClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()){
                    try{
                        String messageFromClient = bufferedReader.readLine();
                        System.out.println(messageFromClient);
                    } catch (IOException e){
                        System.out.println("Error receiving a message!");
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    public void closeEverything(){
        //System.out.println("Closing Everything");
        gameStatus = false;

        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isRoomAvailable() {
        return roomAvailable;
    }
}