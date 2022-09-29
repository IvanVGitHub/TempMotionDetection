package org.example.db;

import com.bedivierre.eloquent.model.DBModel;

public class ModelMDeventImages extends DBModel {
    public int id;
    public int MDevent_id;
    public String image;

    @Override
    public String getTable() {
        return "MDeventImages";
    }

    public ModelMDeventImages() {
    }
}
