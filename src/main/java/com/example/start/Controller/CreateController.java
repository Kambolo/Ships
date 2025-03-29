package com.example.start.Controller;

import com.example.start.Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Controller class for handling the creation of a server.
 */
public class CreateController {
    @FXML
    private TextField port;
    @FXML
    private TextField ipAdd;
    @FXML
    private Label errorLabel;

    private Parent root;
    private Stage stage;
    private Scene scene;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    /**
     * Handles the action event triggered when the create button is clicked.
     * Validates the port and initializes the server if valid.
     *
     * @param evt the action event triggered by clicking the button
     * @throws IOException if an I/O error occurs
     */
    @FXML
    public void onButtonClicked(ActionEvent evt) throws IOException {
        try {
            if (port.getText().isEmpty() || !portValidation()) throw new IllegalStateException();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
            root = loader.load();

            scene = new Scene(root);
            stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(scene);

            ServerController serverController = loader.getController();
            Server server = new Server(new ServerSocket(Integer.valueOf(port.getText())), serverController);
            serverController.initialize(Integer.valueOf(port.getText()), stage, server);

            stage.setOnCloseRequest(e -> server.closeEverything());
            stage.show();
        } catch (IllegalStateException e) {
            errorLabel.setText("Niedozwolony numer portu!");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Validates the port number entered by the user.
     *
     * @return true if the port number is valid, false otherwise
     */
    private boolean portValidation() {
        String temp = port.getText();

        for (Character c : temp.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
