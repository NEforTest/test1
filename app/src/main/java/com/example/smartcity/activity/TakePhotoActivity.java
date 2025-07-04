package com.example.smartcity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartcity.R;

import org.json.JSONException;
import org.json.JSONObject;

import jacoco.jacoco_func;

import java.io.FileNotFoundException;
import java.io.IOException;


import java.io.*;
import java.net.Socket;

//entry
public class TakePhotoActivity extends AppCompatActivity {
    private Button btn_photo;
    private TextView tv;
    private ImageView img;
    private Toolbar toolbar;
    Intent intent = null;

    private jacoco_func jc_f;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        System.out.println("123");
        System.out.println("1234");

        System.out.println("12345");
        super.onCreate(savedInstanceState);
        System.out.println("12345");
        setContentView(R.layout.activity_take_photo);
        System.out.println("12345");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_photo = findViewById(R.id.btn_photo);
        img = findViewById(R.id.img);

        System.out.println("12345");
        System.out.println("12345");
        System.out.println("12345");
        System.out.println("12345");
        /**
         * 解决android7调用照相机后直接奔溃问题
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        checkPermission();
        System.out.println("12345");
        initListener();
        System.out.println("12345");
    }

    public void establCP() throws IOException {
        Log.d("TCP", "Client: Connecting...");
        Socket socket = new Socket("10.0.2.2", 3333);
        String message = "AndroidRes,Where is my Pig (Android)?";
        try {
            Log.d("TCP", "Client: Sending: '" + message + "'");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);
            out.println(message);
        } catch (Exception e) {
            Log.e("TCP", "S: Error", e);
        } finally {
            socket.close();
        }
    }


    public void establishTCP() throws IOException {
        Log.d("TCP", "Client: Connecting...");
        Socket socket = new Socket("10.0.2.2", 3333);
        String message = "AndroidRes,Where is my Pig (Android)?";
        try {
            Log.d("TCP", "Client: Sending: '" + message + "'");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);
            out.println(message);
        } catch (Exception e) {
            Log.e("TCP", "S: Error", e);
        } finally {
            socket.close();
        }
    }

    @Override
    protected void onDestroy() {
        JSONObject json = new JSONObject();
        try {
            // 获取包管理器
            PackageManager packageManager = getPackageManager();

            // 获取当前应用的包名
            String packageName = getPackageName();
            // 通过包名获取包信息
            PackageInfo packageInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageInfo = packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            }else {
                packageInfo = packageManager.getPackageInfo(packageName, 0);
            }

            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String appName = appInfo.loadLabel(packageManager).toString();
            String versionName = packageInfo.versionName;
            String packageName2 = packageInfo.packageName;
            int versionCode = packageInfo.versionCode;

            json.put("appName", appName);
            json.put("versionName", versionName);
            json.put("packageName", packageName);
            json.put("versionCode", versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        jc_f.sendCoverageData(json);

        Log.d("TCP", "Client: Sending: '" + "'");
        super.onDestroy();
    }

    /**
     * 检查拍照权限,防止权限拒绝
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 执行到这里表示没有访问权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(TakePhotoActivity.this, Manifest.permission.CAMERA)) {
                Toast.makeText(TakePhotoActivity.this,"禁止访问",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(TakePhotoActivity.this, new String[]{Manifest.permission.CAMERA}, 200);
            }
        } else {
            takePhoto();
        }
    }

    private void initListener() {
        // 顶部返回
        toolbar.setNavigationIcon(R.mipmap.top_bar_left_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 按钮点击事件,单击弹出AlertDialog对话框
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TakePhotoActivity.this)
                        .setIcon(R.mipmap.picture)
                        .setMessage("插入图片")
                        .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takePhoto();
                            }
                        }).setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chooseFromAlbum();
                    }
                }).create().show();
            }
        });
    }

    /**
     * 获取图片
     */
    public void takePhoto() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 200);   //有数据的返回
    }

    /**
     * 选择相册
     */
    public void chooseFromAlbum() {
        intent = new Intent();
        intent.setType("image/*");   //设定类型为image
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);//选中相片后返回本Activity
    }


    /**
     * 重写onActivityResult方法：将返回的图片数据设置到ImageView上
     *
     * 参数说明：requestCode值：100 为打开系统相册选择相片,requestCode值：200为调用系统相机拍照
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();  //获取数据
            ContentResolver contentResolver = getContentResolver();
            Bitmap bitmap = null;
            Bundle extras = null;
            if (requestCode == 100) {
                try {
                    bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));  //将对象存入Bitmap中
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 200) {
                try {
                    if (uri != null){
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);  // 根据Uri获取Bitmap图片
                    } else{  // 从Bundle里面获取Bitmap图片
                        extras = data.getExtras();
                        bitmap = extras.getParcelable("data");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int imgWidth = bitmap.getWidth();  //获取图片宽度
            int imgHeight = bitmap.getHeight();  // 获取图片高度
            double partion = imgWidth * 1.0 / imgHeight;
            double sqrtLength = Math.sqrt(partion * partion + 1);

            /**
             * 设置图片新的缩略图大小
             */
            double newImgW = 680 * (partion / sqrtLength);
            double newImgH = 680  * (1 / sqrtLength);
            float scaleW = (float) (newImgW / imgWidth);
            float scaleH = (float) (newImgH / imgHeight);
            Matrix mx = new Matrix();

            /**
             * 对原图片进行缩放
             */
            mx.postScale(scaleW, scaleH);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, mx, true);
            bitmap = getBitmapWidth(bitmap);
            img.setImageBitmap(bitmap);
        }
    }

    /**
     * 给图片加边框，并返回边框后的图片
     * @param bitmap
     * @return
     */
    public Bitmap getBitmapWidth(Bitmap bitmap) {
        float frameSize = 0.2f;
        Matrix matrix = new Matrix();
        // 用来做底图
        Bitmap mbitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // 设置底图为画布
        Canvas canvas = new Canvas(mbitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        float scale_x = (bitmap.getWidth() - 2 * frameSize - 2) * 1f / (bitmap.getWidth());
        float scale_y = (bitmap.getHeight() - 2 * frameSize - 2) * 1f / (bitmap.getHeight());
        matrix.reset();
        matrix.postScale(scale_x, scale_y);

        // 减去边框的大小
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        // 绘制底图边框
        canvas.drawRect(new Rect(0, 0, mbitmap.getWidth(), mbitmap.getHeight()),paint);
        // 绘制灰色边框
        paint.setColor(Color.GRAY);
        canvas.drawRect(new Rect((int) (frameSize), (int) (frameSize), mbitmap.getWidth() - (int) (frameSize), mbitmap.getHeight() - (int) (frameSize)), paint);
        canvas.drawBitmap(bitmap, frameSize + 2, frameSize + 2, paint);
        return mbitmap;
    }

}