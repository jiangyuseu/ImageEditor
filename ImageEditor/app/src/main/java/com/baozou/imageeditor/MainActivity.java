package com.baozou.imageeditor;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baozou.imageeditor.view.StickerView;

public class MainActivity extends AppCompatActivity {

    private StickerView stickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stickerView = (StickerView)findViewById(R.id.stick_view);
        stickerView.setBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
    }
}
