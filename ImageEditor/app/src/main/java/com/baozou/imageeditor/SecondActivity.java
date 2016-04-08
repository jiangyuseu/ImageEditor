package com.baozou.imageeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class SecondActivity extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        img = (ImageView)findViewById(R.id.img);
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        img.setImageBitmap(bitmap);

    }
}
