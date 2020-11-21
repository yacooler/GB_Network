package client.view;

import client.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ViewFactory - для представления окон
 */
@Log4j
public class ViewFactory {

    private static final ThreadLocal<ViewFactory> instance = ThreadLocal.withInitial(ViewFactory::new);
    @Getter
    private static Stage currentStage = null;

    public static ViewFactory getInstance() {
        return instance.get();
    }

    /**
     * Открытие окна подключения к серверу
     */
    public void showConnection() {
        BaseController baseController = new ConnectServerView(this, ViewPath.CONNECTION_VIEW_PATH.getPath());
        initStage(baseController, ViewPath.CONNECTION_VIEW_PATH.getPath(), "Connect", StageStyle.TRANSPARENT, Color.TRANSPARENT);
    }

    /**
     * Открытие страницы логина
     */
    public void showLoginView() {
        BaseController baseController = new LoginView(this, ViewPath.LOGIN_VIEW_PATH.getPath());
        initStage(baseController, ViewPath.LOGIN_VIEW_PATH.getPath(), "Siqn in", null, null);
    }

    /**
     * Открытие страницы логина
     */
    public void showErrorView() {
        BaseController baseController = new ErrorView(this, ViewPath.ERROR_VIEW.getPath());
        initStage(baseController, ViewPath.ERROR_VIEW.getPath(), "Error auth", null, null);
    }

    /**
     * Открытие главного окна
     */
    public void showMainView() {
        BaseController baseController = new MainView(this, ViewPath.MAIN_VIEW_PATH.getPath());
        initStage(baseController, ViewPath.MAIN_VIEW_PATH.getPath(), "Main window", null, null);
    }

    /**
     * Открытие главного окна
     */
    public void showRegistrationView() {
        BaseController baseController = new RegistrationView(this, ViewPath.REGISTRATION_VIEW.getPath());
        initStage(baseController, ViewPath.REGISTRATION_VIEW.getPath(), "Registration window", null, null);
    }

    /**
     * Инициализация окна
     *
     * @param baseController контроллер
     * @param path           путь для окна
     * @param title          заголовок сцена
     * @param stageStyle     стиль сцены
     * @param color          цвет фона
     */
    private void initStage(BaseController baseController, String path, String title, StageStyle stageStyle, Color color) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlPath()));
        URL url;
        try {
            url = new File(path).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Не удалось найти путь страницы, %s", e));
        }
        fxmlLoader.setLocation(url);
        fxmlLoader.setController(baseController);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(title);

        if (stageStyle != null) {
            stage.initStyle(stageStyle);
            scene.setFill(color);
        }

        stage.setScene(scene);
        if (currentStage != null) {
            closeStage(currentStage);
        }

        currentStage = stage;
        stage.show();
        log.info("Открыто окно: " + stage.getTitle());
    }

    /**
     * Закрытие окна
     *
     * @param stage текущее окно
     */
    public void closeStage(Stage stage) {
        log.info("Закрыто окно " + stage.getTitle());
        stage.close();
    }
}
