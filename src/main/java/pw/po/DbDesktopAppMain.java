package pw.po;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DbDesktopAppMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        AnchorPane anchorPane = FXMLLoader
                .load(getClass().getResource("/mainPane.fxml"));
        Scene scene = new Scene(anchorPane);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Database desktop app");
        stage.show();
    }
}
