package client.controller;

import client.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ErrorView extends BaseController {

    @FXML
    private Label title;

    public ErrorView(ViewFactory viewFactory, String fxmlPath) {
        super(viewFactory, fxmlPath);
    }

    @FXML
    void tryAgainAuth(ActionEvent event) {
        ViewFactory.getInstance().showLoginView();
    }
}
