module com.example.start {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.start to javafx.fxml;
    exports com.example.start;
    exports com.example.start.Board;
    opens com.example.start.Board to javafx.fxml;
    exports com.example.start.Controller;
    opens com.example.start.Controller to javafx.fxml;
    exports com.example.start.Client;
    opens com.example.start.Client to javafx.fxml;
    exports com.example.start.Server;
    opens com.example.start.Server to javafx.fxml;
    exports com.example.start.DB;
    opens com.example.start.DB to javafx.fxml;
}