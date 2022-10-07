package org.example.db;

import com.bedivierre.eloquent.model.DBModel;
import java.sql.Timestamp;

public class ModelEvent extends DBModel {
    public int id;
    public String uuid;
    public String camera_id;
    public String plugin_id;
    public String data;
    public Timestamp time;

    @Override
    public String getTable() {
        return "event";
    }

    public ModelEvent() {
    }
}