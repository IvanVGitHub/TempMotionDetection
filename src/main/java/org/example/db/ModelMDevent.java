package org.example.db;

import com.bedivierre.eloquent.model.DBModel;
import java.sql.Timestamp;

public class ModelMDevent extends DBModel {
    public int id;
    public String data;
    public Timestamp time;

    @Override
    public String getTable() {
        return "MDevent";
    }

    public ModelMDevent() {
    }
}