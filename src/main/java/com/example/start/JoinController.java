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
import java.net.Socket;

public class JoinController {
    @FXML
    private TextField port;
    @FXML
    private Button joinButton;

    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    public void onButtonClicked(ActionEvent evt) throws IOException {

        Client client = new Client(new Socket("localhost", Integer.parseInt(port.getText())));
        if(client.tryToConnect()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            root = loader.load();

            ClientController clientController = loader.getController();
            clientController.initialize(Integer.valueOf(port.getText()), client);

            scene = new Scene(root);
            stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("startView.fxml"));
            root = loader.load();

            scene = new Scene(root);
            stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
    }

}
