package client.controller;

import client.network.ClientHandler;
import client.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Страница регистрации
 */
public class RegistrationView extends BaseController {

    @FXML
    private Button buttonReg;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private TextField fieldLogin;

    public RegistrationView(ViewFactory viewFactory, String fxmlPath) {
        super(viewFactory, fxmlPath);
    }

    @FXML
    void buttonRegistration(ActionEvent event) {
        String userLogin = fieldLogin.getText();
        String userPassword = fieldPassword.getText();

        new ClientHandler().sendMessage(String.format("Registration|%s|%s", userLogin, userPassword).getBytes());
        ViewFactory.getInstance().showLoginView();
    }
}
