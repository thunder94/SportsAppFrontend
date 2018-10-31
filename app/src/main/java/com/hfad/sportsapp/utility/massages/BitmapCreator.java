package com.hfad.sportsapp.utility.massages;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BitmapCreator {

    public final static int SIDE = 512;
    Bitmap bitmap;

    public BitmapCreator(Context context, Intent data) throws FileNotFoundException {
        InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
        bitmap = BitmapFactory.decodeStream(inputStream);
    }

    public Bitmap getBitmap()  {
        crop();
        resize();
        return bitmap;
    }

    public void crop() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (height >= width) {
            int crop = (height - width)/2;
            bitmap = Bitmap.createBitmap(bitmap, 0, crop, width, width);
        } else {
            int crop = (width - height)/2;
            bitmap = Bitmap.createBitmap(bitmap, crop, 0, height, height);
        }
    }

    public void resize() {
        bitmap = Bitmap.createScaledBitmap(bitmap, SIDE, SIDE, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    }
}
