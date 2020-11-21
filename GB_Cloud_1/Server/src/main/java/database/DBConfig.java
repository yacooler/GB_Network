package database;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("file:Server/src/main/resources/App.properties")
public interface DBConfig extends Config {

    @Key("db.user")
    String user();

    @Key("bd.password")
    String password();

    @Key("JDBC.driver")
    String JDBC_DRIVER();

    @Key("db.url")
    String DB_URL();
}
