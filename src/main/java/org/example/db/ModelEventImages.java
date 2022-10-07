package org.example.db;

import com.bedivierre.eloquent.model.DBModel;

public class ModelEventImages extends DBModel {
    public int id;
    public String uuid;
    public int event_id;
    public String image;

    @Override
    public String getTable() {
        return "eventImages";
    }

    public ModelEventImages() {
    }
}
