package com.example.start;

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

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

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
        } catch (IllegalStateException e){
            errorLabel.setText("Niedowzwolony numer portu!");
            errorLabel.setVisible(true);
        }
    }

    private boolean portValidation(){
        String temp = port.getText();

        for(Character c : temp.toCharArray()){
            if(!Character.isDigit(c)) return false;
        }
        return true;
    }
}
