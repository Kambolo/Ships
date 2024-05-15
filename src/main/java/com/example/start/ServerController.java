package com.example.start;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
    public void initialize(int portNr, Stage stage){
        try{
            this.portNr = portNr;
            this.stage = stage;
            server = new Server(new ServerSocket(this.portNr), this);
            clientConnectionStatus.setText("Waiting\nfor player");


        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error while creating server");
        }
    }

    public void drawBoard(){
        for(ArrayList<Cell> row: board.getBoardArr()){
            for(Cell cell : row){
                pane.getChildren().add(cell.getRectangle());
            }
        }
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

    public void sendCellPlacement(ActionEvent evt){
        String messageToSend = "";
        ArrayList<ArrayList<Cell>> arrayOfCells = board.getBoardArr();

        for(int row =0; row<arrayOfCells.getFirst().size(); row++){
            for(int col=0; col<arrayOfCells.size(); col++){
                if(arrayOfCells.get(row).get(col).isPicked()){
                    messageToSend += ""+row+""+col;
                    messageToSend+=",";
                }

            }
        }
        server.sendMessageToClient(messageToSend);
    }

}