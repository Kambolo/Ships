package com.example.start;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for handling the main menu options of creating or joining a game, or viewing the leaderboard.
 */
public class CreateOrJoinController {
    @FXML
    private Button serverButton;
    @FXML
    private Button clientButton;
    @FXML
    private Label infoLabel;
    @FXML
    private Button leaderboardButton;

    private Parent root;
    private Stage stage;
    private Scene scene;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     *
     * @param msg the message to be displayed on the infoLabel
     */
    @FXML
    public void initialize(String msg) {
        infoLabel.setText(msg);
    }

    /**
     * Handles the action event triggered when the server button is clicked.
     * Loads the create server screen.
     *
     * @param evt the action event triggered by clicking the server button
     * @throws IOException if an I/O error occurs during loading the FXML file
     */
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

    /**
     * Handles the action event triggered when the client button is clicked.
     * Loads the join client screen.
     *
     * @param evt the action event triggered by clicking the client button
     * @throws IOException if an I/O error occurs during loading the FXML file
     */
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

    /**
     * Handles the action event triggered when the leaderboard button is clicked.
     * Loads the leaderboard screen.
     *
     * @param evt the action event triggered by clicking the leaderboard button
     * @throws IOException if an I/O error occurs during loading the FXML file
     */
    @FXML
    public void onLeaderboardButtonClicked(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("leaderboard.fxml"));
        root = loader.load();

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
