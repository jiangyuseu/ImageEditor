package com.baozou.imageeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class SecondActivity extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        img = (ImageView)findViewById(R.id.img);

        String path = getIntent().getStringExtra("bitmap");
        File mFile=new File(path);
        //若该文件存在
        if (mFile.exists()) {
            Bitmap bitmap=BitmapFactory.decodeFile(path);
            img.setImageBitmap(bitmap);
        }

    }
}
