package org.example.db;

import com.bedivierre.eloquent.model.DBModel;

public class ModelNEWEventImages extends DBModel {
    public int id;
    public int event_id;
    public String image;

    @Override
    public String getTable() {
        return "NEWeventImages";
    }

    public ModelNEWEventImages() {
    }
}
