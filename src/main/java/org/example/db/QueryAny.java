package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;

public class QueryAny {

    //получить id камеры по name камеры
    public static int getCameraIDByName(String camera_name) {
        try {
            QueryBuilder<ModelCamera> query = ConnectDB.getConnector().query(ModelCamera.class).where("camera_name", camera_name);

            return query.first().id;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    //получить name камеры id по камеры
    public static String getCameraNameById(int id) {
        try {
            QueryBuilder<ModelCamera> query = ConnectDB.getConnector().query(ModelCamera.class).where("id", id);

            return query.first().camera_name;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
