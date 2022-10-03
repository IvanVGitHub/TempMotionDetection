package org.example.db;

import com.bedivierre.eloquent.DB;

public class ConnectDB {
    static DB connector;

    public static DB getConnector() {
        return  connector = new DB(
                "172.20.3.231",
                "test",
                "ivanUser",
                "Qwerty!@#456"
        );
    }
}
