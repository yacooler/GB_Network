package client.userCredential;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@ToString
public class Record {
    private static final ThreadLocal<Record> instance = ThreadLocal.withInitial(Record::new);

    public static Record getInstance() {
        return instance.get();
    }

    int id;
    String login;
    String password;
    List<HashMap<String, byte[]>> userMusics = new ArrayList<>();
}
