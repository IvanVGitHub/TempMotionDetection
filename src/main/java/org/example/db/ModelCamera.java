package org.example.db;

import com.bedivierre.eloquent.model.DBModel;

public class ModelCamera extends DBModel {
    public int id;
    public String camera_name;
    public String address;
    public int width;
    public int height;

    @Override
    public String getTable() {
        return "camera";
    }

    public ModelCamera(String camera_name, int w, int h) {
        this.camera_name = camera_name;
        this.width = w;
        this.height = h;
    }

    public ModelCamera() {
    }

    public ModelCamera(ModelCamera another) {
        this.camera_name = another.camera_name;
    }
}
