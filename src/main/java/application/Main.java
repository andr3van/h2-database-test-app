package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppController.getInstance(primaryStage);

        primaryStage.setOnCloseRequest(evt -> primaryStage.hide());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
