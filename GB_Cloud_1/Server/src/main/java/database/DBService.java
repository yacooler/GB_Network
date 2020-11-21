package database;

import org.aeonbits.owner.ConfigFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {
    private static final DBConfig dbConfig = ConfigFactory.create(DBConfig.class);

    static {
        try {
            Class.forName(dbConfig.JDBC_DRIVER());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получаем подключение
     *
     * @return Connection
     */
    public static Connection getConnection() {
       try {
           return DriverManager.getConnection(dbConfig.DB_URL(), dbConfig.user(), dbConfig.password());
       } catch (SQLException throwables) {
           throw new RuntimeException("Произошла ошибка подключения к БД" + throwables);
       }
    }
}
