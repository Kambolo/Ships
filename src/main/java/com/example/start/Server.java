package com.example.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends ServerController{
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private LabelUpdateCallback callback;

    public Server(ServerSocket serverSocket, LabelUpdateCallback callback) {
        this.serverSocket = serverSocket;
        this.callback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = serverSocket.accept(); //halt until client connects, and return socket that enables conection
                    callback.updateLabel("Player Connected");
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                } catch (IOException e){
                    System.out.println("Error creating client!");
                    e.printStackTrace();
                    closeEverything(socket, bufferedWriter, bufferedReader);
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
                closeEverything(socket, bufferedWriter, bufferedReader);
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

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}