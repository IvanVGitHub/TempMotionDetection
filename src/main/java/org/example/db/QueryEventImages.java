package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;
import com.sun.xml.internal.bind.v2.TODO;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.example.functional.Event.imageToBase64;

public class QueryEventImages {
    //сохраняем в таблицу eventImages изображение в строковом формате в image, id события (event) в event_id, генерируем uuid
    public static void RecordFrameToSQL(Frame frame) {
        UUID uuid = UUID.randomUUID();
        String stringUUID = uuid.toString();

        //преобразуем кадр в строковое представление для хранения в БД
        Java2DFrameConverter frameLocal = new Java2DFrameConverter();
        BufferedImage bufferedImage = frameLocal.getBufferedImage(frame);
        String strImageBase64 = imageToBase64(bufferedImage);

//        (new Thread(()->{
//
//        })).start();
        if(Main.getCurrentEvent() != null){
            try {
                QueryBuilder<ModelEventImages> query = ConnectDB.getConnector().query(ModelEventImages.class);
                HashMap<String, Object> item = new HashMap<>();
                item.put("uuid", stringUUID);
                item.put("event_id", Main.getCurrentEvent().id);
                item.put("image", strImageBase64);
                query.insert(item);
            } catch (Exception ex) {ex.printStackTrace();}
        }
        frame.close();
    }

    //TODO:запрос на запись картинок в Б долже выполняться в одном соединении
    //сохраняем в таблицу eventImages изображение в строковом формате в image, id события (event) в event_id, генерируем uuid
    public static void RecordAllFrameToSQL(ArrayList<String> listStrImageBase64) {
        HashMap<String, Object> item = new HashMap<>();

        for(String strImageBase64 : listStrImageBase64) {
            UUID uuid = UUID.randomUUID();
            String stringUUID = uuid.toString();

            item.put("uuid", stringUUID);
            item.put("event_id", Main.getCurrentEvent().id);
            item.put("image", strImageBase64);
        }

        if(Main.getCurrentEvent() != null){
            try {
                QueryBuilder<ModelEventImages> query = ConnectDB.getConnector().query(ModelEventImages.class);

                query.insert(item);
            } catch (Exception ex) {ex.printStackTrace();}
        }
    }
}
