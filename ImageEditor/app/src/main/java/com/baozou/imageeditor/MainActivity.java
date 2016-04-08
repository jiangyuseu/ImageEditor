package com.baozou.imageeditor;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baozou.imageeditor.view.StickerView;

public class MainActivity extends AppCompatActivity {

    private StickerView stickerView;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stickerView = (StickerView) findViewById(R.id.stick_view);
        stickerView.addBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        btn = (Button) findViewById(R.id.add_stick);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerView.addBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }
        });
    }
}
