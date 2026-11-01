module com.example.todolist2javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.todolist2javafx to javafx.fxml;
    exports com.example.todolist2javafx;
    opens com.example.todolist2javafx.datamodel to javafx.fxml;
    exports com.example.todolist2javafx.datamodel;
}