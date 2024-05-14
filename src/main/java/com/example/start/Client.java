package com.example.start;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String cellsPlacement;
    private UpdateCellsCallback callback;

    public Client(Socket socket){
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            System.out.println("Error creating server!");
            e.printStackTrace();
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void sendMessageToServer(String messageToServer){
        try{
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine(); //otherwise client would wait for more messages to come
            bufferedWriter.flush(); //sending message even if it is not full
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while sending message");
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    //making a recevieve message func in thread because we dont want the program to stop while waiting for message
    public void receiveMessageFromServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String prev ="";
                while(socket.isConnected()){
                    try{

                        cellsPlacement = bufferedReader.readLine();
                        if(!prev.equals(cellsPlacement)){
                            callback.updateCells(cellsPlacement);
                        }
                    } catch (IOException e){
                        System.out.println("Error receiving a message!");
                        e.printStackTrace();
                    }

                }
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }).start();
    }

    public boolean tryToConnect(){

        //System.out.println("Trying to connect to server");

        Callable<Boolean> thread = new Callable<>(){

            @Override
            public Boolean call() throws Exception {
                while(socket.isConnected()){
                    String msg = null;
                    try {
                        msg = bufferedReader.readLine();
                        if(msg.equals("Server is full")){
                            System.out.println("Server is full!!!!!");
                            closeEverything(socket, bufferedWriter, bufferedReader);
                            return false;
                        } else break;

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
            if(result.get()) receiveMessageFromServer();{}
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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

    public void setCallback(UpdateCellsCallback callback) {
        this.callback = callback;
    }
}
