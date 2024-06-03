package com.example.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main class responsible for starting the JavaFX application.
 */
public class Main extends Application {
    private static String username;

    /**
     * Gets the username.
     *
     * @return the username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public static void setUsername(String username) {
        Main.username = username;
    }

    /**
     * The main entry point for the JavaFX application.
     *
     * @param stage the primary stage for this application
     * @throws IOException if an error occurs during loading of the FXML file
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("startView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Ships Game");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
