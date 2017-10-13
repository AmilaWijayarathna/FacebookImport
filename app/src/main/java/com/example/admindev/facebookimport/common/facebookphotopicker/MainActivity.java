package com.example.admindev.facebookimport.common.facebookphotopicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.axis.photopicker.PhotoPicker;
import com.axis.photopicker.facebook.FbAlbumsFragment;
import com.example.admindev.facebookimport.R;
import com.example.admindev.facebookimport.common.Photo;
import com.koushikdutta.ion.builder.Builders;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button log;
    PhotoPicker photoPicker;
    private int MY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        photoPicker = PhotoPicker.getInstance(this);
        log = (Button) findViewById(R.id.button2);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, FacebookPhotoPickerActivity.class);
//                MainActivity.this.startActivityForResult(intent, MY_REQUEST_CODE);
                photoPicker.openPicker(MainActivity.this);
//                Intent intent = new Intent(MainActivity.this, FbAlbumsFragment.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == MainActivity.this.RESULT_OK) {
            Photo[] photos = FacebookPhotoPicker.getResultPhotos(data);
            for (Photo photo : photos) {
                URL url = photo.getFullURL();

                Toast.makeText(this,""+url, Toast.LENGTH_LONG).show();
                // do something with it
            }
        }
    }
}
