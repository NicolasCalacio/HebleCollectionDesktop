module com.example.heblecollectiondesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.heblecollectiondesktop to javafx.fxml;
    opens com.example.heblecollectiondesktop.controller to javafx.fxml;
    opens com.example.heblecollectiondesktop.model to javafx.base;

    exports com.example.heblecollectiondesktop;
    exports com.example.heblecollectiondesktop.controller;
    exports com.example.heblecollectiondesktop.model;
}