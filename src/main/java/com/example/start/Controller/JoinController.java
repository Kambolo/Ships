package com.example.start.Controller;

import com.example.start.Client.Client;
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

/**
 * Controller for joining a game room.
 */
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

    /**
     * Initializes the JoinController.
     */
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    /**
     * Handles the button click event.
     *
     * @param evt the ActionEvent
     * @throws IOException if an I/O error occurs
     */
    @FXML
    public void onButtonClicked(ActionEvent evt) throws IOException {
        try {
            String ipadd = ipAdd.getText();
            if(ipadd == ""){
                ipadd = "localhost";
            }
            Client client = new Client(new Socket(ipadd, Integer.parseInt(port.getText())));

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
                backToMenu(evt, "Pokój jest zajęty!");
            }
        } catch (ConnectException e) {
            backToMenu(evt, "Pokój nie istnieje!");
        } catch (NumberFormatException e){
            errorLabel.setText("Niedozwolony numer portu!");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Navigates back to the main menu.
     *
     * @param evt the ActionEvent
     * @param msg the message to display
     * @throws IOException if an I/O error occurs
     */
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
