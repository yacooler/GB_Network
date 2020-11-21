package database;


import client.userCredential.Record;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j
public class BaseAuthService {

    /**
     * Запрос на поиск пользователя по логину и паролю.
     *
     * @param userLogin    логин пользователя.
     * @param userPassword пароль пользователя.
     * @return - AuthTrue: пользователь найден; AuthFalse: Пользователь не найден.
     */
    public String findUser(String userLogin, String userPassword) {
        Optional<Record> userByLoginAndPassword = findUserByLoginAndPassword(userLogin, userPassword);
        if (userByLoginAndPassword.isPresent()) {
            return "AuthTrue";
        } else return "AuthFalse";
    }

    /**
     * Запрос в БД на создание пользователя
     *
     * @param userLogin    логин пользователя
     * @param userPassword пароль пользователя
     * @return - RegistrationFalse: пользователь добавлен; RegistrationTrue: пользователь не добавлен.
     */
    public String userRegistration(String userLogin, String userPassword) {
        return (userRegistrationByLoginAndPassword(userLogin, userPassword)) ? "RegistrationFalse" : "RegistrationTrue";
    }

    /**
     * Запрос в БД на создание пользователя
     *
     * @param musicName название музыки
     * @param bytes     байты
     * @return - MusicAddTrue: музыка добавлена; MusicAddFalse: музыка не добавлена.
     */
    public String uploadMusic(String musicName, byte[] bytes) {
        return (uploadMusicBytes(Record.getInstance().getId(), musicName, bytes)) ? "MusicAddFalse" : "MusicAddTrue";
    }

    /**
     * Запрос на получение всех названий песен пользоватля
     *
     * @param user_id айди пользователя
     * @return - Optional<Record> запроса
     */
    public List<String> downloadMusicName(int user_id) {
        return getUserMusic(user_id).get();
    }

    /**
     * Запрос на скачивание одной песни
     *
     * @param user_id   айди пользователя
     * @param nameMusic название песни
     * @return байты песни
     */
    public byte[] downloadSingleMusic(int user_id, String nameMusic) {
        return getSingleMusic(user_id, nameMusic);
    }

    /**
     * Запрос на поиск пользователя по логину и паролю
     *
     * @param userLogin    логин пользователя
     * @param userPassword пароль пользователя
     * @return - Optional<Record> запроса
     */
    private Optional<Record> findUserByLoginAndPassword(String userLogin, String userPassword) {
        log.info("Запрос на поиск пользователя в БД");
        Connection connection = DBService.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = '" +
                    userLogin + "' and password = '" + userPassword + "'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Record.getInstance().setId(resultSet.getInt("user_id"));
                Record.getInstance().setLogin(resultSet.getString("login"));
                Record.getInstance().setPassword(resultSet.getString("password"));
                return Optional.of(Record.getInstance());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * Запрос в БД на создание пользователя
     *
     * @param userLogin    логин пользователя
     * @param userPassword пароль пользователя
     * @return boolean запроса
     */
    private boolean userRegistrationByLoginAndPassword(String userLogin, String userPassword) {
        log.info("Запрос на регистрацию пользователя в БД");
        try (Connection connection = DBService.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
            statement.setString(1, userLogin);
            statement.setString(2, userPassword);
            return statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    /**
     * Запрос в БД на создание пользователя
     *
     * @param user_id   айди пользователя
     * @param musicName название музыки
     * @param bytes     байты
     * @return boolean запроса
     */
    private boolean uploadMusicBytes(int user_id, String musicName, byte[] bytes) {
        log.info("Запрос на добавление музыки в БД");
        try (Connection connection = DBService.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user_music (user_id, music, music_name) VALUES (?, ?, ?)");
            statement.setInt(1, user_id);
            statement.setBytes(2, bytes);
            statement.setString(3, musicName);
            return statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    /**
     * Запрос на получение всех песен пользователя
     *
     * @param user_id айди пользователя
     * @return - Optional<Record> запроса
     */
    private Optional<List<String>> getUserMusic(int user_id) {
        log.info("Запрос на скачивание названий песен");
        Connection connection = DBService.getConnection();
        List<String> musicNames = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT music_name FROM user_music\n" +
                    "INNER JOIN users\n" +
                    "ON user_music.user_id = users.user_id\n" +
                    "WHERE users.user_id = " + user_id + "");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                musicNames.add(resultSet.getString("music_name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return Optional.of(musicNames);
    }

    /**
     * Запрос на получение всех песен пользователя
     *
     * @param user_id   айди пользователя
     * @param nameMusic название песни
     */
    private byte[] getSingleMusic(int user_id, String nameMusic) {
        log.info("Запрос на скачивание музыки");
        Connection connection = DBService.getConnection();
        byte[] bytes = new byte[4096];
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT music FROM user_music\n" +
                    "INNER JOIN users\n" +
                    "ON user_music.user_id = users.user_id\n" +
                    "WHERE users.user_id = " + user_id + " AND user_music.music_name = '" + nameMusic + "'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bytes = (resultSet.getString("music")).getBytes();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return bytes;
    }
}
