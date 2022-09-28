package org.example.db;

import java.awt.*;

public class QueryDB {
    public static void testDB(TrayIcon trayIcon) {
        if(trayIcon == null)
            return;
        try {
            //query to MYSQL
            ConnectDB.getConnector();
            trayIcon.displayMessage(
                    "DB Connection successful!",
                    "Есть контакт!",
                    TrayIcon.MessageType.INFO
            );
        } catch (Exception ex) {
            trayIcon.displayMessage(
                    "DB Connection failed...",
                    "Не контачит...",
                    TrayIcon.MessageType.ERROR
            );
        }
    }
}
