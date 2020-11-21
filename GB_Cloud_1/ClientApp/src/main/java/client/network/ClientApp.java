package client.network;

import client.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewFactory.getInstance().showConnection();
    }

    public void startApp() {
        launch();
    }
}
