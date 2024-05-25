package com.example.start;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateOrJoinController {
    @FXML
    private Button serverButton;
    @FXML
    private Button clientButton;
    @FXML
    private Label infoLabel;

    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    public void initialize(String msg) {
        infoLabel.setText(msg);
    }

    @FXML
    public void onServerButtonClicked(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("create.fxml"));
        root = loader.load();

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onClientButtonClicked(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("join.fxml"));
        root = loader.load();

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
