package com.babykangaroo.android.freedomclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

public class SelfieActivity extends AppCompatActivity {

    private FrameLayout imageContainer;
    private FrameLayout toDraw;
    private CameraView cameraView;
    private TextView timeLeft;
    private long daysLeft;
    private String message;
    private FloatingActionButton takePicture;
    private FloatingActionButton retake;
    private ImageView selfieContainer;
    private FloatingActionButton shareFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        long etsDate = PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.ets_date), 0);
        long now = System.currentTimeMillis();
        long millisLeft = etsDate - now;
        daysLeft = (millisLeft/ DateUtils.DAY_IN_MILLIS) + 1;
        message = String.valueOf(daysLeft) + " " + getString(R.string.days_until_freedom);

        selfieContainer = (ImageView) findViewById(R.id.iv_selfie_container);
        timeLeft = (TextView) findViewById(R.id.tv_time_left);
        timeLeft.setText(message);

        shareFab = (FloatingActionButton) findViewById(R.id.fab_share);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePhoto();
            }
        });
        cameraView = (CameraView) findViewById(R.id.cv_selfie);
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                selfieContainer.setImageBitmap(BitmapFactory.decodeByteArray(jpeg,0,jpeg.length));
                showPhoto();
            }
        });
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        imageContainer = (FrameLayout) findViewById(R.id.fl_to_draw_container);
        toDraw = (FrameLayout) findViewById(R.id.fl_to_draw_actual);

        takePicture = (FloatingActionButton) findViewById(R.id.fab_photo);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.captureImage();
            }
        });

        retake = (FloatingActionButton) findViewById(R.id.fab_retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCamera();
            }
        });
        showCamera();
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
        imageContainer.setVisibility(View.INVISIBLE);
        cameraView.setVisibility(View.VISIBLE);
        cameraView.start();
    }

    void showPhoto(){
        imageContainer.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.INVISIBLE);
    }

    void sharePhoto(){

        Bitmap bitmap = ConvertToBitmap(toDraw);String pathofBmp =
                MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap,"title", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent.setType("image/png");
        startActivity(intent);
    }
    protected Bitmap ConvertToBitmap(View layout) {
        Bitmap map = Bitmap.createBitmap(layout.getMeasuredWidth(),
                layout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Canvas canvas = new Canvas(map);
        layout.draw(canvas);
        return map;
    }
}
