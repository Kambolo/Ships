package com.example.start.Controller;

import com.example.start.DB.DatabaseOperations;
import com.example.start.LeaderboardEntry;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the Leaderboard view.
 */
public class LeaderboardController {

    @FXML
    private TableView<LeaderboardEntry> leaderboard;
    @FXML
    private TableColumn<LeaderboardEntry, String> usernameColumn;
    @FXML
    private TableColumn<LeaderboardEntry, Integer> winsColumn;
    @FXML
    private Button menuButton;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private ObservableList<LeaderboardEntry> leaderboardData;

    /**
     * Initializes the leaderboard view.
     */
    @FXML
    private void initialize() {
        leaderboardData = DatabaseOperations.getLeaderboard("ships", "leaderboard");

        // Set up the columns
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));

        // Bind the data to the TableView
        leaderboard.setItems(leaderboardData);
    }

    /**
     * Handles the event when the menu button is clicked.
     *
     * @param evt the ActionEvent triggered by clicking the menu button
     * @throws IOException if an I/O error occurs while loading the FXML file
     */
    @FXML
    private void onMenuButtonClicked(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createOrJoin.fxml"));
        root = loader.load();

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
