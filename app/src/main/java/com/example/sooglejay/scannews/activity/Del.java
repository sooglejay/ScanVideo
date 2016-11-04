package com.example.sooglejay.scannews.activity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.sooglejay.scannews.R;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Del extends Activity
{
    /** Called when the activity is first created. */
    private ImageView    image    = null;

    public static Activity activity ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del);
        activity = this;
        image = (ImageView) findViewById(R.id.image);
        image.setImageBitmap(compressBytes());

    }

    public static Bitmap compressBytes() {
        Bitmap bitmap = decodeBitmap();
        int width = bitmap.getWidth();

        int height = bitmap.getHeight();
        Log.e("jwjw",width+"    width");
        Log.e("jwjw",height+"    getHeight");

        //bitmap to string
        String ss = encodeTobase64(bitmap);

        //string to bitmap
        bitmap =  decodeBase64(ss);

         width = bitmap.getWidth();
         height = bitmap.getHeight();
        Log.e("jwjw",width+"  123  width");
        Log.e("jwjw",height+" 123   getHeight");

        //compress to 480
        bitmap = getResizedBitmap(bitmap,480);
        Log.e("jwjw",width+"   456 width");
        Log.e("jwjw",height+" 456   getHeight");
        return bitmap;






    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0,      decodedByte.length);
    }
    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static Bitmap decodeBitmap()
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.ic_action_bulb ,options);

        if (bitmap == null)
        {
            System.out.println("bitmap为空");
        }
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);
        // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
        if (scale <= 0)
        {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.ic_action_bulb ,options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println("缩略图高度：" + h + "宽度:" + w);
        return bitmap;
    }
}