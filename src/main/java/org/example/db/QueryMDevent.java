package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;

import java.sql.Timestamp;
import java.util.HashMap;

public class QueryMDevent {
    public static void addEventToDB(String strImageBase64, Timestamp sqlTimestamp) {
        try {
            //query to MYSQL
            QueryBuilder<ModelMDevent> query1 = ConnectDB.getConnector().query(ModelMDevent.class);
            HashMap<String, Object> item1 = new HashMap<>();
            item1.put("time", sqlTimestamp);
            query1.insert(item1);

//            String s =  ConnectDB.getConnector().query(ModelMDEvent.class).orderBy(false, "id").toSql();
            ModelMDevent event = ConnectDB.getConnector().query(ModelMDevent.class).orderBy(false, "id").first();

            //query to MYSQL
            QueryBuilder<ModelMDeventImages> query2 = ConnectDB.getConnector().query(ModelMDeventImages.class);
            HashMap<String, Object> item2 = new HashMap<>();
            item2.put("image", strImageBase64);
            item2.put("MDevent_id", event.id);
            query2.insert(item2);
        } catch (Exception ex) {
            //shows line with error in console
            ex.printStackTrace();
        }
    }
}
