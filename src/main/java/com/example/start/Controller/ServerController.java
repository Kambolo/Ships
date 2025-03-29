package com.example.start.Controller;

import com.example.start.Board.Board;
import com.example.start.LabelUpdateCallback;
import com.example.start.Server.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the server view.
 */
public class ServerController implements LabelUpdateCallback {
    @FXML
    private Pane pane;
    @FXML
    private Label clientConnectionStatus;
    private Board board;
    private Server server;
    private int portNr;
    private Parent root;
    private Stage stage;
    private Scene scene;

    /**
     * Initializes the server controller with the specified port number, stage, and server.
     *
     * @param portNr the port number
     * @param stage  the stage
     * @param server the server
     */
    @FXML
    public void initialize(int portNr, Stage stage, Server server) {
        this.portNr = portNr;
        this.stage = stage;
        this.server = server;
        clientConnectionStatus.setText("Oczekiwanie na\nprzeciwnika");
    }

    /**
     * Updates the label with the new text.
     *
     * @param newText the new text to display
     */
    @Override
    public void updateLabel(String newText) {
        Platform.runLater(() -> clientConnectionStatus.setText(newText));

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("shipsSelect.fxml"));
                root = loader.load();

                ShipsSelectController shipsSelectController = loader.getController();
                shipsSelectController.initialize(portNr, null, server);

                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
