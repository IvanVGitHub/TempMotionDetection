package org.example.functional;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.db.QueryDB;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewEvent {
    public void createImg(org.bytedeco.javacv.Frame frm) {
        try {
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bi = converter.getBufferedImage(frm);

            long now = System.currentTimeMillis();
            Timestamp sqlTimestamp = new Timestamp(now);
            String nameImage = String.valueOf(sqlTimestamp);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = new Date(System.currentTimeMillis());
//            String nameImage = String.valueOf(sdf.format(date));

//            ImageIO.write(bi, "png", new File( "images/" + nameImage + ".png"));
//            ImageIO.write(bi, "jpeg", new File("images/" + nameImage + ".jpeg"));

            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/logoSmall.png")));
            trayIcon.setImageAutoSize(true);
            try {
                systemTray.add(trayIcon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            QueryDB.testDB(trayIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
