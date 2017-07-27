package com.babykangaroo.android.freedomclock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;


public class SelfieActivity extends AppCompatActivity {

    private FrameLayout imageContainer;
    private FrameLayout toDraw;
    private TextView timeLeft;
    private long daysLeft;
    private String message;
    private FloatingActionButton takePicture;
    private ImageView selfieContainer;
    private FloatingActionButton shareFab;
    private static final int REQUEST_IMAGE_CAPTURE = 9988;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MarineTheme);
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

        shareFab = (FloatingActionButton) findViewById(R.id.fab_share);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePhoto();
            }
        });
        imageContainer = (FrameLayout) findViewById(R.id.fl_to_draw_container);
        toDraw = (FrameLayout) findViewById(R.id.fl_to_draw_actual);

        takePicture = (FloatingActionButton) findViewById(R.id.fab_photo);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }});

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selfieContainer.setImageBitmap(imageBitmap);
        }
    }

    void sharePhoto(){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.app_name));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "shareSelfie");
        MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
