module com.example.start {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.start to javafx.fxml;
    exports com.example.start;
}