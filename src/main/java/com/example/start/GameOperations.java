package com.example.start;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Interface defining common game operations.
 */
public interface GameOperations {

    /**
     * Confirms the game readiness.
     *
     * @param socket         the socket
     * @param bufferedReader the buffered reader
     * @return true if the game is ready to start, false otherwise
     */
    default boolean confirmGame(Socket socket, BufferedReader bufferedReader){

        Callable<Boolean> thread = new Callable<>() {

            @Override
            public Boolean call(){
                String msg = null;
                while (socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        if (msg != null && msg.equals("ready")) {
                            return true;
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
     * Receives a message from the opponent.
     *
     * @param socket         the socket
     * @param bufferedReader the buffered reader
     * @return the pair representing the position of the opponent's move
     */
    default Pair<Integer, Integer> receiveMessage(Socket socket, BufferedReader bufferedReader) {

        Callable<String> thread = new Callable<>() {

            @Override
            public String call() {
                String msg = null;
                while (socket.isConnected()) {
                    try {
                        if (!bufferedReader.ready()) {
                            Thread.sleep(10);
                        }
                        msg = bufferedReader.readLine();
                        return msg;

                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                return null;
            }
        };

        FutureTask<String> result = new FutureTask<>(thread);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(result);
        executor.shutdown();

        try {
            while(!result.isDone()){
                Thread.sleep(10);
            }
            String message = result.get();
            if (message.startsWith("selected:")) {
                String[] parts = message.split(":");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);

                Pair<Integer, Integer> position = new Pair<>(y, x);
                System.out.println("Opponent selected cell: (" + x + ", " + y + ")");

                return position;
            }

            return null;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the response from the opponent.
     *
     * @param socket         the socket
     * @param bufferedReader the buffered reader
     * @return true if the opponent's move is a hit, false otherwise
     */
    default boolean getResponse(Socket socket, BufferedReader bufferedReader){

        Callable<Boolean> thread = new Callable<>() {

            @Override
            public Boolean call(){
                String msg = null;
                while (socket.isConnected()) {
                    try {
                        while(!bufferedReader.ready()) {
                            Thread.sleep(10);
                        }
                        msg = bufferedReader.readLine();

                        return msg.equals("hit");

                    } catch (IOException | InterruptedException e) {
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
        System.out.println("Waiting for response in GameOperations");

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the username of the opponent.
     *
     * @param socket         the socket
     * @param bufferedReader the buffered reader
     * @return the opponent's username
     */
    default String getUsername(Socket socket, BufferedReader bufferedReader){

        Callable<String> thread = new Callable<>() {
            @Override
            public String call(){
                String msg = null;
                while (socket.isConnected()) {
                    try {
                        while(!bufferedReader.ready()) {
                            Thread.sleep(10);
                        }
                        msg = bufferedReader.readLine();
                        if (msg.startsWith("username:")){
                            return msg.split(":")[1];
                        }


                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                return null;
            }
        };

        FutureTask<String> result = new FutureTask<>(thread);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(result);
        executor.shutdown();
        System.out.println("Waiting for client username");

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
