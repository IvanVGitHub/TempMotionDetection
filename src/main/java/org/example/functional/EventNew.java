package org.example.functional;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.Main;
import org.example.db.QueryMDevent;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class EventNew {
    public static String imageToBase64(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());
    }

    public void createImg(org.bytedeco.javacv.Frame frm, long timeNow) {
        try {
            //преобразуем
            Java2DFrameConverter frame = new Java2DFrameConverter();
            BufferedImage bufferedImage = frame.getBufferedImage(frm);
            String strImageBase64 = imageToBase64(bufferedImage);

            Timestamp sqlTimestamp = new Timestamp(timeNow);
            String nameImage = String.valueOf(sqlTimestamp);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//            Date date = new Date(System.currentTimeMillis());
//            String nameImage = String.valueOf(sdf.format(date));
//
//            ImageIO.write(bufferedImage, "png", new File( "images/" + nameImage + ".png"));
//            ImageIO.write(bufferedImage, "jpeg", new File("images/" + nameImage + ".jpeg"));
            //преобразуем картинку

            QueryMDevent.addEventToDB(strImageBase64, sqlTimestamp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
