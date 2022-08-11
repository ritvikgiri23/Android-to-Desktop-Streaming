package com.example.Stream_err;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.example.testing_cv.R;
//import com.felhr.usbserial.UsbSerialDevice;
//import com.felhr.usbserial.UsbSerialInterface;

import com.example.Stream_err.R;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER = "com.example.Stream_err.SERVER";
    public static final String PORT = "com.example.Stream_err.PORT";
    public static Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }


        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { Manifest.permission.CAMERA },
                    200);
        }

        Button vid_stream_button = (Button) findViewById(R.id.VideoStream);
        vid_stream_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoActivity();
            }
        });

        Button capture_img_button = (Button) findViewById(R.id.ImageSend);
        capture_img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openImageActivity();
            }
        });

        Button send_img_button = (Button) findViewById(R.id.SendImage);
        send_img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSendImageActivity();
            }
        });
    }

    public void openVideoActivity(){
        EditText server_ = (EditText) findViewById(R.id.IPAddr);
        EditText port_ = (EditText) findViewById(R.id.Port);

        try{
            String server = server_.getText().toString();
            Integer port = Integer.parseInt(port_.getText().toString());

            Intent intent  = new Intent(this, VideoStream.class);

            intent.putExtra(SERVER, server);
            intent.putExtra(PORT, port);

            startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Please Fill Details", Toast.LENGTH_SHORT).show();
        }

//        onResume();
    }

    public void openImageActivity(){
        try{
            EditText server_ = (EditText) findViewById(R.id.IPAddr);
            String server = server_.getText().toString();

            EditText port_ = (EditText) findViewById(R.id.Port);
            Integer port = Integer.parseInt(port_.getText().toString());

            Intent intent  = new Intent(this, CaptureImage.class);

            intent.putExtra(SERVER, server);
            intent.putExtra(PORT, port);
            startActivity(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, "Please Fill Details", Toast.LENGTH_SHORT).show();
        }
    }

    public void openSendImageActivity(){
        try{
            EditText server_ = (EditText) findViewById(R.id.IPAddr);
            String server = server_.getText().toString();

            EditText port_ = (EditText) findViewById(R.id.Port);
            Integer port = Integer.parseInt(port_.getText().toString());

            Intent intent  = new Intent(this, SendImage.class);
            intent.putExtra(SERVER, server);
            intent.putExtra(PORT, port);
            startActivity(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, "Please Fill Details", Toast.LENGTH_SHORT).show();
        }
    }
}