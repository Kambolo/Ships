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
import java.net.ConnectException;
import java.net.Socket;

public class JoinController {
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

            Client client = new Client(new Socket(ipAdd.getText(), Integer.parseInt(port.getText())));

            if (client.tryToConnect()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("shipsSelect.fxml"));
                root = loader.load();

                ShipsSelectController shipsSelectController = loader.getController();
                shipsSelectController.initialize(Integer.valueOf(port.getText()), client, null);

                scene = new Scene(root);
                stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setScene(scene);
                stage.setOnCloseRequest(e -> client.closeEverything());
                stage.show();
            } else {
                backToMenu(evt, "Pokoj jest zajety!");
            }
        } catch (ConnectException e) {
            backToMenu(evt, "Pokoj nie istnieje!");
        } catch (NumberFormatException e){
            errorLabel.setText("Niedozwolony numer portu!");
            errorLabel.setVisible(true);
        }
    }

    private void backToMenu(ActionEvent evt, String msg) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createOrJoin.fxml"));
        root = loader.load();

        CreateOrJoinController createOrJoinController = loader.getController();
        createOrJoinController.initialize(msg);

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}
