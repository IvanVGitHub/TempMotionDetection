package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static org.example.functional.Event.imageToBase64;

public class QueryEventImages {
    //сохраняем в таблицу eventImages изображение в строковом формате в image и id события (event) в event_id
    public static void RecordFrameToSQL(Frame frame) throws SQLException, IOException, InstantiationException, IllegalAccessException {
        UUID uuid = UUID.randomUUID();
        String stringUUID = uuid.toString();

        //преобразуем кадр в строковое представление для хранения в БД
        Java2DFrameConverter frameLocal = new Java2DFrameConverter();
        BufferedImage bufferedImage = frameLocal.getBufferedImage(frame);
        String strImageBase64 = imageToBase64(bufferedImage);

        if(Main.getCurrentEvent() != null){
            QueryBuilder<ModelEventImages> query = ConnectDB.getConnector().query(ModelEventImages.class);
            HashMap<String, Object> item = new HashMap<>();
            item.put("uuid", stringUUID);
            item.put("event_id", Main.getCurrentEvent().id);
            item.put("image", strImageBase64);
            query.insert(item);
        }

        frame.close();
    }
}
