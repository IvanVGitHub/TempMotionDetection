package org.example;

import org.bytedeco.javacv.*;

public class VisionTEST {
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
    public VisionTEST() throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        String pathToRec = "C:\\Users\\Иван\\IdeaProjects\\TempMotionDetection\\video.avi";
        String user = "admin";
        String pwd1 = "asDtin38!";
        String address1 = "172.20.7.17";

        VisionTEST.CamData c2 = new CamData(address1, "/Streaming/Channels/101", user, pwd1);
        VisionTEST.CamData current = c2;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(current.getConnectionUrl());
        grabber.setFrameRate(current.framerate);
        grabber.setImageWidth(current.width);
        grabber.setImageHeight(current.height);
        grabber.setOption("rtsp_transport", "tcp");
        grabber.start();
        Frame frame = grabber.grab();
        CanvasFrame canvasFrame = new CanvasFrame("Заголовок");
        canvasFrame.setCanvasSize(frame.imageWidth, frame.imageHeight);

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                pathToRec,
                frame.imageWidth,
                frame.imageHeight,
                0
        );

//        recorder.setFrameRate(25);
        recorder.setVideoCodec(13);
        recorder.setFormat("avi");
//        double quality = 1;
//        recorder.setVideoBitrate((int) (quality * 1024 * 1024));
        recorder.start();

        while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
            canvasFrame.showImage(frame);
            recorder.record(frame);
        }

        recorder.stop();
        canvasFrame.dispose();
    }

    public static void main(String[] args) throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        VisionTEST vision = new VisionTEST();
    }
}
