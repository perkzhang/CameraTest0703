package com.dreamap.cameratest0703;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_ITEM_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO =2;

    private Button mTakePhoto;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    private void initViews() {
        mImageView = (ImageView) findViewById(R.id.iv_take_photo);
        mTakePhoto = (Button) findViewById(R.id.but_take_photo);
        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent1();
                dispatchTakePictureIntent2();
            }
        });
    }

    private void dispatchTakePictureIntent1() {
        //1、通过发送一个意图捕获照片
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*
        * 使用resolveActivity()：返回能处理该Intent的第一个Activity（即检查有没有能处理这个Intent的Activity）
        * $$这个执行很重要如果调用startActivityForResoult()时候没有应用能处理你的intent,应用将会崩溃，所有
        * $$只有这个结果不为null,使用该intent就是安全的。
        * */
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_ITEM_CAPTURE);
        }

    }

    //2、获取略缩图(data可以传递缩略图)
    /*
    * Android相机会把拍好的照片编码为"缩小"的Bitmap，使用getExtRas()方法将返回的
    * intent传递给onActivityResult()中（对应的key为data），然后将获取的bitmap设置在
    * ImageView中
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(imageBitmap);

        }

    }

    //保存全尺寸照片
    /*思路：
    * 1、如果我们提供一个File对象给Android的相机程序，他会保存这张全尺寸的照片给指定的路径下。
    * 2、我们必须提供存储图片所需要的的含有后缀名形式的文件名。
    *原理：
    * 1、一般而言，用户使用设备相机所拍摄的任何照片都应该被存放在设备的公共外部存储中，
    * 这样就能被所有的应用访问。
    * 2、将DIRECTORY_PICTURE作为参数，传递给getExternalStoragePublicDirectory()方法
    * 可以返回适用于存储公共图片的目录。因此该目录需要进行读写操作人别需要
    *READ_EXTERNAL_STORAG和WRITE_EXTERNAL_STORAGE权限。
    * 3、另外，因为写权限隐含了读权限，所以如果需要外部存储的读权限，那么仅仅需要请求
    * WRITE_EXTERNAL_STORAGE权限即可。
    * Tips：所有存储在getExternalFilesDir()提供的目录中的文件会在用户卸载app时候一并删除
    *4、一旦选定了存储文件的目录，我们还需要设计一个保证文件名不会冲突的命名规则。当然
    *  我们还可以将路径存储在一个成员变量中以备在将来中使用。（以下以日期时间戳做位新照片的文件名）
    *
    * */

    String mCurrentPhotoPath;

    //创建图片目录
    private File creatImageFile() throws IOException {
        //以日期时间戳做位新照片的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" ;

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,/*prefix 前缀*/
                ".jpg",      /*suffix 后缀*/
                storageDir   /*directory 目录*/
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent2(){
        //向camera发出捕获照片的意图
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //确保系统有Camera 来接收这个意图
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                //创建文件夹
                photoFile = creatImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //继续只有当 photoFile 文件被成功创建
            if (photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));//传递路径
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }
        }
    }
}
