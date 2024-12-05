package com.example.todolist2javafx;

import com.example.todolist2javafx.datamodel.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("ToDoList");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        TodoData.getInstance().storeTodoItems();
    }

    @Override
    public void init() throws Exception {
        TodoData.getInstance().loadTodoItems();
    }


    public static void main(String[] args) {
        launch();
    }
}
