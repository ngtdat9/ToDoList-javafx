module com.example.todolist2javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.todolist2javafx to javafx.fxml;
    exports com.example.todolist2javafx;
    opens com.example.todolist2javafx.datamodel to javafx.fxml;
    exports com.example.todolist2javafx.datamodel;
}