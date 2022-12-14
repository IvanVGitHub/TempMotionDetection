package org.example;

//import com.bedivierre.watcher.facerec.compreface.CompreFaceResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.*;
import org.example.db.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import static org.bytedeco.opencv.global.opencv_core.absdiff;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.example.functional.Event.imageToBase64;

public class Main implements Runnable {
    // Создание объекта Scanner
    static String camera = "";
    private static Frame frm;

    public static CamData getCamData() {
        return camData;
    }

    public static void setCamData(CamData camData) {
        Main.camData = camData;
    }

    private static CamData camData;
    public class CamData {
        public String host;
        public String path;
        public String username;
        public String password;
        public int width, height, framerate;
        public String cameraName;

        public String getConnectionUrl(){
            return "rtsp://" + username + ":"+password + "@" + host + path;
        }

        public CamData(String host, String path, String username, String password, int width, int height, int framerate, String cameraName){
            this.host = host;
            this.path = path;
            this.username = username;
            this.password = password;
            this.width = width;
            this.height = height;
            this.framerate = framerate;
            this.cameraName = cameraName;
        }
        public CamData(String host, String path, String username, String password, String cameraName){
            this(host, path, username, password, 1920, 1080, 25, cameraName);
        }
        public CamData(String host, String path, String username, String password, int framerate, String cameraName){
            this(host, path, username, password, 1920, 1080, framerate, cameraName);
        }
        public CamData(String host, String path, String username, String password, int width, int height, String cameraName){
            this(host, path, username, password, width, height, 25, cameraName);
        }
    }

    final String cName1 = "в кафе";
    final String cName2 = "вход Сампо";
    final String cName3 = "!не запускается!";
    final String cName4 = "!не запускается!";
    final String cName5 = "вход в бар (размазано)";
    final String cName6 = "лестница";
    final String cName7 = "холодильник";
    static final String address1 = "172.20.13.10";
    static final String address2 = "172.20.7.17";
    static final String address3 = "172.20.7.36";
    static final String address4 = "172.20.7.68";
    static final String address5 = "172.20.7.2";
    static final String address6 = "172.20.7.71";
    static final String address7 = "172.20.7.72";
    static final String user1 = "admin";
    static final String user2 = "root";
    static final String pwd1 = "asDtin38";
    static final String pwd2 = "WRPas7dZ5!";
    static final String compreFaceApiKeyLocal = "2e2916e6-1dea-4fec-bf78-fe725f678b89";
    static final String compreFaceApiKey = "5e8198f5-d1e1-4ba0-be24-d8b8db15b001";

    final int INTERVAL = 40;///you may use interval

    final int COMPREFACE_INTERVAL = 200;///you may use interval
    final int RECORD_TIMER_INTERVAL = 300;///интервал между сохранением изображения в БД
    final int RECORD_TIME = 5000;///в течении этого времени будут проходить операции сохранения изображений
    public static boolean boolWorkEventMaker = true;
    private static long eventTimeCreate;
    //список кадров перед занесением их в БД
    private static ArrayList<String> listStrImageBase64;
    public static ArrayList<String> getListStrImageBase64() {
        return listStrImageBase64;
    }
    public static void setListStrImageBase64(String listStrImageBase64) {
        Main.listStrImageBase64.add(listStrImageBase64);
    }
    public static void clearListStrImageBase64() {
        Main.listStrImageBase64.clear();
    }

    public static boolean isEventRecording = false;
    QueryEvent currEvent = null;

    public static long getLastEventStart() {
        return lastEventStart;
    }

    static long lastEventStart = 0;

    public static ModelEvent getCurrentEvent() {
        return currentEvent;
    }

    public static void setCurrentEvent(ModelEvent currentEvent) {
        Main.currentEvent = currentEvent;
    }

