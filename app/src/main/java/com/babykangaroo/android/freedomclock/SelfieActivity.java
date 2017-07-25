package com.babykangaroo.android.freedomclock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

public class SelfieActivity extends AppCompatActivity {

    private FrameLayout imageContainer;
    private FrameLayout landingContainer;
    private CameraView cameraView;
    private TextView smile;
    private TextView timeLeft;
    private long daysLeft;
    private String message;
    private ImageView takePicture;
    private ImageView selfieContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);

        long etsDate = PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.ets_date), 0);
        long now = System.currentTimeMillis();
        long millisLeft = etsDate - now;
        daysLeft = (millisLeft/ DateUtils.DAY_IN_MILLIS) + 1;
        message = String.valueOf(daysLeft) + " " + getString(R.string.days_until_freedom);

        selfieContainer = (ImageView) findViewById(R.id.iv_selfie_container);
        timeLeft = (TextView) findViewById(R.id.tv_time_left);
        timeLeft.setText(message);
        cameraView = (CameraView) findViewById(R.id.cv_selfie);
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                selfieContainer.setImageBitmap(BitmapFactory.decodeByteArray(jpeg,0,jpeg.length));
                showPhoto();
            }
        });
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        imageContainer = (FrameLayout) findViewById(R.id.fl_to_draw);
        landingContainer = (FrameLayout) findViewById(R.id.fl_landing);

        takePicture = (ImageView) findViewById(R.id.iv_photo);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.captureImage();
            }
        });

        smile = (TextView) findViewById(R.id.tv_take_selfie);
        smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCamera();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void showCamera(){
        landingContainer.setVisibility(View.INVISIBLE);
        imageContainer.setVisibility(View.INVISIBLE);
        cameraView.setVisibility(View.VISIBLE);
        cameraView.start();
    }

    void showPhoto(){
        landingContainer.setVisibility(View.INVISIBLE);
        imageContainer.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.INVISIBLE);
    }
}
