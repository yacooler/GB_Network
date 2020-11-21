package client.controller;

import client.network.ClientHandler;
import client.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * LoginView - страница Логина
 */
public class LoginView extends BaseController {
    ClientHandler clientHandler = new ClientHandler();
    @FXML
    private Button buttonLogin;
    @FXML
    private Button buttonRegistration;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    public LoginView(ViewFactory viewFactory, String fxmlPath) {
        super(viewFactory, fxmlPath);
    }

    @FXML
    void loginButtonAction(ActionEvent event) {
        String userLogin = loginField.getText();
        String userPassword = passwordField.getText();

        ClientHandler.record.setLogin(userLogin);
        ClientHandler.record.setPassword(userPassword);

        clientHandler.sendMessage(String.format("Auth|%s|%s", userLogin, userPassword).getBytes());
    }

    @FXML
    void openRegView(ActionEvent event) {
        viewFactory.showRegistrationView();
    }
}
