package com.example.start;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class Client implements GameOperations{
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private UpdateCellsCallback callback;

    public Client(Socket socket) {
        try{
            this.setSocket(socket);
            this.setBufferedReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            this.setBufferedWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        } catch (IOException e){
            System.out.println("Error joining server!");
            e.printStackTrace();
            closeEverything();
        }
    }

    public void sendMessage(String messageToServer){
        try{
            getBufferedWriter().write(messageToServer);
            getBufferedWriter().newLine(); //otherwise client would wait for more messages to come
            getBufferedWriter().flush(); //sending message even if it is not full
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while sending message");
            closeEverything();
        }
    }

    public boolean tryToConnect(){

        //System.out.println("Trying to connect to server");

        Callable<Boolean> thread = new Callable<>(){

            @Override
            public Boolean call() throws Exception {
                String msg = null;

                while(getSocket().isConnected()){
                    try {
                        msg = getBufferedReader().readLine();
                        if(msg.equals("Server is full")){
                            System.out.println("Server is full!!!!!");
                            closeEverything();
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
                return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeEverything(){
        try{
            if(getBufferedReader() != null){
                getBufferedReader().close();
            }
            if(getBufferedWriter() != null){
                getBufferedWriter().close();
            }
            if(getSocket() != null){
                getSocket().close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setCallback(UpdateCellsCallback callback) {
        this.callback = callback;
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

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }
}
