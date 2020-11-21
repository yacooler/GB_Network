package client.network;

import client.userCredential.Record;
import client.view.ViewFactory;
import javafx.application.Platform;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

@Log4j
public class ClientHandler {

    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private final ViewFactory viewFactory = ViewFactory.getInstance();
    private final byte[] BYTE_BUFFER = new byte[1024];
    public static Record record = new Record();
    public static String currentMusic = null;

    public ClientHandler() {}

    public static void main(String[] args) {
        new ClientHandler().startClientServer();
    }

    @SneakyThrows
    public void startClientServer() {
        ClientApp clientApp = new ClientApp();
        socket = new Socket("localhost", 8189);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        log.info("Client is started");
        Thread thread = new Thread(() -> {
            try {
                sendMessage("User connection".getBytes());
                while (true) {
                    int cnt = dataInputStream.read(BYTE_BUFFER);

                    String msg = new String(BYTE_BUFFER, 0, cnt);

                    if (msg.equals("ConnectionTrue")) {
                        Thread.sleep(2500);
                        Platform.runLater(viewFactory::showLoginView);
                    } else if (msg.equals("AuthTrue")) {
                        Platform.runLater(viewFactory::showConnection);
                        sendMessage("getUserCredential".getBytes());
                    } else if (msg.equals("AuthFalse")) {
                        Platform.runLater(viewFactory::showErrorView);
                        log.info("Не удалось авторизоваться.");
                    } else if (msg.split("\\|")[0].equals("UserCredential")) {
                        writeUserCredential(msg);
                        Thread.sleep(1000);
                        sendMessage(String.format("%s|%s", "DownloadMusicNames", record.getId()).getBytes());
                        Thread.sleep(2500);
                        Platform.runLater(viewFactory::showMainView);
                    } else if (msg.split("\\|")[0].equals("musicNamesDownloaded")) {
                        writeUserMusicNames(msg);
                    } else if (msg.split("\\|")[0].equals("DownloadSingleMusic")) {
                        writeMusicToMap(msg);
                    } else if (msg.equals("RegistrationTrue")) {
                        log.info("Пользователь зарегистрирован.");
                    } else if (msg.equals("RegistrationFalse")) {
                        log.info("Не удалось зарегистрировать пользователя.");
                    } else if (msg.equals("MusicAddTrue")) {
                        log.info("Музыка загружена на сервер.");
                    } else if (msg.equals("MusicAddFalse")) {
                        log.error("Не удалось загрузить на сервер музыку.");
                    } else if (msg.equals("/end")) {
                        log.info("Сессия прекращена.");
                        closeConnections();
                        break;
                    }
                }
            } catch (IllegalArgumentException | InterruptedException | IOException exception) {
                log.info("Не удалось подключиться к серверу %s", exception);
            } finally {
                closeConnections();
            }
        });
        thread.start();
        clientApp.startApp();
    }

    /**
     * Отправка сообщение.
     *
     * @param bytesMessage байты сообщения.
     */
    public void sendMessage(byte[] bytesMessage) {
        try {
            dataOutputStream.write(bytesMessage);
            dataOutputStream.flush();
        } catch (IOException e) {
            log.error("Не удалось отправить сообщение");
        }
    }

    /**
     * Закрываем соединения.
     */
    private void closeConnections() {
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            log.info("Не удалось закрыть %s", e);
        }
    }

    private void writeUserCredential(String serverMessage) {
        String[] messageSplit = serverMessage.split("\\|");
        record.setId(Integer.parseInt(messageSplit[1]));
        record.setLogin(messageSplit[2]);
        record.setPassword(messageSplit[3]);
    }

    private void writeUserMusicNames(String serverMessage) {
        String[] messageSplit = serverMessage.split("\\|");
        for (int i = 1; i < messageSplit.length; i++) {
            record.getUserMusics().add(getHashMapMusic(messageSplit[i]));
        }
    }

    private HashMap<String, byte[]> getHashMapMusic(String nameFile) {
        HashMap<String, byte[]> hashMap = new HashMap<>();
        hashMap.put(nameFile.replaceAll(" ", ""), null);
        return hashMap;
    }

    @SneakyThrows
    private void writeMusicToMap(String serverMessage) {
        String[] messageSplit = serverMessage.split("\\|");
        String stringBytes = messageSplit[1].substring(1, messageSplit[1].length() - 1);
        String[] getBytesMusic = stringBytes.split(", ");

        byte[] musicBytes = new byte[serverMessage.length()];
        for (int i = 0; i < getBytesMusic.length; i++) {
            musicBytes[i] = Byte.parseByte(getBytesMusic[i]);
        }

        FileUtils.writeByteArrayToFile(new File("D:\\bazha\\Documents\\Java\\Geekbrains\\GB_Cloud_1\\GB_Cloud_1\\ClientApp\\src\\main\\resources\\userMusic\\test.mp3"), musicBytes);
    }
}
