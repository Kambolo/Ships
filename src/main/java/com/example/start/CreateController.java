package com.example.start;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateController {
    @FXML
    private TextField port;
    @FXML
    private TextField ipAdd;
    @FXML
    private Button createButton;

    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    public void onButtonClicked(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
        root = loader.load();

        ServerController serverController = loader.getController();
        serverController.initialize(Integer.valueOf(port.getText()));

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
