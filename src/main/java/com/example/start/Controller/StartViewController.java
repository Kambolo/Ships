package com.example.start.Controller;

import com.example.start.Main;
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

/**
 * Controller class for the start view.
 */
public class StartViewController {

    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    private Button playButton;
    @FXML
    private TextField username;
    @FXML
    private Label wrongUsernameLabel;

    /**
     * Handles button click event.
     *
     * @param evt Action event
     * @throws IOException If an I/O error occurs
     */
    @FXML
    protected void onButtonClicked(ActionEvent evt) throws IOException {
        Main.setUsername(username.getText());

        if(Main.getUsername().equals("")){
            wrongUsernameLabel.setText("Musisz podać nazwę użytkownika!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("createOrJoin.fxml"));
        root = loader.load();

        CreateOrJoinController controller = loader.getController();
        controller.initialize("");

        scene = new Scene(root);
        stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
