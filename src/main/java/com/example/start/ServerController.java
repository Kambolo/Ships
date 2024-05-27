package com.example.start;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ServerController implements LabelUpdateCallback{
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


    @FXML
    public void initialize(int portNr, Stage stage, Server server){
        this.portNr = portNr;
        this.stage = stage;
        this.server = server;
        clientConnectionStatus.setText("Oczekiwanie na\nprzeciwnika");
    }

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