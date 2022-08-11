package com.example.Stream_err;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;


public class VideoStream extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {// OpenCV variables

    private boolean isFrontCam = false;
    private Mat mRgba;
    private CameraBridgeViewBase opencvCamView;
    private BaseLoaderCallback baseLoaderCallback;



    // GUI and other variables
    private static final String TAG = "VideoStream";
    private Button backButton;

    // socket variables
    private boolean lock = true;
    private boolean init_status = false;
    private byte[] arr;

    Socket socket;
    DataOutputStream out;
    String server;
    Integer port;

    // Initializations
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        }
        else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }


        setContentView(R.layout.activity_video_stream);

        Intent intent = getIntent();
        server = intent.getStringExtra(MainActivity.SERVER);
        port = intent.getIntExtra(MainActivity.PORT, 0);

        Thread init = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    int connection_time_out = 5000;
                    Log.d("SOCKET", "trying to connect!");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(server,port),connection_time_out);
                    out = new DataOutputStream(socket.getOutputStream());
                    Log.d("SOCKET", "Connected");
                    init_status = true;
                }
                catch(SocketException e)
                {
                    e.printStackTrace();
                    init_status = false;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    init_status = false;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    init_status = false;
                }
            }
        });

        init.start();
        try {
            init.join();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(!init_status){
            Toast.makeText(this, "Could not connect to Stream_err server", Toast.LENGTH_LONG).show();
            finish();
//            super.onBackPressed();
        }

        consumer.start();

        opencvCamView = findViewById(R.id.camView);
        opencvCamView.setMaxFrameSize(1920, 1080);
        opencvCamView.setVisibility(SurfaceView.VISIBLE);
        opencvCamView.setCvCameraViewListener(this);



        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        opencvCamView.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    Thread consumer = new Thread(new Runnable() {

        @Override
        public void run() {
            try {
                while (true) if (!lock) {              // if lock == 0, its turn of consumer
                    out.write(arr);
                    lock = true;

                    socket.getInetAddress().isReachable(5);
                }

            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    });

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if(lock){
            Mat dst = new Mat();
            // Convert image to RGB
            Imgproc.cvtColor(mRgba,dst,Imgproc.COLOR_BGRA2RGB);
            MatOfByte byteMat = new MatOfByte();

            arr = null;
            Imgcodecs.imencode(".jpg", dst, byteMat);
            arr = byteMat.toArray();

            lock = false;

        }

        return mRgba;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(opencvCamView !=null){
            opencvCamView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){ }
        else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(opencvCamView !=null){
            try {
                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            opencvCamView.disableView();
        }
    }

    public void swapCamera(View view) {
        isFrontCam = !isFrontCam;
        opencvCamView.disableView();
        opencvCamView.setCameraIndex(isFrontCam ? 1 : 0);
        opencvCamView.enableView();
    }
}