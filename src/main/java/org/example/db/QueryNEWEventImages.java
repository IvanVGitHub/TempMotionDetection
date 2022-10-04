package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import static org.example.functional.EventNew.imageToBase64;

public class QueryNEWEventImages {
    //сохраняем в таблицу eventImages изображение в строковом формате в image и id события (event) в event_id
    public static void RecordFrameToSQL(Frame frame) throws SQLException, IOException, InstantiationException, IllegalAccessException {
        //преобразуем кадр в строковое представление для хранения в БД
        Java2DFrameConverter frameLocal = new Java2DFrameConverter();
        BufferedImage bufferedImage = frameLocal.getBufferedImage(frame);
        String strImageBase64 = imageToBase64(bufferedImage);

        if(Main.getCurrentEvent() != null){
            QueryBuilder<ModelNEWEventImages> query2 = ConnectDB.getConnector().query(ModelNEWEventImages.class);
            HashMap<String, Object> item2 = new HashMap<>();
            item2.put("image", strImageBase64);
            item2.put("event_id", Main.getCurrentEvent().id);
            query2.insert(item2);
        }
    }
}
