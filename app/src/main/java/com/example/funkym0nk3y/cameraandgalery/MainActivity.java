package com.example.funkym0nk3y.cameraandgalery;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity implements View.OnClickListener {

  String action,notifiyTitle, notifyDesc;
  Button btnTakePic, btnSearchPic;
  Bitmap bitmap;
  ImageView imageView;
  static int TAKE_PICTURE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    imageView = (ImageView) findViewById(R.id.picture);
    btnTakePic = (Button) findViewById(R.id.btnTakePic);
    btnTakePic.setOnClickListener(this);
    btnSearchPic = (Button) findViewById(R.id.btnSearchPic);
    btnSearchPic.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch ( view.getId() ) {
      case R.id.btnTakePic:
        // Take a pic
        Intent intentTP = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentTP, TAKE_PICTURE);
        action = "TP";
        break;

      case R.id.btnSearchPic:
        // Search a pic
        Intent intentSP = new Intent();
        intentSP.setType("image/*");
        intentSP.setAction(Intent.ACTION_GET_CONTENT);
        intentSP.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intentSP, TAKE_PICTURE);
        action = "SP";
        break;
    }
    this.removeNotification(500);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    InputStream stream = null;

    switch ( action ) {
      case "TP":
        // Take a pic
        if ( requestCode == TAKE_PICTURE && resultCode == RESULT_OK && data != null ) {
          Bundle extras = data.getExtras();
          bitmap = (Bitmap) extras.get("data");
          imageView.setImageBitmap(bitmap);
          Uri notifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
          notifiyTitle = "Camera And Galery";
          notifyDesc = "Hey!  You taked a pic!";

          NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                  .setSmallIcon(R.mipmap.ic_launcher)
                  .setContentTitle(notifiyTitle)
                  .setContentText(notifyDesc)
                  .setSound(notifySound);

          Intent notificationIntent = new Intent(this, MainActivity.class);
          PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                  PendingIntent.FLAG_UPDATE_CURRENT);
          builder.setContentIntent(contentIntent);
          NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          manager.notify(500, builder.build());
        }
        break;
      case "SP":
        // Search a pic
        if ( requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK ) {
          try {
            if ( bitmap != null ) {
              bitmap.recycle();
            }
            stream = getContentResolver().openInputStream(data.getData());
            bitmap = BitmapFactory.decodeStream(stream);

            imageView.setImageBitmap(bitmap);
          } catch ( FileNotFoundException e ) {
            e.printStackTrace();
          }
        }
        break;
    }
  }

  private void removeNotification(int notifyId) {
    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    manager.cancel(notifyId);
  }
}
