package org.example;

//import com.bedivierre.watcher.facerec.compreface.CompreFaceResult;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.example.db.ConnectDB;
import org.example.functional.NewEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.bytedeco.opencv.global.opencv_core.absdiff;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Main implements Runnable{

    public class CamData {
        public String host;
        public String path;
        public String username;
        public String password;
        public int width, height, framerate;

        public String getConnectionUrl(){
            return "rtsp://" + username + ":"+password + "@" + host + path;
        }
        public CamData(String host, String path, String username, String password, int width, int height, int framerate){
            this.host = host;
            this.path = path;
            this.username = username;
            this.password = password;
            this.width = width;
            this.height = height;
            this.framerate = framerate;
        }
        public CamData(String host, String path, String username, String password){
            this(host, path, username, password, 1920, 1080, 25);
        }
        public CamData(String host, String path, String username, String password, int framerate){
            this(host, path, username, password, 1920, 1080, framerate);
        }
        public CamData(String host, String path, String username, String password, int width, int height){
            this(host, path, username, password, width, height, 25);
        }
    }

    static String address1 = "172.20.7.17";
    static String address2 = "172.20.7.36";
    static String address3 = "172.20.7.68";
    static String address4 = "172.20.13.10";
    static String user = "admin";
    static String user2 = "root";
    static String pwd1 = "asDtin38";
    static String pwd2 = "WRPas7dZ5!";
    static String compreFaceApiKeyLocal = "2e2916e6-1dea-4fec-bf78-fe725f678b89";
    static String compreFaceApiKey = "5e8198f5-d1e1-4ba0-be24-d8b8db15b001";


    final int INTERVAL = 40;///you may use interval
    final int COMPREFACE_INTERVAL = 500;///you may use interval
    CanvasFrame canvas;

    public Main() {
        canvas =  new CanvasFrame("Web Cam");
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws Exception {
        try {
            Main gs = new Main();
            Thread th = new Thread(gs);
            th.start();

            ConnectDB.init();
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    FrameGrabber getGrabber(){
        try {
            //4 сыра
            CamData c1 = new CamData(address4, "/", user, pwd2);

            CamData c2 = new CamData(address1, "/Streaming/Channels/101", user, pwd1, 1280, 800);
            CamData c3 = new CamData(address2, "/Streaming/Channels/101", user, pwd1);
            CamData c4 = new CamData(address3, "/Streaming/Channels/101", user, pwd1);
            CamData c5 = new CamData("172.20.7.2", "/axis-media/media.amp", user, pwd1);

            CamData current = c2;
            FFmpegFrameGrabber streamGrabber = new FFmpegFrameGrabber(current.getConnectionUrl());
            streamGrabber.setFrameRate(current.framerate);
            streamGrabber.setImageWidth(current.width);
            streamGrabber.setImageHeight(current.height);
            streamGrabber.setOption("rtsp_transport", "tcp");
            return streamGrabber;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void run(){
        try {
            FFmpegFrameGrabber streamGrabber = (FFmpegFrameGrabber) getGrabber();
            OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

            new File("images").mkdir();

            //OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

            System.out.println("Connecting to camera...");

            streamGrabber.start();

            System.out.println("Camera connected.");

            long timer = System.currentTimeMillis();

            Frame frm;
            Mat frame = new Mat();
            Mat firstFrame = new Mat();
            Mat gray = new Mat();
            Mat frameDelta = new Mat();
            Mat thresh = new Mat();
            MatVector cnts = new MatVector();

            frm = streamGrabber.grabImage();
            frame = converterToMat.convert(frm);
            //convert to grayscale and set the first frame
            cvtColor(frame, firstFrame, COLOR_BGR2GRAY);
            GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);
//
//
//
//            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
//                    "C:\\Users\\Иван\\IdeaProjects\\TempMotionDetection\\video.avi",
//                    frm.imageWidth,
//                    frm.imageHeight,
//                    0
//            );
//            recorder.setFrameRate(25);
//            recorder.setVideoCodec(13);
//            recorder.setFormat("avi");
//            double quality = 1;
//            recorder.setVideoBitrate((int) (quality * 1024 * 1024));
//
//            recorder.start();
//
//            while (canvas.isVisible() && (frm = streamGrabber.grab()) != null) {
//                canvas.showImage(frm);
//                recorder.record(frm);
//            }
//
//            recorder.stop();
//            canvas.dispose();
//
//
//
            System.out.println("Start cycle");

            while(true) {
                frm = streamGrabber.grabImage();

                frame = converterToMat.convert(frm);

                if((timer + COMPREFACE_INTERVAL) < System.currentTimeMillis()){
                    timer = System.currentTimeMillis();//convert to grayscale
                    cvtColor(frame, gray, COLOR_BGR2GRAY);
                    GaussianBlur(gray, gray, new Size(21, 21), 0);

                    //compute difference between first frame and current frame
                    absdiff(firstFrame, gray, frameDelta);
                    firstFrame = gray.clone();

                    threshold(frameDelta, thresh, 25, 255, THRESH_BINARY);

                    dilate(thresh, thresh, new Mat());
                }

                cnts.clear();
                findContours(thresh, cnts, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

                if(cnts.size() > 0){
                    //евент "замечено движение"
                    for(int i=0; i < cnts.size(); i++) {
                        if(contourArea(cnts.get(i)) < 700) {
                            continue;
                        }
                        Rect r = boundingRect(cnts.get(i));
                        rectangle(frame, r, new Scalar(0, 255, 0, 2));
                    }
                }

                canvas.showImage(converterToMat.convert(frame));



                new NewEvent.createImg(frm);
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
            String imgBase64 = Base64.encodeBase64String(b);

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
