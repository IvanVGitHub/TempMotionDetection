package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;
import org.example.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class QueryEvent {
    //сохраняем в таблицу event время фиксирования события (event) в time и id камеры в camera_id
    public static void MakeEvent() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        UUID uuid = UUID.randomUUID();
        String stringUUID = uuid.toString();

        QueryBuilder<ModelEvent> query1 = ConnectDB.getConnector().query(ModelEvent.class);
        HashMap<String, Object> item = new HashMap<>();
        item.put("uuid", stringUUID);
        item.put("camera_id", QueryAny.getCameraIDByName(Main.getCamData().cameraName));
        item.put("time", new Timestamp(Main.getLastEventStart()));
        query1.insert(item);
        Main.setCurrentEvent(ConnectDB.getConnector().query(ModelEvent.class).orderBy(false, "id").first());
    }
}
