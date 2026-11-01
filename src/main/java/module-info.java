module io.github.ngtdat9.todolist {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    exports io.github.ngtdat9.todolist to javafx.graphics;
    opens io.github.ngtdat9.todolist.ui to javafx.fxml;
}
