package org.example.db;

import com.bedivierre.eloquent.QueryBuilder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.example.functional.Event.imageToBase64;

public class QueryEvent {
    public volatile ArrayList<String> images = new ArrayList<>();
    ModelEvent model;


    //сохраняем в таблицу event время фиксирования события (event) в time и id камеры в camera_id
    public static QueryEvent MakeEvent() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        (new Thread(()->{
            //Сюда многопоточность!
        })).start();
        UUID uuid = UUID.randomUUID();
        String stringUUID = uuid.toString();

        QueryBuilder<ModelEvent> query1 = ConnectDB.getConnector().query(ModelEvent.class);
        HashMap<String, Object> item = new HashMap<>();
        item.put("uuid", stringUUID);
        item.put("camera_id", QueryAny.getCameraIDByName(Main.getCamData().cameraName));
        item.put("time", new Timestamp(Main.getLastEventStart()));
        query1.insert(item);
        QueryEvent ev = new QueryEvent();
        ev.model = ConnectDB.getConnector().query(ModelEvent.class).orderBy(false, "id").first();
        Main.setCurrentEvent(ev.model);
        return ev;
    }
    public String addFrameToEvent(Frame frame) throws SQLException, IOException, InstantiationException, IllegalAccessException {

        if(this.model == null)
            return null;
        Java2DFrameConverter frameLocal = new Java2DFrameConverter();
        BufferedImage bufferedImage = frameLocal.getBufferedImage(frame);
        String strImageBase64 = imageToBase64(bufferedImage);
        images.add(strImageBase64);
        return strImageBase64;

        //преобразуем кадр в строковое представление для хранения в БД

//        if(Main.getCurrentEvent() != null){
//            QueryBuilder<ModelEventImages> query = ConnectDB.getConnector().query(ModelEventImages.class);
//            HashMap<String, Object> item = new HashMap<>();
//            item.put("uuid", stringUUID);
//            item.put("event_id", ev.model.id);
//            item.put("image", strImageBase64);
//            query.insert(item);
//        }
//
//        frame.close();
    }


    public void saveImages() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        if(this.model == null)
            return;
        (new Thread(()-> {
            for (String image: images) {
                UUID uuid = UUID.randomUUID();
                String stringUUID = uuid.toString();
                QueryBuilder<ModelEventImages> query = ConnectDB.getConnector().query(ModelEventImages.class);
                HashMap<String, Object> item = new HashMap<>();
                item.put("uuid", stringUUID);
                item.put("event_id", this.model.id);
                item.put("image", image);
                try {
                    query.insert(item);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            images.clear();
        })).start();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        model = null;
        images.clear();
    }
}
