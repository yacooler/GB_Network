package client.controller;

import client.network.ClientHandler;
import client.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javazoom.jl.player.Player;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * MainView - главное окно
 */
@Log4j
public class MainView extends BaseController implements Initializable {
    private static int currentColumn = 0;
    private static MusicOrientation musicOrientation = new MusicOrientation();
    private static int iterator = 0;
    private static Thread currentMusicThreadPlaying;
    private ClientHandler clientHandler = new ClientHandler();
    @FXML
    private Label nameMusic;

    @FXML
    private GridPane gridPanel;

    @FXML
    private Button stopCurrentMusic;

    public MainView(ViewFactory viewFactory, String fxmlPath) {
        super(viewFactory, fxmlPath);
    }

    /**
     * Поиск песни в проводнике
     *
     * @param fileChooser поиск файла
     */
    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View music");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP3", "*.mp3")
        );
    }

    @FXML
    void stopCurrentMusic(ActionEvent event) {
        stopCurrentMusic.setVisible(false);
        currentMusicThreadPlaying.stop();
    }

    @FXML
    @SneakyThrows
    void buttonLoadMusic(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(
                fileChooser
        );
        // Выбираем локальную песню
        File file = fileChooser.showOpenDialog(ViewFactory.getCurrentStage());

        FileUtils.copyFile(file, new File("ClientApp/src/main/resources/userMusic/" + file.getName()));

        // Загружаем локальную песню в GridPane
        loadLocalMusic(file.getName());

        clientHandler.sendMessage(String.format("UploadMusic|%s|%s", file.getName(), file).getBytes());
    }

    /**
     * <p>Загрузка локальной песни</p>
     * <p>Добавление в gridPane локальной песни</p>
     *
     * @param nameFile название песни
     */
    @SneakyThrows
    public void loadLocalMusic(String nameFile) {
        int rowCount = getRowCount(gridPanel);

        Button button = dynamicCreatePlayButton();
        gridPanel.add(button, 0, rowCount + 1);
        gridPanel.add(dynamicCreateLabel(nameFile), 1, rowCount + 1);
        musicOrientation.getStringStringHashMap().put(button.getId(), nameFile);
    }

    /**
     * При инициализации добавляются песни, которые были загружены у пользователя в БД
     *
     * @param location  location
     * @param resources resources
     */
    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<HashMap<String, byte[]>> strings = ClientHandler.record.getUserMusics();
        if (strings.size() != 0) {
            for (HashMap<String, byte[]> hashMap : strings) {
                int rowCount = getRowCount(gridPanel);
                Button button = dynamicCreatePlayButton();
                String next = hashMap.entrySet().iterator().next().getKey();
                gridPanel.add(button, 0, rowCount + 1);
                gridPanel.add(dynamicCreateLabel(next), 1, rowCount + 1);
                musicOrientation.getStringStringHashMap().put(button.getId(), next);
            }
        }
    }

    /**
     * Динамическое создание кнопки "Play" в GridPane
     *
     * @return Button
     */
    private Button dynamicCreatePlayButton() {
        Button buttonPlay = new Button();
        buttonPlay.setId(String.format("%s", iterator));
        buttonPlay.setAlignment(Pos.CENTER);
        buttonPlay.setCenterShape(true);
        buttonPlay.setMinHeight(20);
        buttonPlay.setMinWidth(Region.USE_COMPUTED_SIZE);
        buttonPlay.setPrefHeight(33);
        buttonPlay.setPrefWidth(69);
        buttonPlay.setMaxHeight(Region.USE_COMPUTED_SIZE);
        buttonPlay.setMaxHeight(Region.USE_COMPUTED_SIZE);
        buttonPlay.setText("Play");
        buttonPlay.setTextAlignment(TextAlignment.CENTER);
        buttonPlay.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> playMusic(buttonPlay.getId()));
        iterator++;
        return buttonPlay;
    }

    /**
     * Динамическое создание Label в GridPane
     *
     * @param nameMusic название песни
     * @return label
     */
    private javafx.scene.control.Label dynamicCreateLabel(String nameMusic) {
        javafx.scene.control.Label label = new javafx.scene.control.Label();
        label.setText(nameMusic);
        return label;
    }


    /**
     * Включение песни
     *
     * @param buttonId - айди кнопки
     */
    @SneakyThrows
    private void playMusic(String buttonId) {
        String nameMusic = null;

        for (Map.Entry<String, String> entry : musicOrientation.getStringStringHashMap().entrySet()) {
            if (entry.getKey().equals(buttonId)) {
                nameMusic = entry.getValue();
                break;
            }
        }

        File file = FileUtils.getFile("ClientApp/src/main/resources/userMusic/" + nameMusic);
        if (!file.exists()) {
            downloadMusic(nameMusic);
        }
        playMusic(file);
    }

    /**
     * Проигровка файла
     *
     * @param file файл
     */
    @SneakyThrows
    private void playMusic(File file) {
        FileInputStream stream = new FileInputStream(file.getPath());
        stopCurrentMusic.setVisible(true);
        Thread threadPlayMusic = new Thread(() -> {
            try {
                Player player = new Player(stream);
                player.play();
            } catch (Exception e) {
                log.info("Не удалось запустить песню");
                e.printStackTrace();
            }
        });
        threadPlayMusic.start();
        currentMusicThreadPlaying = threadPlayMusic;
    }

    /**
     * Получение кол-во строк в GriPane
     *
     * @param pane GridPane
     * @return ол-во строк
     */
    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }

    /**
     * Скачиваем песню из сервера
     *
     * @param nameMusic название песни
     */
    public void downloadMusic(String nameMusic) {
        ClientHandler.currentMusic = nameMusic;
        clientHandler.sendMessage(String.format("%s|%s|%s", "DownloadSingleMusic", ClientHandler.record.getId(), nameMusic).getBytes());
    }
}
