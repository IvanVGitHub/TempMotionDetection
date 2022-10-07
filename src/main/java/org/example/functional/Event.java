package org.example.functional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Event {
    public static String imageToBase64(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", outputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String imgBase64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        return imgBase64;
    }
}