    public static ModelEvent currentEvent = null;
    public static long getEventTimeCreate() {
        eventTimeCreate = System.currentTimeMillis();
        return eventTimeCreate;
    }
    public void setEventTimeCreate(long eventTimeCreate) {
        this.eventTimeCreate = eventTimeCreate;
    }
    public static Frame getFrm() {
        return frm;
    }
    public static void setFrm(Frame frm) {
        Main.frm = frm;
    }

    CanvasFrame canvas;
    static boolean useCanvas = true;
    static Main gs;

    boolean interruptThread = false;
    OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
    String currentCamera = "";
    boolean nocanvas = true;

    public Main(boolean nocanvas) {
        this.nocanvas = nocanvas;
        if(useCanvas && !nocanvas) {
            canvas = new CanvasFrame("Web Cam");
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        }
    }

    private static String getArgumentValue(String[] args, String argument){
        return getArgumentValue(args, argument, "");
    }

    private static String getArgumentValue(String[] args, String argument, String defaultValue){
        String result = "";

        for(int i = 0; i < args.length; i++){
            if(!args[i].equals("-" + argument))
                continue;
            if(args.length==i + 1)
                return result;
            result = args[i+1];
            return result;
        }

        return defaultValue;
    }

    private static boolean hasKey(String[] args, String argument){
        String result = "";

        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-" + argument))
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            useCanvas = !hasKey(args, "nocanvas");
            boolean useTray = !hasKey(args, "notray");
            boolean useConsole = !hasKey(args, "noconsole");

            ConnectDB.getConnector();
            if(useTray) {
                SystemTray systemTray = SystemTray.getSystemTray();
                URL url = ClassLoader.getSystemResource("img/logoSmall.png");
                TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(url));
                trayIcon.setImageAutoSize(true);
                try {
                    systemTray.add(trayIcon);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                QueryDB.testDB(trayIcon);
            }

            String camera1 = getArgumentValue(args, "camera1");
            String camera2 = getArgumentValue(args, "camera2");
            String camera3 = getArgumentValue(args, "camera3");
            boolean nocanvas = false;

            if(!camera1.equals("")) {
                Main gs = new Main(nocanvas);
                gs.currentCamera = camera1;
                if(!nocanvas)
                    nocanvas = false;
                Thread th = new Thread(gs);
                th.start();
            }
            if(!camera2.equals("")) {
                Main gs = new Main(nocanvas);
                gs.currentCamera = camera2;
                if(!nocanvas)
                    nocanvas = true;
                Thread th = new Thread(gs);
                th.start();
            }
            if(!camera3.equals("")) {
                Main gs = new Main(nocanvas);
                gs.currentCamera = camera3;
                if(!nocanvas)
                    nocanvas = true;
                Thread th = new Thread(gs);
                th.start();
            }

            if(useConsole)
                processConsole();
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            System.exit(0);
        }
    }

    public static void processConsole() {
        boolean cycle = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Для закрытия программы введите 'exit'");
        while (cycle) {
            String command = scanner.nextLine();
            switch (command) {
                case "exit":
                    System.exit(0);
                    break;
                case "exitconsole":
                    cycle = false;
                    System.out.println("Console disabled.");
                    break;
                case "help":
                    String help = "help - descriptions of command\n";
                    help += "exit - exit program and close all cameras\n";
                    help += "exitconsole - disable console\n";
                    System.out.println(help);
                    break;
                default:
                    System.out.println("Command '" + command + "' not exists.");
            }
        }
    }

    FrameGrabber getGrabber(String camera) {
        try {
            //4 сыра
            CamData c1 = new CamData(address1, "/", user1, pwd2, cName1); //в кафе

            CamData c2 = new CamData(address2, "/Streaming/Channels/101", user1, pwd1, 1280, 800, cName2); //вход Сампо
            CamData c3 = new CamData(address3, "/Streaming/Channels/101", user1, pwd1, cName3); //!не запускается!
            CamData c4 = new CamData(address4, "/Streaming/Channels/101", user1, pwd1, cName4); //!не запускается!
            CamData c5 = new CamData(address5, "/axis-media/media.amp", user1, pwd1, cName5); //вход в бар (размазано)
            CamData c6 = new CamData(address6, "/Streaming/Channels/101", user1, pwd1, cName6); //лестница
            CamData c7 = new CamData(address7, "/Streaming/Channels/101", user1, pwd2, cName7); //холодильник
            CamData cName;
            switch (camera) {
                case  ("c1"):
                    cName = c1;
                    break;
                case ("c2"):
                    cName = c2;
                    break;
                case ("c3"):
                    cName = c3;
                    break;
                case ("c4"):
                    cName = c4;
                    break;
                case ("c5"):
                    cName = c5;
                    break;
                case ("c6"):
                    cName = c6;
                    break;
                case ("c7"):
                    cName = c7;
                    break;
                default:
                    cName = c2;//по-умолчанию
                    break;
            }
            setCamData(cName);
            System.out.println("Run camera: " + cName.cameraName);
            FFmpegFrameGrabber streamGrabber = new FFmpegFrameGrabber(getCamData().getConnectionUrl());
            streamGrabber.setFrameRate(getCamData().framerate);
            streamGrabber.setImageWidth(getCamData().width);
            streamGrabber.setImageHeight(getCamData().height);
            streamGrabber.setOption("rtsp_transport", "tcp");
            return streamGrabber;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void run(){
        try {
            FFmpegFrameGrabber streamGrabber = (FFmpegFrameGrabber) getGrabber(currentCamera);


            new File("images").mkdir();

            //OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

            System.out.println("Connecting to camera...");

            streamGrabber.start();

            System.out.println("Camera connected.");

            long timer = System.currentTimeMillis();
            long recordTimer = System.currentTimeMillis();
            lastEventStart = 0;

            Mat frame = new Mat();
            Mat firstFrame = new Mat();
            Mat gray = new Mat();
            Mat frameDelta = new Mat();
            Mat thresh = new Mat();
            MatVector cnts = new MatVector();

            frm = streamGrabber.grabImage();
            ///// ? 1.1/2
            frame = converterToMat.convert(frm);
            //convert to grayscale and set the first frame
            cvtColor(frame, firstFrame, COLOR_BGR2GRAY);
            GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

            System.out.println("Start cycle");

            while(!interruptThread) {
                frm = streamGrabber.grabImage();
                isEventRecording = (lastEventStart + RECORD_TIME) > System.currentTimeMillis();

                if(currentEvent != null && !isEventRecording)
                    currentEvent = null;
/*                if(currEvent != null && currEvent.images.size() > 9){
                    currEvent.saveImages();
                }
                if(currEvent != null && !isEventRecording) {
                    currEvent.saveImages();
                    currEvent = null;
                }*/

                ///// ? 1.2/2
                frame = converterToMat.convert(frm);

                if((timer + COMPREFACE_INTERVAL) < System.currentTimeMillis()){
                    timer = System.currentTimeMillis();//convert to grayscale
                    cvtColor(frame, gray, COLOR_BGR2GRAY);
                    GaussianBlur(gray, gray, new Size(21, 21), 0);

                    //compute difference between first frame and current frame
                    absdiff(firstFrame, gray, frameDelta);
                    //очистка памяти
                    firstFrame.release();
                    firstFrame = gray.clone();
                    //очистка памяти
                    gray.release();

                    threshold(frameDelta, thresh, 25, 255, THRESH_BINARY);
                    //очистка памяти
                    frameDelta.release();

                    dilate(thresh, thresh, new Mat());
                }

                cnts.clear();
                findContours(thresh, cnts, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
                //thresh.release();

                if(cnts.size() > 0){
                    for(int i=0; i < cnts.size(); i++) {
                        if(contourArea(cnts.get(i)) < 2000) {
                            continue;
                        }
                        Rect r = boundingRect(cnts.get(i));
                        rectangle(frame, r, new Scalar(0, 255, 0, 2));

                        //
                        //создать евент "замечено движение"
                        //
                        if(currentEvent == null) {
//                        if(currEvent == null) {
                            lastEventStart = System.currentTimeMillis();
                            QueryEvent.MakeEvent();
//                            //тестирование записи каров "пакетом"
//                            if (getListStrImageBase64() != null)
//                                QueryEventImages.RecordAllFrameToSQL(getListStrImageBase64());

//                            currEvent = QueryEvent.MakeEvent();
//                            if(!isEventRecording)
//                                currEvent.addFrameToEvent(frm);
                        }
                    }
                }

                if((recordTimer + RECORD_TIMER_INTERVAL) < System.currentTimeMillis()
                        && isEventRecording) {
                    recordTimer = System.currentTimeMillis();
//                    if(currEvent != null)
//                        currEvent.addFrameToEvent(frm);



                    QueryEventImages.RecordFrameToSQL(frm);
//                    //ТЕСТ преобразуем кадр в строковое представление и обавляем в список для записи в БД "пакетом"
//                    Java2DFrameConverter frameLocal = new Java2DFrameConverter();
//                    BufferedImage bufferedImage = frameLocal.getBufferedImage(frm);
//                    String strImageBase64 = imageToBase64(bufferedImage);
//                    //добавляем кадр в список
//                    setListStrImageBase64(strImageBase64);
                }

                if(useCanvas && !this.nocanvas && canvas != null)
                    this.canvas.showImage(converterToMat.convert(frame));
                //очистка памяти
                frame.release();


            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            System.out.println("Camera Error");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Common Error");
        }
    }

    public static CloseableHttpClient httpClient;
    static String checkInCFBase64(byte[] b){
        try {
//            File f = new File("images\\forCheck.jpg");
//            byte[] fileContent = Files.readAllBytes(f.toPath());
            String imgBase64 = Base64.getEncoder().encodeToString(b);

            if(httpClient == null)
                httpClient = HttpClients.createDefault();

//            HttpPost post = new HttpPost("http://localhost:8000/api/v1/recognition/recognize");
            HttpPost post = new HttpPost("http://172.20.3.221:8000/api/v1/recognition/recognize");
//            HttpPost post = new HttpPost("http://172.20.3.221:8000/api/v1/detection/detect");

            StringEntity entity = new StringEntity("{\"file\":\"" + imgBase64 + "\"}");

            post.setEntity(entity);

            post.addHeader("Content-type", "application/json");
            post.addHeader("x-api-key", "5e8198f5-d1e1-4ba0-be24-d8b8db15b001"); //local
//            post.addHeader("x-api-key", "5e8198f5-d1e1-4ba0-be24-d8b8db15b001"); //recognition
//            post.addHeader("x-api-key", "f91c591c-1d3c-4bca-9a07-d15ac398a9a2"); //detection
            long start = System.currentTimeMillis();
            CloseableHttpResponse response = httpClient.execute(post);
            long end = System.currentTimeMillis();
            System.out.println("Made query in " + (end - start) + "ms");
            HttpEntity responseEntity = response.getEntity();
            String s = "ERROR!";
            if (responseEntity != null) {
                s = EntityUtils.toString(responseEntity);
            }
            return s;
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }
//
//    public static CompreFaceResult processMat(Mat mat){
//        BytePointer bp = new BytePointer();
//        imencode(".jpg", mat, bp);
//
//        byte[] b = new byte[(int)bp.capacity()];
//        bp.get(b);
//        String result = "";
////        result = checkInCF();
//        result = checkInCFBase64(b);
//        Gson gson = new Gson();
//        CompreFaceResult resp = gson.fromJson(result, CompreFaceResult.class);
//
//        return resp;
//    }
}
