package com.example.start;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

public interface GameOperations {
    public default boolean confirmGame(Socket socket, BufferedReader bufferedReader) throws IOException {

        Callable<Boolean> thread = new Callable<>() {

            @Override
            public Boolean call() throws Exception {
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
}
