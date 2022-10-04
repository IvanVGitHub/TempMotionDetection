package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;
import org.example.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

public class QueryNEWEvent {
    //сохраняем в таблицу event время фиксирования события (event) в time и id камеры в camera_id
    public static void MakeEvent() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        QueryBuilder<ModelNEWEvent> query1 = ConnectDB.getConnector().query(ModelNEWEvent.class);
        HashMap<String, Object> item1 = new HashMap<>();
        item1.put("time", new Timestamp(Main.getLastEventStart()));
        item1.put("camera_id", QueryAny.getCameraIDByName(Main.getCamData().host));
        query1.insert(item1);
        Main.setCurrentEvent(ConnectDB.getConnector().query(ModelNEWEvent.class).orderBy(false, "id").first());
    }
}
